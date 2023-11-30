package me.Thelnfamous1.portalgunclassic;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface IMessageHandler<MSG> {
    void onMessage(MSG message, Supplier<NetworkEvent.Context> ctx);
}
