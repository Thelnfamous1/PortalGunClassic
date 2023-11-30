package me.ichun.mods.portalgunclassic.common.tileentity;

import me.Thelnfamous1.portalgunclassic.PGCRegistries;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.packet.PacketEntityLocation;
import me.ichun.mods.portalgunclassic.common.packet.PacketRequestTeleport;
import me.ichun.mods.portalgunclassic.common.portal.PortalInfo;
import me.ichun.mods.portalgunclassic.common.sounds.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class TileEntityPortal extends BlockEntity
{
    public boolean setup;
    public boolean top;
    public boolean orange;
    public Direction face;

    public TileEntityPortal(BlockPos blockPos, BlockState blockState)
    {
        super(PGCRegistries.TILE_PORTAL.get(), blockPos, blockState);
        top = false;
        orange = false;
        face = Direction.DOWN;
    }

    @Override
    public void update()
    {
        if(top)
        {
            return; //The top never does anything and is there just to look pretty.
        }

        BlockPos pairLocation = BlockPos.ZERO;
        if(!level.isClientSide)
        {
            if(!PortalGunClassic.eventHandlerServer.getSaveData((ServerLevel) level).portalInfo.containsKey(level.dimension()))
            {
                return;
            }
            PortalInfo info = PortalGunClassic.eventHandlerServer.getSaveData((ServerLevel) level).portalInfo.get(level.dimension()).get(orange ? "blue" : "orange");
            if(info == null)
            {
                return;
            }
            pairLocation = info.pos;
        }
        else
        {
            if(orange && !PortalGunClassic.eventHandlerClient.status.blue || !orange && !PortalGunClassic.eventHandlerClient.status.orange)
            {
                return;
            }
        }
        //Only hits here if we have a pair
        AABB aabbScan =   new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + (face.getAxis() != Direction.Axis.Y ? 2 : 1), worldPosition.getZ() + 1).expandTowards(face.getStepX() * 4, face.getStepY() * 4, face.getStepZ() * 4);
        AABB aabbInside = new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + (face.getAxis() != Direction.Axis.Y ? 2 : 1), worldPosition.getZ() + 1).expandTowards(face.getStepX() * 9, face.getStepY() * 9, face.getStepZ() * 9).move(-face.getStepX() * 9.999D, -face.getStepY() * 9.999D, -face.getStepZ() * 9.999D);
        List<? extends Entity> ents = level.isClientSide ? level.getEntitiesOfClass(Player.class, aabbScan) : level.getEntitiesOfClass(Entity.class, aabbScan);
        for(Entity ent : ents)
        {
            if(!level.isClientSide && ent instanceof Player)
            {
                continue; //we ignore players. They tell the server when they want a teleport.
            }

            Vec3 motion = ent.getDeltaMovement();
            if(ent.getBoundingBox().move(motion.x, motion.y, motion.z).intersects(aabbInside))
            {
                if(level.isClientSide)
                {
                    handleClientTeleport((Player)ent);
                }
                else
                {
                    BlockEntity te = level.getBlockEntity(pairLocation);
                    if(te instanceof TileEntityPortal)
                    {
                        teleport(ent, (TileEntityPortal)te);
                    }
                }
            }
        }
    }

    public void handleClientTeleport(Player player)
    {
        if(PortalGunClassic.eventHandlerClient.teleportCooldown <= 0 && player == Minecraft.getInstance().player)
        {
            PortalGunClassic.eventHandlerClient.teleportCooldown = 3;
            PortalGunClassic.channel.sendToServer(new PacketRequestTeleport(worldPosition));
        }
    }

    //This code is bad. I know.
    public void teleport(Entity ent, TileEntityPortal pair)
    {
        double newX = pair.getBlockPos().getX() + 0.5D - (0.5D - (ent.getBoundingBox().maxX - ent.getBoundingBox().minX) / 2D) * 0.99D * pair.face.getStepX();
        double newZ = pair.getBlockPos().getZ() + 0.5D - (0.5D - (ent.getBoundingBox().maxZ - ent.getBoundingBox().minZ) / 2D) * 0.99D * pair.face.getStepZ();

        double newY = pair.getBlockPos().getY() + (pair.face.getStepY() < 0 ? -(ent.getBoundingBox().maxY - ent.getBoundingBox().minY) + 0.999D : 0.001D);

        Vec3 motion = ent.getDeltaMovement();
        if(face.getAxis() != Direction.Axis.Y && pair.face.getAxis() != Direction.Axis.Y) //horizontal
        {
            float yawDiff = face.toYRot() - (pair.face.getOpposite().toYRot());
            ent.xRotO -= yawDiff;
            ent.setYRot(ent.getYRot() - yawDiff);

            double mX = motion.x;
            double mZ = motion.z;

            if(pair.face == face)
            {
                /*
                ent.motionX = -mX;
                ent.motionZ = -mZ;
                 */
                ent.push(-mX, 0, -mZ);
            }
            else if(face.getAxis() == Direction.Axis.X)
            {
                if(pair.face == Direction.NORTH)
                {
                    ent.motionZ = -mX * - face.getStepX();
                    ent.motionX = mZ * - face.getStepX();
                }
                else if(pair.face == Direction.SOUTH)
                {
                    ent.motionZ = mX * - face.getStepX();
                    ent.motionX = -mZ * - face.getStepX();
                }
            }
            else if(face.getAxis() == Direction.Axis.Z)
            {
                if(pair.face == Direction.EAST)
                {
                    ent.motionZ = -mX * - face.getStepZ();
                    ent.motionX = mZ * - face.getStepZ();
                }
                else if(pair.face == Direction.WEST)
                {
                    ent.motionZ = mX * - face.getStepZ();
                    ent.motionX = -mZ * - face.getStepZ();
                }
            }
        }
        else if(face.getAxis() == Direction.Axis.Y && pair.face.getAxis() != Direction.Axis.Y) //from vertical to horizontal
        {
            ent.setXRot(0F);
            ent.setYRot(pair.face.toYRot());
            //ent.motionX = Math.abs(motion.y) * pair.face.getStepX();
            //ent.motionZ = Math.abs(motion.y) * pair.face.getStepZ();
            //ent.motionY = 0D;
            ent.setDeltaMovement(new Vec3(Math.abs(motion.y) * pair.face.getStepX(), 0D, Math.abs(motion.y) * pair.face.getStepZ()));
            ent.fallDistance = 0F;
        }
        else if(face.getAxis() != Direction.Axis.Y && pair.face.getAxis() == Direction.Axis.Y) //from horizontal to vertical
        {
            //ent.motionY = Math.sqrt(motion.x * motion.x + motion.z * motion.z) * pair.face.getStepY();
            ent.setDeltaMovement(new Vec3(motion.x, Math.sqrt(motion.x * motion.x + motion.z * motion.z) * pair.face.getStepY(), motion.z));
        }
        else //vertical only
        {
            if(pair.face == face)
            {
                //ent.motionY = -ent.motionY;
                ent.setDeltaMovement(new Vec3(motion.x, -motion.y, motion.z));
            }
            ent.fallDistance = 0F;
        }
        /*
        ent.motionX += pair.face.getStepX() * 0.2D;
        ent.motionY += pair.face.getStepY() * 0.2D;
        ent.motionZ += pair.face.getStepZ() * 0.2D;
         */
        ent.push(pair.face.getStepX() * 0.2D, pair.face.getStepY() * 0.2D, pair.face.getStepZ() * 0.2D);
        ent.moveTo(newX, newY, newZ, ent.getYRot(), ent.getXRot());

        level.playSound(null, this.getBlockPos().getX() + 0.5D, this.getBlockPos().getY() + (face.getAxis() != Direction.Axis.Y ? 1D : 0.5D), this.getBlockPos().getZ() + 0.5D, PGCRegistries.ENTER.get(), SoundSource.BLOCKS, 0.1F, 1.0F);
        level.playSound(null, pair.getBlockPos().getX() + 0.5D, pair.getBlockPos().getY() + (pair.face.getAxis() != Direction.Axis.Y ? 1D : 0.5D), pair.getBlockPos().getZ() + 0.5D, PGCRegistries.EXIT.get(), SoundSource.BLOCKS, 0.1F, 1.0F);

        PortalGunClassic.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new PacketEntityLocation(ent), new NetworkRegistry.TargetPoint(ent.getLevel().dimension(), worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D, 256));
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(this.worldPosition, 0, this.getUpdateTag());
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.writeToNBT(new CompoundTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.getNbtCompound());
    }

    public void setup(boolean top, boolean orange, Direction face)
    {
        this.setup = true;

        this.top = top;
        this.orange = orange;
        this.face = face;
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putBoolean("setup", setup);
        tag.putBoolean("top", top);
        tag.putBoolean("orange", orange);
        tag.putInt("face", face.get3DDataValue());
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        setup = tag.getBoolean("setup");
        top = tag.getBoolean("top");
        orange = tag.getBoolean("orange");
        face = Direction.from3DDataValue(tag.getInt("face"));
    }
}
