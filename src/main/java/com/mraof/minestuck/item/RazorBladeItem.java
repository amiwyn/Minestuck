package com.mraof.minestuck.item;

import com.mraof.minestuck.entity.MeteorEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class RazorBladeItem extends Item
{
	
	public RazorBladeItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		if(attacker instanceof Player player)
		{
			if(!player.isCreative())
			{
				ItemEntity razor = new ItemEntity(attacker.level, attacker.getX(), attacker.getY(), attacker.getZ(), stack.copy());
				if(!attacker.level.isClientSide)
				{
					razor.getItem().setCount(1);
					razor.setPickUpDelay(40);
					attacker.level.addFreshEntity(razor);
					stack.shrink(1);
					Component message = new TranslatableComponent("While you handle the razor blade, you accidentally cut yourself and drop it.");
					attacker.sendMessage(message, Util.NIL_UUID);
				}
				attacker.setHealth(attacker.getHealth() - 1);
				return true;
			}
		}
		return super.hurtEnemy(stack, target, attacker);
	}
	
	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving)
	{
		if(entityLiving instanceof Player player)
		{
			if(!player.isCreative())
			{
				ItemEntity razor = new ItemEntity(entityLiving.level, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), stack.copy());
				if(!entityLiving.level.isClientSide)
				{
					razor.getItem().setCount(1);
					razor.setPickUpDelay(40);
					entityLiving.level.addFreshEntity(razor);
					stack.shrink(1);
					Component message = new TranslatableComponent("While you handle the razor blade, you accidentally cut yourself and drop it.");
					entityLiving.sendMessage(message, Util.NIL_UUID);
				}
				entityLiving.hurt(DamageSource.GENERIC, 1);
			}
		}
		return super.mineBlock(stack, level, state, pos, entityLiving);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn)
	{
		if(!level.isClientSide)
		{
			Random rdn = new Random();
			MeteorEntity meteor = new MeteorEntity(level, playerIn.position(), rdn.nextInt(12) + 1);
			meteor.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.0F, 0.1F, 1.0F);
			level.addFreshEntity(meteor);
		}
		
		return super.use(level, playerIn, handIn);
	}
}
