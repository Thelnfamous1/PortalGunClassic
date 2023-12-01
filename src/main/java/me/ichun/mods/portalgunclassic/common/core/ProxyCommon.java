package me.ichun.mods.portalgunclassic.common.core;

import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.packet.PacketEntityLocation;
import me.ichun.mods.portalgunclassic.common.packet.PacketPortalStatus;
import me.ichun.mods.portalgunclassic.common.packet.PacketRequestTeleport;
import me.ichun.mods.portalgunclassic.common.packet.PacketSwapType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;

import java.util.Optional;

public class ProxyCommon
{
    private static final ResourceLocation CHANNEL_NAME = new ResourceLocation(PortalGunClassic.MOD_ID, "channel");
    private static final String PROTOCOL_VERSION = "1.0";

    public void preInit()
    {
        //GameRegistry.registerTileEntity(TileEntityPortal.class, "portalgunclassic:tile_portal");

        //EntityRegistry.registerModEntity(new ResourceLocation(PortalGunClassic.MOD_ID, "portal_projectile"), EntityPortalProjectile.class, "portalgunclassic_portal_projectile", 0, PortalGunClassic.instance, 256, 1, true);

        PortalGunClassic.eventHandlerServer = new EventHandlerServer();
        MinecraftForge.EVENT_BUS.register(PortalGunClassic.eventHandlerServer);

        PortalGunClassic.channel = NetworkRegistry.newSimpleChannel(
                CHANNEL_NAME,
                () -> "1.0",
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(() -> {
                PortalGunClassic.channel.registerMessage(0, PacketSwapType.class, PacketSwapType::toBytes, PacketSwapType::new, PacketSwapType::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
                PortalGunClassic.channel.registerMessage(1, PacketPortalStatus.class, PacketPortalStatus::toBytes, PacketPortalStatus::new, PacketPortalStatus::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
                PortalGunClassic.channel.registerMessage(2, PacketRequestTeleport.class, PacketRequestTeleport::toBytes, PacketRequestTeleport::new, PacketRequestTeleport::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
                PortalGunClassic.channel.registerMessage(3, PacketEntityLocation.class, PacketEntityLocation::toBytes, PacketEntityLocation::new, PacketEntityLocation::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
            });
        });


    }
}
