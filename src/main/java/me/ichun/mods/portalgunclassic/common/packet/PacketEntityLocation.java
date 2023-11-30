package me.ichun.mods.portalgunclassic.common.packet;

import io.netty.buffer.ByteBuf;
import me.Thelnfamous1.portalgunclassic.IMessage;
import me.Thelnfamous1.portalgunclassic.IMessageHandler;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketEntityLocation implements IMessage
{
    public int id;
    public double ltX;
    public double ltY;
    public double ltZ;
    public double prevX;
    public double prevY;
    public double prevZ;
    public double posX;
    public double posY;
    public double posZ;
    public double mX;
    public double mY;
    public double mZ;
    public float prevYaw;
    public float prevPitch;
    public float yaw;
    public float pitch;
    private static final PacketEntityLocation.Handler HANDLER = new PacketEntityLocation.Handler();

    public PacketEntityLocation()
    {}

    public PacketEntityLocation(Entity ent)
    {
        id = ent.getId();
        ltX = ent.xo;
        ltY = ent.yo;
        ltZ = ent.zo;
        prevX = ent.xOld;
        prevY = ent.yOld;
        prevZ = ent.zOld;
        posX = ent.getX();
        posY = ent.getY();
        posZ = ent.getZ();
        mX = ent.getDeltaMovement().x;
        mY = ent.getDeltaMovement().y;
        mZ = ent.getDeltaMovement().z;
        prevYaw = ent.yRotO;
        prevPitch = ent.xRotO;
        yaw = ent.getYRot();
        pitch = ent.getXRot();
    }

    public PacketEntityLocation(ByteBuf buf)
    {
        id = buf.readInt();
        ltX = buf.readDouble();
        ltY = buf.readDouble();
        ltZ = buf.readDouble();
        prevX = buf.readDouble();
        prevY = buf.readDouble();
        prevZ = buf.readDouble();
        posX = buf.readDouble();
        posY = buf.readDouble();
        posZ = buf.readDouble();
        mX = buf.readDouble();
        mY = buf.readDouble();
        mZ = buf.readDouble();
        prevYaw = buf.readFloat();
        prevPitch = buf.readFloat();
        yaw = buf.readFloat();
        pitch = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeDouble(ltX);
        buf.writeDouble(ltY);
        buf.writeDouble(ltZ);
        buf.writeDouble(prevX);
        buf.writeDouble(prevY);
        buf.writeDouble(prevZ);
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);
        buf.writeDouble(mX);
        buf.writeDouble(mY);
        buf.writeDouble(mZ);
        buf.writeFloat(prevYaw);
        buf.writeFloat(prevPitch);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> HANDLER.onMessage(this, ctx));
        ctx.get().setPacketHandled(true);
    }

    public static class Handler implements IMessageHandler<PacketEntityLocation>
    {
        @Override
        public void onMessage(PacketEntityLocation message, Supplier<NetworkEvent.Context> ctx)
        {
            handleClient(message);
        }

        public void handleClient(PacketEntityLocation message)
        {
            Entity ent = Minecraft.getInstance().level.getEntity(message.id);
            if(ent != null)
            {

                /*
                ent.posX = message.posX;
                ent.posY = message.posY;
                ent.posZ = message.posZ;
                ent.motionX = message.mX;
                ent.motionY = message.mY;
                ent.motionZ = message.mZ;
                 */
                ent.absMoveTo(message.posX, message.posY, message.posZ, message.yaw, message.pitch);
                ent.lerpMotion(message.mX, message.mY, message.mZ);
                ent.xo = message.ltX;
                ent.yo = message.ltY;
                ent.zo = message.ltZ;
                ent.xOld = message.prevX;
                ent.yOld = message.prevY;
                ent.zOld = message.prevZ;
                ent.yRotO = message.prevYaw;
                ent.xRotO = message.prevPitch;
                /*
                ent.rotationYaw = message.yaw;
                ent.rotationPitch = message.pitch;
                 */

                if(ent == Minecraft.getInstance().player)
                {
                    PortalGunClassic.eventHandlerClient.justTeleported = true;
                    PortalGunClassic.eventHandlerClient.mX = ent.getDeltaMovement().x;
                    PortalGunClassic.eventHandlerClient.mY = ent.getDeltaMovement().y;
                    PortalGunClassic.eventHandlerClient.mZ = ent.getDeltaMovement().z;
                }
            }
        }
    }
}
