package me.ichun.mods.portalgunclassic.common.world;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.packet.PacketPortalStatus;
import me.ichun.mods.portalgunclassic.common.portal.PortalInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class PortalSavedData extends SavedData
{
    public static final String DATA_ID = "PortalGunClassicSaveData";
    private static final Logger LOGGER = LogUtils.getLogger();

    public HashMap<ResourceKey<Level>, HashMap<String, PortalInfo>> portalInfo = new HashMap<>();

    public PortalSavedData()
    {
    }

    public void set(Level world, boolean orange, BlockPos pos)
    {
        HashMap<String, PortalInfo> map = portalInfo.computeIfAbsent(world.dimension(), k -> new HashMap<>());
        map.put(orange ? "orange" : "blue", new PortalInfo(orange, pos));
        this.setDirty();
        PortalGunClassic.channel.send(PacketDistributor.DIMENSION.with(world::dimension), new PacketPortalStatus(map.containsKey("blue"), map.containsKey("orange")));
    }

    public void kill(Level world, boolean orange)
    {
        HashMap<String, PortalInfo> map = portalInfo.get(world.dimension());
        if(map != null)
        {
            PortalInfo info = map.get(orange ? "orange" : "blue");
            if(info != null)
            {
                info.kill(world);
                map.remove(orange ? "orange" : "blue");
                if(map.isEmpty())
                {
                    portalInfo.remove(world.dimension());
                }
                this.setDirty();
            }
            PortalGunClassic.channel.send(PacketDistributor.DIMENSION.with(world::dimension), new PacketPortalStatus(map.containsKey("blue"), map.containsKey("orange")));

        }
    }

    public static PortalSavedData load(CompoundTag tag)
    {
        PortalSavedData portalSavedData = new PortalSavedData();
        int count = tag.getInt("dimCount");
        for(int dimIdx = 0; dimIdx < count; dimIdx++)
        {
            CompoundTag dimTag = tag.getCompound("dim" + dimIdx);
            HashMap<String, PortalInfo> map = new HashMap<>();
            if(dimTag.contains("blue"))
            {
                map.put("blue", PortalInfo.createFromNBT(dimTag.getCompound("blue")));
            }
            if(dimTag.contains("orange"))
            {
                map.put("orange", PortalInfo.createFromNBT(dimTag.getCompound("orange")));
            }
            if(!map.isEmpty())
            {
                ResourceKey<Level> dimension = DimensionType.parseLegacy(new Dynamic<>(NbtOps.INSTANCE, dimTag.get("dimension")))
                        .resultOrPartial(LOGGER::error)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid portal dimension: " + dimTag.get("dim")));
                portalSavedData.portalInfo.put(dimension, map);
            }
        }
        return portalSavedData;
    }

    @Override
    public CompoundTag save(CompoundTag tag)
    {
        tag.putInt("dimCount", portalInfo.size());
        int dimIdx = 0;
        for(Map.Entry<ResourceKey<Level>, HashMap<String, PortalInfo>> e : portalInfo.entrySet())
        {
            CompoundTag dimTag = new CompoundTag();
            ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, e.getKey().location())
                    .resultOrPartial(LOGGER::error)
                    .ifPresent((nbt) -> dimTag.put("dim", nbt));
            if(e.getValue().containsKey("blue"))
            {
                dimTag.put("blue", e.getValue().get("blue").toNBT());
            }
            if(e.getValue().containsKey("orange"))
            {
                dimTag.put("orange", e.getValue().get("orange").toNBT());
            }

            tag.put("dim" + dimIdx, dimTag);
            dimIdx++;
        }

        return tag;
    }
}
