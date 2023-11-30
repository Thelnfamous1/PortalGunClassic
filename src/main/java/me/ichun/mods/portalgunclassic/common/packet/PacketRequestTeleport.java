package me.ichun.mods.portalgunclassic.common.packet;

import io.netty.buffer.ByteBuf;
import me.Thelnfamous1.portalgunclassic.IMessage;
import me.Thelnfamous1.portalgunclassic.IMessageHandler;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.portal.PortalInfo;
import me.ichun.mods.portalgunclassic.common.tileentity.TileEntityPortal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketRequestTeleport implements IMessage
{
    public BlockPos pos;
    private static final PacketRequestTeleport.Handler HANDLER = new PacketRequestTeleport.Handler();

    public PacketRequestTeleport()
    {}

    public PacketRequestTeleport(BlockPos pos)
    {
        this.pos = pos;
    }

    public PacketRequestTeleport(ByteBuf buf)
    {
        pos = BlockPos.of(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.asLong());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> HANDLER.onMessage(this, ctx));
        ctx.get().setPacketHandled(true);
    }

    public static class Handler implements IMessageHandler<PacketRequestTeleport>
    {
        @Override
        public void onMessage(PacketRequestTeleport message, Supplier<NetworkEvent.Context> ctx)
        {
            ServerPlayer player = ctx.get().getSender();
            BlockEntity te = player.level.getBlockEntity(message.pos);
            if(te instanceof TileEntityPortal)
            {
                TileEntityPortal portalCurrent = (TileEntityPortal)te;
                if(PortalGunClassic.eventHandlerServer.getSaveData(player.getLevel()).portalInfo.containsKey(player.level.dimension()))
                {
                    PortalInfo info = PortalGunClassic.eventHandlerServer.getSaveData(player.getLevel()).portalInfo.get(player.level.dimension()).get(portalCurrent.orange ? "blue" : "orange");
                    if(info != null)
                    {
                        te = player.level.getBlockEntity(info.pos);
                        if(te instanceof TileEntityPortal)
                        {
                            //There is a pair! We can teleport!
                            TileEntityPortal portalDest = (TileEntityPortal)te;

                            portalCurrent.teleport(player, portalDest);

//                            ObfuscationReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, player.connection, ObfuscationReflectionHelper.getPrivateValue(NetHandlerPlayServer.class, player.connection, "field_147368_e" ,"networkTickCount"), "field_184343_A" ,"lastPositionUpdate");
//                            player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                            //player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                            player.connection.teleport(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                        }
                    }
                }
            }

        }
    }
}
