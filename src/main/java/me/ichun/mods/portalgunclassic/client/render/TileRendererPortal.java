package me.ichun.mods.portalgunclassic.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.tileentity.TileEntityPortal;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class TileRendererPortal implements BlockEntityRenderer<TileEntityPortal>
{
    public static final ResourceLocation txBlueBtm = new ResourceLocation(PortalGunClassic.MOD_ID, "textures/blocks/blue_bottom.png");
    public static final ResourceLocation txBlueTop = new ResourceLocation(PortalGunClassic.MOD_ID, "textures/blocks/blue_top.png");
    public static final ResourceLocation txBlueY = new ResourceLocation(PortalGunClassic.MOD_ID, "textures/blocks/blue_floor.png");
    public static final ResourceLocation txOrangeBtm = new ResourceLocation(PortalGunClassic.MOD_ID, "textures/blocks/orange_bottom.png");
    public static final ResourceLocation txOrangeTop = new ResourceLocation(PortalGunClassic.MOD_ID, "textures/blocks/orange_top.png");
    public static final ResourceLocation txOrangeY = new ResourceLocation(PortalGunClassic.MOD_ID, "textures/blocks/orange_floor.png");

    public TileRendererPortal(BlockEntityRendererProvider.Context pContext) {

    }

    /*
    public void render(TileEntityPortal te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        
    }
     */

    @Override
    public void render(TileEntityPortal te, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if(te.setup)
        {
            //GlStateManager.pushMatrix();
            pPoseStack.pushPose();
            //GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
            pPoseStack.translate(0.5D, 0.5D, 0.5D);

            //GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            //GlStateManager.rotate(te.face.getStepY() * 90F, 0F, 0F, 1F);
            pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(te.face.getStepY() * 90F));
            //GlStateManager.rotate(-te.face.toYRot(), 0F, 1F, 0F);
            pPoseStack.mulPose(Vector3f.YP.rotationDegrees(-te.face.toYRot()));
            //GlStateManager.translate(0D, 0D, -0.495D);
            pPoseStack.translate(0D, 0D, -0.495D);

            //GlStateManager.enableAlpha(); TODO
            //GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F); TODO

            //GlStateManager.enableBlend();
            //RenderSystem.enableBlend();
            //GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            //RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            //GlStateManager.disableLighting(); TODO

            //bindTexture(te.face.getAxis() == Direction.Axis.Y ? (te.orange ? txOrangeY : txBlueY) : te.top ? (te.orange ? txOrangeTop : txBlueTop) : te.orange ? txOrangeBtm : txBlueBtm);
            //RenderSystem.setShaderTexture(0, te.face.getAxis() == Direction.Axis.Y ? (te.orange ? txOrangeY : txBlueY) : te.top ? (te.orange ? txOrangeTop : txBlueTop) : te.orange ? txOrangeBtm : txBlueBtm);

            //Tesselator tessellator = Tesselator.getInstance();
            //BufferBuilder bufferbuilder = tessellator.getBuilder();
            //bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            VertexConsumer bufferbuilder = pBufferSource.getBuffer(RenderType.beaconBeam(te.face.getAxis() == Direction.Axis.Y ? (te.orange ? txOrangeY : txBlueY) : te.top ? (te.orange ? txOrangeTop : txBlueTop) : te.orange ? txOrangeBtm : txBlueBtm, false));
            Matrix4f pose = pPoseStack.last().pose();
            Matrix3f normal = pPoseStack.last().normal();
            bufferbuilder.vertex(pose, -0.5F, -0.5F, 0.0F).color(1F, 1F, 1F, 1F).uv(0F, 1F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(pose,0.5F, -0.5F, 0.0F).color(1F, 1F, 1F, 1F).uv(1F, 1F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(pose,0.5F, 0.5F, 0.0F).color(1F, 1F, 1F, 1F).uv(1F, 0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(pose,-0.5F, 0.5F, 0.0F).color(1F, 1F, 1F, 1F).uv(0F, 0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 0.0F, 0.0F).endVertex();
            //tessellator.end();

            //GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F); TODO

            //GlStateManager.enableLighting(); TODO

            //GlStateManager.disableBlend();
            //RenderSystem.disableBlend();

            //GlStateManager.popMatrix();
            pPoseStack.popPose();
        }
    }
}
