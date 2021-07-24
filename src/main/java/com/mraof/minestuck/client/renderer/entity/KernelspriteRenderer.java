package com.mraof.minestuck.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mraof.minestuck.entity.KernelspriteEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

public class KernelspriteRenderer extends EntityRenderer<KernelspriteEntity>
{
	private int frame;
	private long lastTick;
	
	private static final ResourceLocation BACKGROUND = new ResourceLocation("minestuck","textures/entity/kernelsprite_back.png");
	private static final ResourceLocation COLOR_LAYER = new ResourceLocation("minestuck","textures/entity/kernelsprite_color.png");
	
	public KernelspriteRenderer(EntityRendererManager manager)
	{
		super(manager);
		this.frame = 0;
		this.shadowSize = 0.15F;
		this.shadowOpaque = .75F;
	}
	
	@Override
	public void render(KernelspriteEntity kernelsprite, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		int color = kernelsprite.getColor();
		int r = ((color >> 16) & 255);
		int g = ((color >> 8) & 255);
		int b = (color & 255);
		
		long tick = kernelsprite.getEntityWorld().getGameTime();
		if(lastTick != tick)
		{
			frame = (frame + 1) % 7;
		}
		
		lastTick = tick;
		float width = 0.14285714285714285714285714285714f; // 1/7
		float uPos = frame * width;
		
		matrixStackIn.push();
		matrixStackIn.translate(0.0F, 0.25F, 0.0F);
		matrixStackIn.rotate(this.renderManager.getCameraOrientation());
		MatrixStack.Entry backgroundMatrixstack = matrixStackIn.getLast();
		Matrix4f backgroundPos = backgroundMatrixstack.getMatrix();
		Matrix3f backgroundNormals = backgroundMatrixstack.getNormal();
		
		IVertexBuilder backgroundVertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(BACKGROUND));
		backgroundVertexBuilder.pos(backgroundPos, 0.0F - 0.5F, 0 - 0.25F, 0.0F).color(255, 255, 255, 255).tex(uPos, 1)
				.overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(backgroundNormals, 0.0F, 1.0F, 0.0F).endVertex();
		backgroundVertexBuilder.pos(backgroundPos, 1.0F - 0.5F, 0 - 0.25F, 0.0F).color(255, 255, 255, 255).tex(uPos + width, 1)
				.overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(backgroundNormals, 0.0F, 1.0F, 0.0F).endVertex();
		backgroundVertexBuilder.pos(backgroundPos, 1.0F - 0.5F, 1 - 0.25F, 0.0F).color(255, 255, 255, 255).tex(uPos + width, 0)
				.overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(backgroundNormals, 0.0F, 1.0F, 0.0F).endVertex();
		backgroundVertexBuilder.pos(backgroundPos, 0.0F - 0.5F, 1 - 0.25F, 0.0F).color(255, 255, 255, 255).tex(uPos, 0)
				.overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(backgroundNormals, 0.0F, 1.0F, 0.0F).endVertex();
		matrixStackIn.pop();
		
		matrixStackIn.push();
		matrixStackIn.translate(0.0F, 0.25F, 0.0F);
		matrixStackIn.rotate(this.renderManager.getCameraOrientation());
		MatrixStack.Entry matrixstack = matrixStackIn.getLast();
		Matrix4f colorPos = matrixstack.getMatrix();
		Matrix3f colorNormals = matrixstack.getNormal();
		
		IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(COLOR_LAYER));
		vertexBuilder.pos(colorPos, 0.0F - 0.5F, 0 - 0.25F, 0.0F).color(r, g, b, 255).tex(uPos, 1)
				.overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(colorNormals, 0.0F, 1.0F, 0.0F).endVertex();
		vertexBuilder.pos(colorPos, 1.0F - 0.5F, 0 - 0.25F, 0.0F).color(r, g, b, 255).tex(uPos + width, 1)
				.overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(colorNormals, 0.0F, 1.0F, 0.0F).endVertex();
		vertexBuilder.pos(colorPos, 1.0F - 0.5F, 1 - 0.25F, 0.0F).color(r, g, b, 255).tex(uPos + width, 0)
				.overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(colorNormals, 0.0F, 1.0F, 0.0F).endVertex();
		vertexBuilder.pos(colorPos, 0.0F - 0.5F, 1 - 0.25F, 0.0F).color(r, g, b, 255).tex(uPos, 0)
				.overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(colorNormals, 0.0F, 1.0F, 0.0F).endVertex();
		matrixStackIn.pop();
		
		super.render(kernelsprite, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}
	
	@Override
	public ResourceLocation getEntityTexture(KernelspriteEntity entity)
	{
		return COLOR_LAYER;
	}
}
