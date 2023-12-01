package me.ichun.mods.portalgunclassic.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.entity.EntityPortalProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class RenderPortalProjectile extends EntityRenderer<EntityPortalProjectile>
{
    public static final ResourceLocation txBlue = new ResourceLocation(PortalGunClassic.MOD_ID, "textures/entity/portalball_blue.png");
    public static final ResourceLocation txOrange = new ResourceLocation(PortalGunClassic.MOD_ID, "textures/entity/portalball_orange.png");

    public RenderPortalProjectile(EntityRendererProvider.Context renderManager)
    {
        super(renderManager);
    }

    @Override
    public void render(EntityPortalProjectile pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if(pEntity.age < 1)
        {
            return;
        }

        //GlStateManager.pushMatrix();
        pPoseStack.pushPose();
        //GlStateManager.translate((float)x, (float)y + 0.15F, (float)z);
        pPoseStack.translate(0.0F, 0.15F, 0.0F);
        //GlStateManager.enableRescaleNormal(); TODO
        //GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        pPoseStack.mulPose(Vector3f.YP.rotationDegrees(-this.entityRenderDispatcher.camera.getYRot()));
        //GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        pPoseStack.mulPose(Vector3f.XP.rotationDegrees((this.entityRenderDispatcher.options.getCameraType().isMirrored() ? -1 : 1) * -this.entityRenderDispatcher.camera.getXRot()));
        //GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        pPoseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        //bindTexture(getTextureLocation(pEntity));
        //RenderSystem.setShaderTexture(0, getTextureLocation(pEntity));

        /*
        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(pEntity));
        }
         */

        float f = 0F;
        float f1 = 1F;
        float f2 = 0F;
        float f3 = 1F;

        //Tesselator tessellator = Tesselator.getInstance();
        //BufferBuilder bufferbuilder = tessellator.getBuilder();
        //bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        VertexConsumer bufferbuilder = pBuffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(pEntity)));
        PoseStack.Pose posestack$pose = pPoseStack.last();
        Matrix4f pose = posestack$pose.pose();
        Matrix3f normal = posestack$pose.normal();

        bufferbuilder.vertex(pose, -0.5F, -0.5F, 0.0F).color(1F, 1F, 1F, 1F).uv(f, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(pose, 0.5F, -0.5F, 0.0F).color(1F, 1F, 1F, 1F).uv(f1, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(pose,0.5F, 0.5F, 0.0F).color(1F, 1F, 1F, 1F).uv(f1, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(pose,-0.5F, 0.5F, 0.0F).color(1F, 1F, 1F, 1F).uv(f, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        //tessellator.end();

        /*
        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }
         */

        //GlStateManager.disableRescaleNormal(); TODO
        //GlStateManager.popMatrix();
        pPoseStack.popPose();
        //super.doRender(pEntity, x, y, z, entityYaw, partialTicks);
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    /*
    @Override
    public void doRender(EntityPortalProjectile entity, double x, double y, double z, float entityYaw, float partialTicks)
    {

    }
     */

    @Override
    public ResourceLocation getTextureLocation(EntityPortalProjectile pEntity) {
        return pEntity.isOrange() ? txOrange : txBlue;
    }
}
