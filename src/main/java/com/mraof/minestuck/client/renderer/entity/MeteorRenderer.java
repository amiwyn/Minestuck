package com.mraof.minestuck.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mraof.minestuck.client.model.entity.MeteorModel;
import com.mraof.minestuck.entity.MeteorEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class MeteorRenderer extends GeoProjectilesRenderer<MeteorEntity>
{
	private static final int textureSize = 256;
	private static final int frameCount = 3;
	private static final float halfPi = (float) Math.PI / 2;
	
	public MeteorRenderer(EntityRendererProvider.Context context)
	{
		super(context, new MeteorModel());
	}
	
	@Override
	public void renderEarly(MeteorEntity animatable, PoseStack stackIn, float partialTicks, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		stackIn.translate(0.3, -0.5, 0);
		stackIn.mulPose(Vector3f.YP.rotationDegrees(-90));
		super.renderEarly(animatable, stackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
	
	@Override
	public void render(MeteorEntity meteor, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
	{
		int frame = (int) (meteor.getLevel().getGameTime() % frameCount);
		
		matrixStackIn.pushPose();
//		Vec3 origin = new Vec3(-1, 0, 0);
//		Vec3 direction = meteor.getDeltaMovement().normalize();
//		float angle = (float) (1 + direction.dot(origin));
//		Vec3 rotationAxis = direction.cross(origin);
//		Quaternion rotation = new Quaternion((float) rotationAxis.x, (float) rotationAxis.y, (float) rotationAxis.z, -angle);
//		rotation.normalize();
//		matrixStackIn.mulPose(rotation);
		
		Vec3 direction = meteor.getDeltaMovement().normalize();
		float yaw = (float) Math.atan2(-direction.z, -direction.x);
		float pitch = (float) Math.acos(Vector3f.YP.dot(new Vector3f(direction)));
		matrixStackIn.mulPose(Vector3f.YP.rotation(-yaw));
		matrixStackIn.mulPose(Vector3f.ZP.rotation(pitch - halfPi));
		
		renderSideFlamesTexture(meteor, matrixStackIn, bufferIn, packedLightIn, frame);
		renderFrontFlamesTexture(meteor, matrixStackIn, bufferIn, packedLightIn, frame);
		matrixStackIn.popPose();
		
		super.render(meteor, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}
	
	private void renderSideFlamesTexture(MeteorEntity meteor, PoseStack stack, MultiBufferSource buffer, int light, int frame)
	{
		stack.pushPose();
		Vec3 direction = meteor.getDeltaMovement();
		Vec3 cameraPos = entityRenderDispatcher.camera.getPosition();

		float side2 = Math.signum(entityRenderDispatcher.camera.getYRot());
		float side3 = (float) Math.signum(direction.x * (cameraPos.z - meteor.position().z) - direction.z * (cameraPos.x - meteor.position().x));
		stack.mulPose(Vector3f.XP.rotationDegrees(side2 * -side3 * entityRenderDispatcher.camera.getXRot()));
		
		stack.scale(3, 3, 3);
		
		final float height = 48f / textureSize;
		final float width = 128f / textureSize;
		final Vec2 start = new Vec2(48f / textureSize, 80f / textureSize);
		float uPos = frame * height;
		
		PoseStack.Pose pose = stack.last();
		Matrix4f pos = pose.pose();
		Matrix3f normals = pose.normal();
		
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(MeteorModel.Texture));
		consumer.vertex(pos, -0.4F, -0.5F, 0.0F).color(255, 255, 255, 255).uv(start.x, start.y + height + uPos)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normals, 0.0F, 1.0F, 0.0F).endVertex();
		consumer.vertex(pos, 2.2666F, -0.5F, 0.0F).color(255, 255, 255, 255).uv(start.x + width, start.y + height + uPos)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normals, 0.0F, 1.0F, 0.0F).endVertex();
		consumer.vertex(pos, 2.2666F, 0.5F, 0.0F).color(255, 255, 255, 255).uv(start.x + width, start.y + uPos)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normals, 0.0F, 1.0F, 0.0F).endVertex();
		consumer.vertex(pos, -0.4F, 0.5F, 0.0F).color(255, 255, 255, 255).uv(start.x, start.y + uPos)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normals, 0.0F, 1.0F, 0.0F).endVertex();
		stack.popPose();
	}
	
	private void renderFrontFlamesTexture(MeteorEntity meteor, PoseStack stack, MultiBufferSource buffer, int light, int frame)
	{
		stack.pushPose();
		stack.mulPose(Vector3f.YP.rotationDegrees(90));
		stack.scale(2.5f, 2.5f, 2.5f);
		
		final float height = 48f / textureSize;
		final float width = 48f / textureSize;
		final Vec2 start = new Vec2(0, 80f / textureSize);
		float uPos = frame * height;
		
		PoseStack.Pose pose = stack.last();
		Matrix4f pos = pose.pose();
		Matrix3f normals = pose.normal();
		
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(MeteorModel.Texture));
		consumer.vertex(pos, -0.5F, -0.5F, 0.0F).color(255, 255, 255, 255).uv(start.x, start.y + height + uPos)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normals, 0.0F, 1.0F, 0.0F).endVertex();
		consumer.vertex(pos, 0.5F, -0.5F, 0.0F).color(255, 255, 255, 255).uv(start.x + width, start.y + height + uPos)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normals, 0.0F, 1.0F, 0.0F).endVertex();
		consumer.vertex(pos, 0.5F, 0.5F, 0.0F).color(255, 255, 255, 255).uv(start.x + width, start.y + uPos)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normals, 0.0F, 1.0F, 0.0F).endVertex();
		consumer.vertex(pos, -0.5F, 0.5F, 0.0F).color(255, 255, 255, 255).uv(start.x, start.y + uPos)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normals, 0.0F, 1.0F, 0.0F).endVertex();
		stack.popPose();
	}
}
