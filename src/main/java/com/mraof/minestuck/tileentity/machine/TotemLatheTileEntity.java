package com.mraof.minestuck.tileentity.machine;

import com.mraof.minestuck.block.EnumDowelType;
import com.mraof.minestuck.block.MSBlocks;
import com.mraof.minestuck.block.machine.TotemLatheBlock;
import com.mraof.minestuck.item.MSItems;
import com.mraof.minestuck.item.crafting.alchemy.AlchemyHelper;
import com.mraof.minestuck.item.crafting.alchemy.CombinationMode;
import com.mraof.minestuck.item.crafting.alchemy.CombinationRecipe;
import com.mraof.minestuck.item.crafting.alchemy.CombinerWrapper;
import com.mraof.minestuck.tileentity.MSTileEntityTypes;
import com.mraof.minestuck.util.ColorHandler;
import com.mraof.minestuck.util.Debug;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

public class TotemLatheTileEntity extends TileEntity implements ITickableTileEntity
{
	private boolean isProcessing;
	private int animationticks;
	private boolean broken = false;
	//two cards so that we can preform the && alchemy operation
	private ItemStack card1 = ItemStack.EMPTY;
	private ItemStack card2 = ItemStack.EMPTY;
	
	public TotemLatheTileEntity()
	{
		super(MSTileEntityTypes.TOTEM_LATHE.get());
	}
	
	private boolean tryAddCard(ItemStack stack)
	{
		if(!isBroken() && stack.getItem() == MSItems.CAPTCHA_CARD)
		{
			if(card1.isEmpty())
				card1 = stack;
			else if(card2.isEmpty())
				card2 = stack;
			else return false;
			
			updateState();
			return true;
		}
		return false;
	}
	
	private ItemStack tryTakeCard()
	{
		ItemStack card = ItemStack.EMPTY;
		if(!card2.isEmpty())
		{
			card = card2;
			card2 = ItemStack.EMPTY;
		} else if(!card1.isEmpty())
		{
			card = card1;
			card1 = ItemStack.EMPTY;
		}
		if(!card.isEmpty())
			updateState();
		return card;
	}
	
	private void updateState()
	{
		int worldCount = getBlockState().getValue(TotemLatheBlock.Slot.COUNT);
		int actualCount = getActualCardCount();
		if(worldCount != actualCount)
		{
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(TotemLatheBlock.Slot.COUNT, actualCount));
		}
	}
	
	private int getActualCardCount()
	{
		if(!card2.isEmpty())
			return 2;
		else if(!card1.isEmpty())
			return 1;
		else return 0;
	}
	
	public boolean isBroken()
	{
		return broken;
	}
	
	public void setBroken()
	{
		broken = true;
	}
	
	public boolean setDowel(ItemStack stack)
	{
		if(level == null)
			return false;
		Direction facing = getFacing();
		BlockPos pos = MSBlocks.TOTEM_LATHE.getDowelPos(getBlockPos(), getBlockState());
		BlockState state = level.getBlockState(pos);
		BlockState newState = MSBlocks.TOTEM_LATHE.DOWEL_ROD.get().defaultBlockState().setValue(TotemLatheBlock.FACING, facing).setValue(TotemLatheBlock.DowelRod.DOWEL, EnumDowelType.getForDowel(stack));
		if(isValidDowelRod(state, facing))
		{
			TileEntity te = level.getBlockEntity(pos);
			if(!(te instanceof TotemLatheDowelTileEntity))
			{
				te = new TotemLatheDowelTileEntity();
				level.setBlockEntity(pos, te);
			}
			TotemLatheDowelTileEntity teItem = (TotemLatheDowelTileEntity) te;
			teItem.setStack(stack);
			//updating the dowel tile entity
			if(!state.equals(newState))
				level.setBlockAndUpdate(pos, newState);
			else level.sendBlockUpdated(pos, state, state, Constants.BlockFlags.BLOCK_UPDATE);
			
			//updating the machine's tile entity
			isProcessing = false;
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
			return true;
		}
		return false;
	}
	
	public ItemStack getDowel()
	{
		BlockPos pos = MSBlocks.TOTEM_LATHE.getDowelPos(getBlockPos(), getBlockState());
		if(isValidDowelRod(level.getBlockState(pos), getFacing()))
		{
			TileEntity te = level.getBlockEntity(pos);
			if(te instanceof TotemLatheDowelTileEntity)
			{
				return ((TotemLatheDowelTileEntity) te).getStack();
			}
		}
		return ItemStack.EMPTY;
		
	}
	
	private boolean isValidDowelRod(BlockState state, Direction facing)
	{
		return state.getBlock() == MSBlocks.TOTEM_LATHE.DOWEL_ROD.get() && state.getValue(TotemLatheBlock.FACING) == facing;
	}
	
	public Direction getFacing()
	{
		return getBlockState().getValue(TotemLatheBlock.FACING);
	}
	
	public void onRightClick(PlayerEntity player, BlockState clickedState)
	{
		boolean working = isUseable(clickedState);
		
		//if they have clicked on the part that holds the captcha cards
		if(clickedState.getBlock() instanceof TotemLatheBlock.Slot)
			handleSlotClick(player, working);
		
		//if they have clicked the dowel block
		if(clickedState.getBlock() == MSBlocks.TOTEM_LATHE.DOWEL_ROD.get())
			handleDowelClick(player, working);
		
		//if they have clicked on the lever
		if(working && clickedState.getBlock() == MSBlocks.TOTEM_LATHE.TOP.get())
		{
			//carve the dowel.
			if(!getDowel().isEmpty() && !AlchemyHelper.hasDecodedItem(getDowel()) && (!card1.isEmpty() || !card2.isEmpty()) && level != null)
			{
				isProcessing = true;
				animationticks = 25;
				level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
			}
		}
	}
	
	private void handleSlotClick(PlayerEntity player, boolean isWorking)
	{
		ItemStack heldStack = player.getMainHandItem();
		ItemStack card = heldStack.copy().split(1);
		if(tryAddCard(card))
		{
			heldStack.shrink(1);
		} else
		{
			card = tryTakeCard();
			if(!card.isEmpty())
			{
				if(player.getMainHandItem().isEmpty())
					player.setItemInHand(Hand.MAIN_HAND, card);
				else if(!player.inventory.add(card))
					dropItem(false, getBlockPos(), card);
				else player.inventoryMenu.broadcastChanges();
			}
		}
	}
	
	private void handleDowelClick(PlayerEntity player, boolean isWorking)
	{
		ItemStack heldStack = player.getMainHandItem();
		ItemStack dowel = getDowel();
		if(dowel.isEmpty())
		{
			if(isWorking && heldStack.getItem() == MSBlocks.CRUXITE_DOWEL.asItem())
			{
				ItemStack copy = heldStack.copy();
				copy.setCount(1);
				if(setDowel(copy))
				{
					heldStack.shrink(1);
				}
			}
		} else
		{
			if(player.getMainHandItem().isEmpty())
				player.setItemInHand(Hand.MAIN_HAND, dowel);
			else if(!player.inventory.add(dowel))
				dropItem(true, getBlockPos().above().relative(getFacing().getCounterClockWise(), 2), dowel);
			else player.inventoryMenu.broadcastChanges();
			setDowel(ItemStack.EMPTY);
		}
	}
	
	private boolean isUseable(BlockState state)
	{
		BlockState currentState = getLevel().getBlockState(getBlockPos());
		if(!isBroken())
		{
			checkStates();
			if(isBroken())
				Debug.warnf("Failed to notice a block being broken or misplaced at the totem lathe at %s", getBlockPos());
		}
		
		if(!state.getValue(TotemLatheBlock.FACING).equals(currentState.getValue(TotemLatheBlock.FACING)))
			return false;
		return !isBroken();
	}
	
	public void checkStates()
	{
		if(isBroken())
			return;
		
		if(MSBlocks.TOTEM_LATHE.isInvalidFromSlot(level, getBlockPos()))
			setBroken();
	}
	
	private void dropItem(boolean inBlock, BlockPos pos, ItemStack stack)
	{
		Direction direction = getFacing();
		BlockPos dropPos;
		if(inBlock)
			dropPos = pos;
		else if(!Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite()))
			dropPos = pos.relative(direction);
		else dropPos = pos;
		
		InventoryHelper.dropItemStack(level, dropPos.getX(), dropPos.getY(), dropPos.getZ(), stack);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt)
	{
		super.load(state, nbt);
		broken = nbt.getBoolean("broken");
		card1 = ItemStack.of(nbt.getCompound("card1"));
		card2 = ItemStack.of(nbt.getCompound("card2"));
		isProcessing = nbt.getBoolean("isProcessing");
		if(card1.isEmpty() && !card2.isEmpty())
		{
			card1 = card2;
			card2 = ItemStack.EMPTY;
		}
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound)
	{
		super.save(compound);
		compound.putBoolean("broken", broken);
		compound.put("card1", card1.save(new CompoundNBT()));
		compound.put("card2", card2.save(new CompoundNBT()));
		compound.putBoolean("isProcessing", isProcessing);
		return compound;
	}
	
	public void processContents()
	{
		ItemStack dowel = getDowel();
		ItemStack output;
		if(!dowel.isEmpty() && !AlchemyHelper.hasDecodedItem(dowel) && (!card1.isEmpty() || !card2.isEmpty()))
		{
			if(!card1.isEmpty() && !card2.isEmpty())
				if(!AlchemyHelper.isPunchedCard(card1) || !AlchemyHelper.isPunchedCard(card2))
					output = new ItemStack(MSBlocks.GENERIC_OBJECT);
				else
					output = CombinationRecipe.findResult(new CombinerWrapper(card1, card2, CombinationMode.AND), level);
			else
			{
				ItemStack input = card1.isEmpty() ? card2 : card1;
				if(!AlchemyHelper.isPunchedCard(input))
					output = new ItemStack(MSBlocks.GENERIC_OBJECT);
				else output = AlchemyHelper.getDecodedItem(input);
			}
			
			if(!output.isEmpty())
			{
				ItemStack outputDowel = output.getItem().equals(MSBlocks.GENERIC_OBJECT.asItem()) ? new ItemStack(MSBlocks.CRUXITE_DOWEL) : AlchemyHelper.createEncodedItem(output, false);
				ColorHandler.setColor(outputDowel, ColorHandler.getColorFromStack(dowel));
				setDowel(outputDowel);
			}
		}
	}
	
	public ItemStack getCard1()
	{
		return card1;
	}
	
	public ItemStack getCard2()
	{
		return card2;
	}

	@Override
	public CompoundNBT getUpdateTag()
	{
		return save(new CompoundNBT());
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket()
	{
		return new SUpdateTileEntityPacket(this.worldPosition, 0, getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
	{
		handleUpdateTag(getBlockState(), pkt.getTag());
	}
	
	@Override
	public void tick()
	{
		if(animationticks > 0)
		{
			animationticks--;
			if(animationticks <= 0)
			{
				processContents();
			}
		}
	}
	
	public boolean isProcessing()
	{
		return isProcessing;
	}
}