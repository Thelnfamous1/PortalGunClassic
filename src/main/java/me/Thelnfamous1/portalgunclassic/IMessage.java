package me.Thelnfamous1.portalgunclassic;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface IMessage {
    void toBytes(ByteBuf buf);

    void handle(Supplier<NetworkEvent.Context> ctx);
}
