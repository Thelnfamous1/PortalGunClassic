package me.ichun.mods.portalgunclassic.common;

import me.ichun.mods.portalgunclassic.client.core.EventHandlerClient;
import me.ichun.mods.portalgunclassic.common.core.EventHandlerServer;
import me.ichun.mods.portalgunclassic.common.core.ProxyCommon;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod(PortalGunClassic.MOD_ID)
public class PortalGunClassic
{
    public static final String MOD_ID = "portalgunclassic";
    public static final String MOD_NAME = "PortalGunClassic";
    public static final String VERSION = "1.0.0";

    public static PortalGunClassic instance;

    @SidedProxy(clientSide = "me.ichun.mods.portalgunclassic.client.core.ProxyClient", serverSide = "me.ichun.mods.portalgunclassic.common.core.ProxyCommon")
    public static ProxyCommon proxy;

    public static EventHandlerClient eventHandlerClient;
    public static EventHandlerServer eventHandlerServer;

    //public static Item itemPortalGun;
    //public static Item itemPortalCore;

    //public static Block blockPortalGun;

    public static SimpleChannel channel;

    public PortalGunClassic(){
        instance = this;
    }

    //@Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        proxy.preInit();
    }
}
