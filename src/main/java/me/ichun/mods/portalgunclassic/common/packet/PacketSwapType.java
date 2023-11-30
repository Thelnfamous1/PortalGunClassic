package me.ichun.mods.portalgunclassic.common.packet;

import io.netty.buffer.ByteBuf;
import me.Thelnfamous1.portalgunclassic.IMessage;
import me.Thelnfamous1.portalgunclassic.IMessageHandler;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.sounds.SoundRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSwapType implements IMessage
{
    public boolean reset;
    public int type;
    private static final PacketSwapType.Handler HANDLER = new PacketSwapType.Handler();

    public PacketSwapType()
    {}

    public PacketSwapType(boolean reset, int type)
    {
        this.reset = reset;
        this.type = type;
    }

    public PacketSwapType(ByteBuf buf)
    {
        reset = buf.readBoolean();
        type = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(reset);
        buf.writeInt(type);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> HANDLER.onMessage(this, ctx));
        ctx.get().setPacketHandled(true);
    }

    public static class Handler implements IMessageHandler<PacketSwapType>
    {
        @Override
        public void onMessage(PacketSwapType message, Supplier<NetworkEvent.Context> ctx)
        {
            ServerPlayer player = ctx.get().getSender();
            if(!message.reset)
            {
                for(InteractionHand hand : InteractionHand.values())
                {
                    ItemStack is = player.getItemInHand(hand);
                    if(is.getItem() == PortalGunClassic.PORTAL_GUN.get())
                    {
                        is.setDamageValue(is.getDamageValue() == 1 ? 0 : 1);
                    }
                }
            }
            else
            {
                if(message.type == 0)
                {
                    PortalGunClassic.eventHandlerServer.getSaveData(player.getLevel()).kill(player.level, false);
                    PortalGunClassic.eventHandlerServer.getSaveData(player.getLevel()).kill(player.level, true);
                }
                else
                {
                    for(InteractionHand hand : InteractionHand.values())
                    {
                        ItemStack is = player.getItemInHand(hand);
                        if(is.getItem() == PortalGunClassic.PORTAL_GUN.get())
                        {
                            PortalGunClassic.eventHandlerServer.getSaveData(player.getLevel()).kill(player.level, is.getDamageValue() == 1);
                        }
                    }
                }
                player.getLevel().playSound(null, player.getX(), player.getEyeY(), player.getZ(), SoundRegistry.RESET.get(), SoundSource.PLAYERS, 0.3F, 1.0F);
            }
        }
    }
}
