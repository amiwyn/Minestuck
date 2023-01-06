package com.mraof.minestuck.client.model.entity;

import com.mraof.minestuck.entity.MeteorEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MeteorModel extends AnimatedGeoModel<MeteorEntity>
{
	public static ResourceLocation Texture = new ResourceLocation("minestuck", "textures/entity/meteor.png");
	
	@Override
	public ResourceLocation getModelLocation(MeteorEntity object)
	{
		return new ResourceLocation("minestuck", "geo/entity/meteor.geo.json");
	}
	
	@Override
	public ResourceLocation getTextureLocation(MeteorEntity object)
	{
		return Texture;
	}
	
	@Override
	public ResourceLocation getAnimationFileLocation(MeteorEntity entity)
	{
		return new ResourceLocation("minestuck", "animations/entity/meteor.animation.json");
	}
}