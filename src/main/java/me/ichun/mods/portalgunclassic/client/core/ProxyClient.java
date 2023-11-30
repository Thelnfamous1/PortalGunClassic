package me.ichun.mods.portalgunclassic.client.core;

import me.ichun.mods.portalgunclassic.client.render.RenderPortalProjectile;
import me.ichun.mods.portalgunclassic.client.render.TileRendererPortal;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.core.ProxyCommon;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
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
        modEventBus.addListener((RegisterKeyMappingsEvent event) -> event.register(PortalGunClassic.eventHandlerClient.keySwitch));
        modEventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
            event.registerBlockEntityRenderer(PortalGunClassic.TILE_PORTAL.get(), TileRendererPortal::new);
            event.registerEntityRenderer(PortalGunClassic.PORTAL_PROJECTILE.get(), RenderPortalProjectile::new);
        });
    }
}
