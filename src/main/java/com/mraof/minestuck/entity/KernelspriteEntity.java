package com.mraof.minestuck.entity;

import com.mraof.minestuck.MinestuckConfig;
import com.mraof.minestuck.skaianet.SburbHandler;
import com.mraof.minestuck.util.ColorHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class KernelspriteEntity extends CreatureEntity
{
	protected static final DataParameter<Integer> COLOR = EntityDataManager.createKey(KernelspriteEntity.class, DataSerializers.VARINT);
	
	private BlockPos homePos;
	private UUID ownerID;
	
	public KernelspriteEntity(EntityType<? extends KernelspriteEntity> type, World world)
	{
		super(type, world);
		this.moveController = new FlyingMovementController(this, 20, true);
	}
	
	@Override
	protected void registerData()
	{
		super.registerData();
		this.dataManager.register(COLOR, 0);
	}
	
	@Override
	public void writeAdditional(CompoundNBT compound)
	{
		super.writeAdditional(compound);
		compound.putInt("Color", this.getColor());
		compound.putString("Owner", this.ownerID.toString());
		compound.put("HomePosition", NBTUtil.writeBlockPos(this.homePos));
	}
	
	@Override
	public void readAdditional(CompoundNBT compound)
	{
		super.readAdditional(compound);
		this.dataManager.set(COLOR, compound.getInt("Color"));
		this.ownerID = UUID.fromString(compound.getString("Owner"));
		
		BlockPos homePos = NBTUtil.readBlockPos(compound.getCompound("HomePosition"));
		ServerPlayerEntity player = (ServerPlayerEntity) this.world.getPlayerByUuid(this.ownerID);
		if(player != null && SburbHandler.hasEntered(player))
		{
			homePos = NBTUtil.readBlockPos(compound.getCompound("HomePosition"));
		}
		
		setHomePosAndDistance(NBTUtil.readBlockPos(compound.getCompound("HomePosition")), MinestuckConfig.SERVER.artifactRange.get()); //travels as far as your entry platform is wide
		this.homePos = homePos;
	}
	
	@Override
	protected boolean processInteract(PlayerEntity player, Hand hand)
	{
		return super.processInteract(player, hand);
	}
	
	public int getColor()
	{
		return this.dataManager.get(COLOR);
	}
	
	public ServerPlayerEntity getServerPlayerEntityFromUUID()
	{
		return this.getServer().getPlayerList().getPlayerByUUID(ownerID);
	}
	
	public void setOwner(PlayerEntity player)
	{
		this.ownerID = player.getUniqueID();
		this.homePos = player.getPosition();
		setHomePosAndDistance(this.homePos, MinestuckConfig.SERVER.artifactRange.get());
		int color = ColorHandler.getColorForPlayer((ServerPlayerEntity) player);
		this.dataManager.set(COLOR, color);
	}
	
	public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn)
	{
		return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
	}
	
	protected void registerGoals()
	{
		this.goalSelector.addGoal(1, new KernelspriteEntity.HealOwnerGoal());
		this.goalSelector.addGoal(2, new KernelspriteEntity.WanderGoal());
		this.goalSelector.addGoal(3, new SwimGoal(this));
	}
	
	protected void registerAttributes()
	{
		super.registerAttributes();
		this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
		this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue((double) 4F);
	}
	
	protected PathNavigator createNavigator(World worldIn)
	{
		FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn);
		flyingpathnavigator.setCanOpenDoors(false);
		flyingpathnavigator.setCanSwim(false);
		flyingpathnavigator.setCanEnterDoors(true);
		return flyingpathnavigator;
	}
	
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
	{
		return this.isChild() ? sizeIn.height * 0.5F : sizeIn.height * 0.5F;
	}
	
	public boolean onLivingFall(float distance, float damageMultiplier)
	{
		return false;
	}
	
	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos)
	{
	}
	
	@Override
	public boolean canDespawn(double distanceToClosestPlayer)
	{
		return false;
	}
	
	class WanderGoal extends Goal
	{
		WanderGoal()
		{
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}
		
		public boolean shouldExecute()
		{
			return KernelspriteEntity.this.navigator.noPath() && KernelspriteEntity.this.rand.nextInt(10) == 0;
		}
		
		public boolean shouldContinueExecuting()
		{
			return !KernelspriteEntity.this.navigator.noPath();
		}
		
		public void startExecuting()
		{
			Vec3d vec3d = this.getRandomLocation();
			if(vec3d != null)
			{
				KernelspriteEntity.this.navigator.setPath(KernelspriteEntity.this.navigator.getPathToPos(new BlockPos(vec3d), 1), 1.0D);
			}
		}
		
		@Nullable
		private Vec3d getRandomLocation()
		{
			Vec3d vec3d = KernelspriteEntity.this.getLook(0.0F);
			Vec3d vec3d2 = RandomPositionGenerator.findAirTarget(KernelspriteEntity.this, 8, 7, vec3d, ((float) Math.PI / 2F), 2, 1);
			return vec3d2 != null ? vec3d2 : RandomPositionGenerator.findGroundTarget(KernelspriteEntity.this, 8, 4, -2, vec3d, (double) ((float) Math.PI / 2F));
		}
	}
	
	class HealOwnerGoal extends Goal
	{
		HealOwnerGoal()
		{
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
			this.setMutexFlags(EnumSet.of(Flag.LOOK));
		}
		
		public boolean shouldExecute()
		{
			ServerPlayerEntity ownerPlayer = KernelspriteEntity.this.getServerPlayerEntityFromUUID();
			if(ownerPlayer != null)
			{
				return ownerPlayer.getHealth() / ownerPlayer.getMaxHealth() <= 0.5 && ownerPlayer.getPositionVec().distanceTo(KernelspriteEntity.this.getPositionVec()) <= 32; //checks if players health is at half or less and if the player is nearby
			}
			return false;
		}
		
		public boolean shouldContinueExecuting()
		{
			ServerPlayerEntity ownerPlayer = KernelspriteEntity.this.getServerPlayerEntityFromUUID();
			if(ownerPlayer != null)
			{
				if(ownerPlayer.getHealth() / ownerPlayer.getMaxHealth() <= 0.5 && ownerPlayer.getPositionVec().squareDistanceTo(KernelspriteEntity.this.getPositionVec()) <= 10)
				{
					ownerPlayer.addPotionEffect(new EffectInstance(Effects.INSTANT_HEALTH, 1, 2));
					return false;
				}
			}
			
			return !KernelspriteEntity.this.navigator.noPath();
		}
		
		public void startExecuting()
		{
			ServerPlayerEntity ownerPlayer = KernelspriteEntity.this.getServerPlayerEntityFromUUID();
			if(ownerPlayer != null)
			{
				KernelspriteEntity.this.navigator.setPath(KernelspriteEntity.this.navigator.getPathToPos(new BlockPos(ownerPlayer.getPositionVec()), 1), 0.8D);
			}
		}
	}
}