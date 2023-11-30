package me.ichun.mods.portalgunclassic.common.core;

import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.entity.EntityPortalProjectile;
import me.ichun.mods.portalgunclassic.common.packet.PacketEntityLocation;
import me.ichun.mods.portalgunclassic.common.packet.PacketPortalStatus;
import me.ichun.mods.portalgunclassic.common.packet.PacketRequestTeleport;
import me.ichun.mods.portalgunclassic.common.packet.PacketSwapType;
import me.ichun.mods.portalgunclassic.common.tileentity.TileEntityPortal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
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
        PortalGunClassic.channel.registerMessage(0, PacketSwapType.class, new PacketSwapType.Handler(), Optional.of(NetworkDirection.PLAY_TO_SERVER));
        PortalGunClassic.channel.registerMessage(1, PacketPortalStatus.class, new PacketPortalStatus.Handler(), Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PortalGunClassic.channel.registerMessage(2, PacketRequestTeleport.class, new PacketRequestTeleport.Handler(), Optional.of(NetworkDirection.PLAY_TO_SERVER));
        PortalGunClassic.channel.registerMessage(3, PacketEntityLocation.class, new PacketEntityLocation.Handler(), Optional.of(NetworkDirection.PLAY_TO_CLIENT));


    }
}
