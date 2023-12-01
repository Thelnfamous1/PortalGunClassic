package me.ichun.mods.portalgunclassic.client.core;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import me.ichun.mods.portalgunclassic.client.portal.PortalStatus;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.packet.PacketSwapType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class EventHandlerClient
{
    public static final ResourceLocation txLEmpty = new ResourceLocation("portalgunclassic", "textures/overlay/lempty.png");
    public static final ResourceLocation txLFull = new ResourceLocation("portalgunclassic", "textures/overlay/lfull.png");
    public static final ResourceLocation txREmpty = new ResourceLocation("portalgunclassic", "textures/overlay/rempty.png");
    public static final ResourceLocation txRFull = new ResourceLocation("portalgunclassic", "textures/overlay/rfull.png");


    public KeyMapping keySwitch = new KeyMapping("key.portalgunclassic.switch", GLFW.GLFW_KEY_G, "key.categories.portalgun");
    public KeyMapping keyReset = new KeyMapping("key.portalgunclassic.reset", GLFW.GLFW_KEY_R, "key.categories.portalgun");

    public boolean keySwitchDown = false;
    public boolean keyResetDown = false;

    public PortalStatus status = null;
    public int teleportCooldown = 0;

    public boolean justTeleported = false;
    public double mX = 0D;
    public double mY = 0D;
    public double mZ = 0D;

    /*
    @SubscribeEvent
    public void onModelRegistry(ModelEvent.RegisterAdditional event)
    {
        ModelLoader.setCustomModelResourceLocation(PortalGunClassic.PORTAL_GUN.get(), 0, new ModelResourceLocation(PortalGunClassic.MOD_ID, "pg_blue", "inventory"));
        ModelLoader.setCustomModelResourceLocation(PortalGunClassic.PORTAL_GUN.get(), 1, new ModelResourceLocation(PortalGunClassic.MOD_ID, "pg_orange", "inventory"));
        ModelLoader.setCustomModelResourceLocation(PortalGunClassic.PORTAL_CORE.get(), 0, new ModelResourceLocation(PortalGunClassic.MOD_ID, "pg_core", "inventory"));
    }
     */

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getInstance();
            if(mc.player != null && (mc.player.getMainHandItem().getItem() == PortalGunClassic.PORTAL_GUN.get() || mc.player.getOffhandItem().getItem() == PortalGunClassic.PORTAL_GUN.get()))
            {
                if(!keySwitchDown && keySwitch.isDown())
                {
                    PortalGunClassic.channel.sendToServer(new PacketSwapType(false, 0));
                }
                if(!keyResetDown && keyReset.isDown())
                {
                    PortalGunClassic.channel.sendToServer(new PacketSwapType(true, Screen.hasShiftDown() ? 1 : 0));
                }
                keySwitchDown = keySwitch.isDown();
                keyResetDown = keyReset.isDown();
            }
        }
        else
        {
            Minecraft mc = Minecraft.getInstance();
            if(teleportCooldown > 0 && !mc.isPaused())
            {
                teleportCooldown--;
            }
            if(justTeleported)
            {
                Vec3 deltaMove = mc.player.getDeltaMovement();
                if(mc.player != null && deltaMove.x == 0.0D && deltaMove.y == 0.0D && deltaMove.z == 0.0D)
                {
                    justTeleported = false;
                    /*
                    mc.player.motionX = mX;
                    mc.player.motionY = mY;
                    mc.player.motionZ = mZ;
                     */
                    mc.player.setDeltaMovement(mX, mY, mZ);
                }
            }
        }
    }

    /*
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {

        }
    }
     */

    public void renderPortalStatusOverlay(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.screen == null && !mc.options.hideGui && mc.player != null && (mc.player.getMainHandItem().getItem() == PortalGunClassic.PORTAL_GUN.get() || mc.player.getOffhandItem().getItem() == PortalGunClassic.PORTAL_GUN.get()))
        {
            //is holding a portal gun

            gui.setupOverlayRenderState(true, false);
            //RenderSystem.enableBlend();
            //RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value);

            //ScaledResolution reso = new ScaledResolution(mc);
            double size = 40;
            double x1 = screenWidth / 2D - size + 1;
            double x2 = screenWidth / 2D + size + 1;
            double y1 = screenHeight / 2D - size + 1;
            double y2 = screenHeight / 2D + size + 1;

            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();

            RenderSystem.setShaderTexture(0, status != null && status.blue ? txLFull : txLEmpty);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferbuilder.vertex(x2, y2, 0.0D).uv(1F, 1F).color(5, 130, 255, 255).endVertex();
            bufferbuilder.vertex(x2, y1, 0.0D).uv(1F, 0F).color(5, 130, 255, 255).endVertex();
            bufferbuilder.vertex(x1, y1, 0.0D).uv(0F, 0F).color(5, 130, 255, 255).endVertex();
            bufferbuilder.vertex(x1, y2, 0.0D).uv(0F, 1F).color(5, 130, 255, 255).endVertex();
            tessellator.end();

            RenderSystem.setShaderTexture(0, status != null && status.orange ? txRFull : txREmpty);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferbuilder.vertex(x2, y2, 0.0D).uv(1F, 1F).color(255, 176, 6, 255).endVertex();
            bufferbuilder.vertex(x2, y1, 0.0D).uv(1F, 0F).color(255, 176, 6, 255).endVertex();
            bufferbuilder.vertex(x1, y1, 0.0D).uv(0F, 0F).color(255, 176, 6, 255).endVertex();
            bufferbuilder.vertex(x1, y2, 0.0D).uv(0F, 1F).color(255, 176, 6, 255).endVertex();
            tessellator.end();
        }
    }



    @SubscribeEvent
    public void onConnectToServerEvent(ClientPlayerNetworkEvent.LoggingIn event)
    {
        status = null;
        justTeleported = false;
    }
}
