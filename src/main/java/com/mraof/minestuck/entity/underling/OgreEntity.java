package com.mraof.minestuck.entity.underling;

import com.mraof.minestuck.item.crafting.alchemy.GristHelper;
import com.mraof.minestuck.item.crafting.alchemy.GristSet;
import com.mraof.minestuck.item.crafting.alchemy.GristType;
import com.mraof.minestuck.player.Echeladder;
import com.mraof.minestuck.util.MSSoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

//Makes non-stop ogre puns
public class OgreEntity extends HeavyUnderlingEntity implements IAnimatable
{
	private AnimationFactory factory = new AnimationFactory(this);

	public OgreEntity(EntityType<? extends OgreEntity> type, World world)
	{
		super(type, world, 3, 18, 20);
		this.maxUpStep = 1.0F;
	}
	
	public static AttributeModifierMap.MutableAttribute ogreAttributes()
	{
		return UnderlingEntity.underlingAttributes().add(Attributes.MAX_HEALTH, 50)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.4).add(Attributes.MOVEMENT_SPEED, 0.22)
				.add(Attributes.ATTACK_DAMAGE, 6);
	}

	protected SoundEvent getAmbientSound()
	{
		return MSSoundEvents.ENTITY_OGRE_AMBIENT;
	}
	
	protected SoundEvent getDeathSound()
	{
		return MSSoundEvents.ENTITY_OGRE_DEATH;
	}
	
	protected SoundEvent getHurtSound(DamageSource damageSourceIn)
	{
		return MSSoundEvents.ENTITY_OGRE_HURT;
	}
	
	@Override
	public GristSet getGristSpoils()
	{
		return GristHelper.generateUnderlingGristDrops(this, damageMap, 4);
	}
	
	@Override
	protected int getVitalityGel()
	{
		return random.nextInt(3) + 3;
	}
	
	@Override
	protected void onGristTypeUpdated(GristType type)
	{
		super.onGristTypeUpdated(type);
		applyGristModifier(Attributes.MAX_HEALTH, 13 * type.getPower(), AttributeModifier.Operation.ADDITION);
		applyGristModifier(Attributes.ATTACK_DAMAGE, 2.1 * type.getPower(), AttributeModifier.Operation.ADDITION);
		this.xpReward = (int) (5 * type.getPower() + 4);
	}
	
	@Override
	public void die(DamageSource cause)
	{
		super.die(cause);
		Entity entity = cause.getEntity();
		if(this.dead && !this.level.isClientSide)
		{
			computePlayerProgress((int) (15 + 2.2 * getGristType().getPower())); //most ogres stop giving xp at rung 18
			firstKillBonus(entity, (byte) (Echeladder.UNDERLING_BONUS_OFFSET + 1));
		}
	}

	@Override
	public void registerControllers(AnimationData data) {
		AnimationController<OgreEntity> armsController = new AnimationController<>(this, "walkArmsAnimation", 0, this::walkArmsAnimation);
		armsController.setAnimationSpeed(0.3);
		data.addAnimationController(armsController);

		AnimationController<OgreEntity> walkController = new AnimationController<>(this, "walkAnimation", 0, this::walkAnimation);
		walkController.setAnimationSpeed(0.3);
		data.addAnimationController(walkController);

		AnimationController<OgreEntity> swingController = new AnimationController<>(this, "swingAnimation", 0, this::swingAnimation);
		swingController.setAnimationSpeed(0.5);
		data.addAnimationController(swingController);
	}

	private <E extends IAnimatable> PlayState walkAnimation(AnimationEvent<E> event) {
		if (event.isMoving()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ogre.walk", true));
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	private <E extends IAnimatable> PlayState walkArmsAnimation(AnimationEvent<E> event) {
		if (event.isMoving() && !isPerformingHeavyAttack()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ogre.walkarms", true));
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	private <E extends IAnimatable> PlayState swingAnimation(AnimationEvent<E> event) {
		if (isPerformingHeavyAttack()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ogre.punch", false));
			return PlayState.CONTINUE;
		}
		event.getController().markNeedsReload();
		return PlayState.STOP;
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}