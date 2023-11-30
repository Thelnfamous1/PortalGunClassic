package me.ichun.mods.portalgunclassic.client.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import me.Thelnfamous1.portalgunclassic.PGCRegistries;
import me.ichun.mods.portalgunclassic.client.portal.PortalStatus;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.packet.PacketSwapType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
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

    @SubscribeEvent
    public void onModelRegistry(ModelEvent.RegisterAdditional event)
    {
        ModelLoader.setCustomModelResourceLocation(PGCRegistries.PORTAL_GUN.get(), 0, new ModelResourceLocation(PortalGunClassic.MOD_ID, "pg_blue", "inventory"));
        ModelLoader.setCustomModelResourceLocation(PGCRegistries.PORTAL_GUN.get(), 1, new ModelResourceLocation(PortalGunClassic.MOD_ID, "pg_orange", "inventory"));
        ModelLoader.setCustomModelResourceLocation(PGCRegistries.PORTAL_CORE.get(), 0, new ModelResourceLocation(PortalGunClassic.MOD_ID, "pg_core", "inventory"));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getInstance();
            if(mc.player != null && (mc.player.getMainHandItem().getItem() == PGCRegistries.PORTAL_GUN.get() || mc.player.getOffhandItem().getItem() == PGCRegistries.PORTAL_GUN.get()))
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

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getInstance();
            if(mc.screen == null && !mc.options.hideGui && mc.player != null && (mc.player.getMainHandItem().getItem() == PGCRegistries.PORTAL_GUN.get() || mc.player.getOffhandItem().getItem() == PGCRegistries.PORTAL_GUN.get()))
            {
                //is holding a portal gun

                GlStateManager._enableBlend();
                GlStateManager._blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value);

                ScaledResolution reso = new ScaledResolution(mc);
                double size = 40;
                double x1 = reso.getScaledWidth() / 2D - size + 1;
                double x2 = reso.getScaledWidth() / 2D + size + 1;
                double y1 = reso.getScaledHeight() / 2D - size + 1;
                double y2 = reso.getScaledHeight() / 2D + size + 1;

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();

                mc.getTextureManager().bindForSetup(status != null && status.blue ? txLFull : txLEmpty);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos(x2, y2, 0.0D).tex(1D, 1D).color(5, 130, 255, 255).endVertex();
                bufferbuilder.pos(x2, y1, 0.0D).tex(1D, 0D).color(5, 130, 255, 255).endVertex();
                bufferbuilder.pos(x1, y1, 0.0D).tex(0D, 0D).color(5, 130, 255, 255).endVertex();
                bufferbuilder.pos(x1, y2, 0.0D).tex(0D, 1D).color(5, 130, 255, 255).endVertex();
                tessellator.draw();

                mc.getTextureManager().bindTexture(status != null && status.orange ? txRFull : txREmpty);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos(x2, y2, 0.0D).tex(1D, 1D).color(255, 176, 6, 255).endVertex();
                bufferbuilder.pos(x2, y1, 0.0D).tex(1D, 0D).color(255, 176, 6, 255).endVertex();
                bufferbuilder.pos(x1, y1, 0.0D).tex(0D, 0D).color(255, 176, 6, 255).endVertex();
                bufferbuilder.pos(x1, y2, 0.0D).tex(0D, 1D).color(255, 176, 6, 255).endVertex();
                tessellator.draw();
            }
        }
    }

    @SubscribeEvent
    public void onConnectToServerEvent(ClientPlayerNetworkEvent.LoggingIn event)
    {
        status = null;
        justTeleported = false;
    }
}
