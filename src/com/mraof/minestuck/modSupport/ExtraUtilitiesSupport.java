package com.mraof.minestuck.modSupport;

import com.mraof.minestuck.util.CombinationRegistry;
import com.mraof.minestuck.util.GristRegistry;
import com.mraof.minestuck.util.GristSet;
import com.mraof.minestuck.util.GristType;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ExtraUtilitiesSupport extends ModSupport
{
	
	@Override
	public void registerRecipes()
	{
		Item enderLily = Item.REGISTRY.getObject(new ResourceLocation("extrautils2", "enderlilly"));
		
		if(enderLily != null)
		{
			GristRegistry.addGristConversion(new ItemStack(enderLily), new GristSet(new GristType[]{GristType.Uranium, GristType.Iodine}, new int[]{24, 6}));
			CombinationRegistry.addCombination(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.ENDER_PEARL), CombinationRegistry.Mode.MODE_OR,  new ItemStack(enderLily));
			CombinationRegistry.addCombination(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.ENDER_EYE), CombinationRegistry.Mode.MODE_OR,  new ItemStack(enderLily));	//Might as well do this too
		}
	}
}
