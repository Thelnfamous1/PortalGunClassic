package me.ichun.mods.portalgunclassic.common.packet;

import io.netty.buffer.ByteBuf;
import me.Thelnfamous1.portalgunclassic.IMessage;
import me.Thelnfamous1.portalgunclassic.IMessageHandler;
import me.ichun.mods.portalgunclassic.client.portal.PortalStatus;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketPortalStatus implements IMessage
{
    public boolean blue;
    public boolean orange;
    private static final PacketPortalStatus.Handler HANDLER = new PacketPortalStatus.Handler();

    public PacketPortalStatus()
    {}

    public PacketPortalStatus(boolean blue, boolean orange)
    {
        this.blue = blue;
        this.orange = orange;
    }

    public PacketPortalStatus(ByteBuf buf)
    {
        blue = buf.readBoolean();
        orange = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(blue);
        buf.writeBoolean(orange);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> HANDLER.onMessage(this, ctx));
        ctx.get().setPacketHandled(true);
    }

    public static class Handler implements IMessageHandler<PacketPortalStatus>
    {
        @Override
        public void onMessage(PacketPortalStatus message, Supplier<NetworkEvent.Context> ctx)
        {
            PortalGunClassic.eventHandlerClient.status = new PortalStatus(message.blue, message.orange);
        }
    }
}
