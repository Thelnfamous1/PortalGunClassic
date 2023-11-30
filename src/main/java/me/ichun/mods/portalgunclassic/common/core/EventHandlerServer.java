package me.ichun.mods.portalgunclassic.common.core;

import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.packet.PacketPortalStatus;
import me.ichun.mods.portalgunclassic.common.portal.PortalInfo;
import me.ichun.mods.portalgunclassic.common.world.PortalSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;

public class EventHandlerServer
{
    /*
    @SubscribeEvent
    public void onRegisterBlock(RegistryEvent.Register<Block> event)
    {
        PortalGunClassic.blockPortalGun = new BlockPortal(BlockBehaviour.Properties.of(Material.DECORATION)
                .strength(-1F, 1000000.0F)
                .lightLevel(state -> 8));
        event.getRegistry().register(PortalGunClassic.blockPortalGun);
    }
    */

    /*
    @SubscribeEvent
    public void onRegisterItem(RegistryEvent.Register<Item> event)
    {
        PortalGunClassic.itemPortalGun = new ItemPortalGun(new Item.Properties().stacksTo(1).durability(0).tab(CreativeModeTab.TAB_TOOLS));
        event.getRegistry().register(PortalGunClassic.itemPortalGun);

        PortalGunClassic.itemPortalCore = new ItemPortalCore(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
        event.getRegistry().register(PortalGunClassic.itemPortalCore);
    }
     */

    /*
    @SubscribeEvent
    public void onRegisterSound(RegistryEvent.Register<SoundEvent> event)
    {
        SoundRegistry.init(event.getRegistry());
    }
     */

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        updatePlayerDimensionStatus(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        updatePlayerDimensionStatus(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event)
    {
        updatePlayerDimensionStatus(event.getEntity());
    }

    public void updatePlayerDimensionStatus(Player player)
    {
        if(player instanceof ServerPlayer serverPlayer){
            PortalSavedData data = getSaveData(serverPlayer.getLevel());
            HashMap<String, PortalInfo> map = data.portalInfo.get(serverPlayer.getLevel().dimension());
            PortalGunClassic.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PacketPortalStatus(map != null && map.containsKey("blue"), map != null && map.containsKey("orange")));
        }
    }

    public PortalSavedData getSaveData(ServerLevel world)
    {
        PortalSavedData data = world.getDataStorage().get(PortalSavedData::load, PortalSavedData.DATA_ID);
        if(data == null)
        {
            data = new PortalSavedData();
            world.getDataStorage().set(PortalSavedData.DATA_ID, data);
            data.setDirty();
        }
        return data;
    }
}
