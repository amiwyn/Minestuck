package com.mraof.minestuck.block;

import com.mraof.minestuck.MinestuckConfig;
import com.mraof.minestuck.entity.KernelspriteEntity;
import com.mraof.minestuck.entity.MSEntityTypes;
import com.mraof.minestuck.world.storage.PlayerData;
import com.mraof.minestuck.world.storage.PlayerSavedData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CruxtruderLidBlock extends Block
{
	public static final VoxelShape SHAPE = Block.makeCuboidShape(2, 0, 2, 14, 5, 14);
	
	public CruxtruderLidBlock(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public PushReaction getPushReaction(BlockState state)
	{
		return PushReaction.DESTROY;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}
	
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
	{
		if(!worldIn.isRemote)
		{
			PlayerData playerData = PlayerSavedData.getData((ServerPlayerEntity) player);
			
			if((!playerData.hasKernelSprite() || player.isCreative()) && MinestuckConfig.SERVER.kernelSpriteSpawns.get())
			{
				KernelspriteEntity kernelspriteEntity = MSEntityTypes.KERNELSPRITE.create(worldIn);
				kernelspriteEntity.setLocationAndAngles((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, 0.0F, 0.0F);
				kernelspriteEntity.setOwner(player);
				worldIn.addEntity(kernelspriteEntity);
				playerData.setHasKernelSprite(true);
			}
		}
	}
}
