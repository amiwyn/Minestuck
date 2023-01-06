package com.mraof.minestuck.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class MeteorEntity extends Projectile implements IAnimatable
{
	private final AnimationFactory factory = new AnimationFactory(this);
	
	public MeteorEntity(EntityType<? extends Projectile> pEntityType, Level pLevel)
	{
		super(pEntityType, pLevel);
	}
	
	public MeteorEntity(Level pLevel, Vec3 position)
	{
		super(MSEntityTypes.METEOR.get(), pLevel);
		this.setPos(position);
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
	
	@Override
	protected void defineSynchedData()
	{
	}
}
