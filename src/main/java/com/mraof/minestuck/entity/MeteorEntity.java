package com.mraof.minestuck.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class MeteorEntity extends Projectile implements IAnimatable, IEntityAdditionalSpawnData
{
	private int size = 1;
	
	private final AnimationFactory factory = new AnimationFactory(this);
	
	public MeteorEntity(EntityType<? extends Projectile> pEntityType, Level pLevel)
	{
		super(pEntityType, pLevel);
	}
	
	public MeteorEntity(Level pLevel, Vec3 position, int size)
	{
		super(MSEntityTypes.METEOR.get(), pLevel);
		this.setPos(position);
		this.size = size;
	}
	
	public void tick()
	{
		Entity owner = this.getOwner();
		if(this.level.isClientSide || (owner == null || !owner.isRemoved()) && this.level.hasChunkAt(this.blockPosition()))
		{
			super.tick();
			
			HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
			if(hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult))
			{
				this.onHit(hitresult);
			}
			
			this.checkInsideBlocks();
			Vec3 delta = this.getDeltaMovement();
			double x = this.getX() + delta.x;
			double y = this.getY() + delta.y;
			double z = this.getZ() + delta.z;
			
			level.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, x - (size * delta.x), y - (size * delta.y), z - (size * delta.z), 0.0D, 0.0D, 0.0D);
			
			this.setPos(x, y, z);
		} else
		{
			this.discard();
		}
	}
	
	protected void onHit(HitResult result)
	{
		super.onHit(result);
		if(!level.isClientSide)
		{
			discard();
		}
	}
	
	@Override
	public void registerControllers(AnimationData data)
	{
		data.addAnimationController(new AnimationController<>(this, "controller", 0, event -> {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.meteor.smoke", true));
			return PlayState.CONTINUE;
		}));
	}
	
	@Override
	public AnimationFactory getFactory()
	{
		return factory;
	}
	
	public void addAdditionalSaveData(CompoundTag pCompound)
	{
		super.addAdditionalSaveData(pCompound);
		pCompound.putInt("Size", size);
	}
	
	public void readAdditionalSaveData(CompoundTag pCompound)
	{
		super.readAdditionalSaveData(pCompound);
		if(pCompound.contains("Size"))
		{
			this.size = pCompound.getInt("Size");
		}
	}
	
	@Override
	public EntityDimensions getDimensions(Pose poseIn)
	{
		return super.getDimensions(poseIn).scale(size * 0.3f);
	}
	
	public int getSize()
	{
		return size;
	}
	
	@Override
	protected void defineSynchedData()
	{
	}
	
	@Override
	public void writeSpawnData(FriendlyByteBuf buffer)
	{
		buffer.writeInt(size);
	}
	
	@Override
	public void readSpawnData(FriendlyByteBuf data)
	{
		size = data.readInt();
	}
	
	@Override
	public Packet<?> getAddEntityPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
