package me.ichun.mods.portalgunclassic.client.core;

import me.ichun.mods.portalgunclassic.client.render.RenderPortalProjectile;
import me.ichun.mods.portalgunclassic.client.render.TileRendererPortal;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.core.ProxyCommon;
import me.ichun.mods.portalgunclassic.common.item.ItemPortalGun;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ProxyClient extends ProxyCommon
{
    @Override
    public void preInit()
    {
        super.preInit();

        PortalGunClassic.eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(PortalGunClassic.eventHandlerClient);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener((RegisterKeyMappingsEvent event) -> {
            event.register(PortalGunClassic.eventHandlerClient.keySwitch);
            event.register(PortalGunClassic.eventHandlerClient.keyReset);
        });
        modEventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
            event.registerBlockEntityRenderer(PortalGunClassic.TILE_PORTAL.get(), TileRendererPortal::new);
            event.registerEntityRenderer(PortalGunClassic.PORTAL_PROJECTILE.get(), RenderPortalProjectile::new);
        });
        modEventBus.addListener((FMLClientSetupEvent event) -> {
            event.enqueueWork(() -> {
                ItemProperties.register(PortalGunClassic.PORTAL_GUN.get(), new ResourceLocation(PortalGunClassic.MOD_ID, "orange"), ((pStack, pLevel, pEntity, pSeed) -> {
                    return ItemPortalGun.isOrange(pStack) ? 1.0F : 0.0F;
                }));
            });
        });
        modEventBus.addListener((RegisterGuiOverlaysEvent event) -> {
            event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "portal_status", PortalGunClassic.eventHandlerClient::renderPortalStatusOverlay);
        });
    }

}
