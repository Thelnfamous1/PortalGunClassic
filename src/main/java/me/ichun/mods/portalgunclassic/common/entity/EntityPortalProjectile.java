package me.ichun.mods.portalgunclassic.common.entity;

import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.block.BlockPortal;
import me.ichun.mods.portalgunclassic.common.sounds.SoundRegistry;
import me.ichun.mods.portalgunclassic.common.tileentity.TileEntityPortal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class EntityPortalProjectile extends Entity
{
    private static final EntityDataAccessor<Boolean> ORANGE = SynchedEntityData.defineId(EntityPortalProjectile.class, EntityDataSerializers.BOOLEAN);
    public int age = 0;

    public EntityPortalProjectile(EntityType<? extends EntityPortalProjectile> type, Level worldIn)
    {
        super(type, worldIn);
        //setSize(0.3F, 0.3F);
        setInvulnerable(true);
    }

    public EntityPortalProjectile(Level world, Entity entity, boolean isOrange)
    {
        this(PortalGunClassic.PORTAL_PROJECTILE.get(), world);
        this.setOrange(isOrange);
        shoot(entity, 3F); // original is 4.9999, but too high for vanilla packets
        moveTo(entity.getX(), entity.getEyeY() - (this.getBbWidth() / 2F), entity.getZ(), entity.getYRot(), entity.getXRot());
    }

    public void setOrange(boolean flag)
    {
        entityData.set(ORANGE, flag);
    }

    public boolean isOrange()
    {
        return entityData.get(ORANGE);
    }

    public void shoot(Entity entity, float velocity)
    {
        float f = -Mth.sin(entity.getYRot() * Mth.DEG_TO_RAD) * Mth.cos(entity.getXRot() * Mth.DEG_TO_RAD);
        float f1 = -Mth.sin((entity.getXRot()) * Mth.DEG_TO_RAD);
        float f2 = Mth.cos(entity.getYRot() * Mth.DEG_TO_RAD) * Mth.cos(entity.getXRot() * Mth.DEG_TO_RAD);
        this.shoot(f, f1, f2, velocity);
        /*
        this.motionX += entity.motionX;
        this.motionZ += entity.motionZ;

        if (!entity.onGround)
        {
            this.motionY += entity.motionY;
        }
         */
        Vec3 entityMotion = entity.getDeltaMovement();
        this.push(entityMotion.x, !entity.isOnGround() ? entityMotion.y : 0, entityMotion.z);
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void shoot(double x, double y, double z, float velocity)
    {
        float f = Mth.sqrt((float) (x * x + y * y + z * z));
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x * (double)velocity;
        y = y * (double)velocity;
        z = z * (double)velocity;
        /*
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
         */
        this.setDeltaMovement(x, y, z);
        float f1 = Mth.sqrt((float) (x * x + z * z));
        this.setYRot((float)(Mth.atan2(x, z) * Mth.RAD_TO_DEG));
        this.setXRot((float)(Mth.atan2(y, f1) * Mth.RAD_TO_DEG));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    public void defineSynchedData()
    {
        this.entityData.define(ORANGE, false);
    }

    @Override
    public void tick()
    {
        if(getY() > level.getHeight() * 2 || getY() < -level.getHeight() * 2 || age > 1200) //a minute
        {
            discard();
            return;
        }

        age++;

        /*
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
         */
        this.setOldPosAndRot();

        super.tick();

        Vec3 motion = this.getDeltaMovement();
        Vec3 vec31 = new Vec3(this.getX(), this.getY(), this.getZ());
        Vec3 vec32 = new Vec3(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);
        if (!Double.isNaN(vec31.x) && !Double.isNaN(vec31.y) && !Double.isNaN(vec31.z))
        {
            if (!Double.isNaN(vec32.x) && !Double.isNaN(vec32.y) && !Double.isNaN(vec32.z))
            {
                int i = Mth.floor(vec32.x);
                int j = Mth.floor(vec32.y);
                int k = Mth.floor(vec32.z);
                int l = Mth.floor(vec31.x);
                int i1 = Mth.floor(vec31.y);
                int j1 = Mth.floor(vec31.z);
                BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos();

                int k1 = 200;

                while (k1-- >= 0)
                {
                    if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z) || (l == i && i1 == j && j1 == k))
                    {
                        break;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l)
                    {
                        d0 = (double)l + 1.0D;
                    }
                    else if (i < l)
                    {
                        d0 = (double)l + 0.0D;
                    }
                    else
                    {
                        flag2 = false;
                    }

                    if (j > i1)
                    {
                        d1 = (double)i1 + 1.0D;
                    }
                    else if (j < i1)
                    {
                        d1 = (double)i1 + 0.0D;
                    }
                    else
                    {
                        flag = false;
                    }

                    if (k > j1)
                    {
                        d2 = (double)j1 + 1.0D;
                    }
                    else if (k < j1)
                    {
                        d2 = (double)j1 + 0.0D;
                    }
                    else
                    {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.x - vec31.x;
                    double d7 = vec32.y - vec31.y;
                    double d8 = vec32.z - vec31.z;

                    if (flag2)
                    {
                        d3 = (d0 - vec31.x) / d6;
                    }

                    if (flag)
                    {
                        d4 = (d1 - vec31.y) / d7;
                    }

                    if (flag1)
                    {
                        d5 = (d2 - vec31.z) / d8;
                    }

                    if (d3 == -0.0D)
                    {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D)
                    {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D)
                    {
                        d5 = -1.0E-4D;
                    }

                    Direction enumfacing;

                    if (d3 < d4 && d3 < d5)
                    {
                        enumfacing = i > l ? Direction.WEST : Direction.EAST;
                        vec31 = new Vec3(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
                    }
                    else if (d4 < d5)
                    {
                        enumfacing = j > i1 ? Direction.DOWN : Direction.UP;
                        vec31 = new Vec3(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
                    }
                    else
                    {
                        enumfacing = k > j1 ? Direction.NORTH : Direction.SOUTH;
                        vec31 = new Vec3(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
                    }

                    l = Mth.floor(vec31.x) - (enumfacing == Direction.EAST ? 1 : 0);
                    i1 = Mth.floor(vec31.y) - (enumfacing == Direction.UP ? 1 : 0);
                    j1 = Mth.floor(vec31.z) - (enumfacing == Direction.SOUTH ? 1 : 0);
                    blockpos.set(l, i1, j1);
                    BlockState iblockstate1 = level.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if (!iblockstate1.getCollisionShape(level, blockpos).isEmpty())
                    {
                        //if (block1.canCollideCheck(iblockstate1, true))
                        {
                            //HitResult clip = iblockstate1.collisionRayTrace(world, blockpos, vec31, vec32);
                            BlockHitResult clip = level.clipWithInteractionOverride(vec31, vec32, blockpos, iblockstate1.getCollisionShape(level, blockpos), iblockstate1);
                            if (clip != null)
                            {
                                if(block1 == Blocks.IRON_BARS)
                                {
                                    vec31 = new Vec3(clip.getLocation().x + (motion.x / 5000D), clip.getLocation().y + (motion.y / 5000D), clip.getLocation().z + (motion.z / 5000D));
                                }
                                else
                                {
                                    PortalGunClassic.LOGGER.info("Creating portal at {}", clip.getLocation());
                                    createPortal(clip);
                                    discard();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        /*
        this.posX += motion.x;
        this.posY += motion.y;
        this.posZ += motion.z;
         */
        Vec3 newPos = this.position().add(motion);
        float f = Mth.sqrt((float) (motion.x * motion.x + motion.z * motion.z));
        this.setYRot((float) ((float)Mth.atan2(motion.x, motion.z) * Mth.RAD_TO_DEG));

        for (this.setXRot((float)(Mth.atan2(motion.y, f) * Mth.RAD_TO_DEG)); this.getXRot() - this.xRotO < -180.0F; this.yRotO -= 360.0F)
        {
        }

        while (this.getXRot() - this.xRotO >= 180.0F)
        {
            this.xRotO += 360.0F;
        }

        while (this.getYRot() - this.yRotO < -180.0F)
        {
            this.yRotO -= 360.0F;
        }

        while (this.getYRot() - this.yRotO >= 180.0F)
        {
            this.yRotO += 360.0F;
        }

        this.setXRot(this.xRotO + (this.getXRot() - this.xRotO) * 0.2F);
        this.setYRot(this.yRotO + (this.getYRot() - this.yRotO) * 0.2F);

        //this.setPosition(this.posX, this.posY, this.posZ);
        this.setPos(newPos);
    }

    public void createPortal(BlockHitResult rayTraceResult)
    {
        if(!level.isClientSide)
        {
            BlockPos pos = rayTraceResult.getBlockPos().offset(rayTraceResult.getDirection().getNormal());
            if(BlockPortal.canPlace(level, pos, rayTraceResult.getDirection(), isOrange()))
            {
                PortalGunClassic.eventHandlerServer.getSaveData((ServerLevel)level).kill(level, isOrange());

                level.setBlockAndUpdate(pos, PortalGunClassic.BLOCK_PORTAL.get().defaultBlockState());
                BlockEntity te = level.getBlockEntity(pos);
                if(te instanceof TileEntityPortal)
                {
                    ((TileEntityPortal)te).setup(rayTraceResult.getDirection().getAxis() != Direction.Axis.Y, isOrange(), rayTraceResult.getDirection());
                }
                if(rayTraceResult.getDirection().getAxis() != Direction.Axis.Y)
                {
                    level.setBlockAndUpdate(pos.below(), PortalGunClassic.BLOCK_PORTAL.get().defaultBlockState());
                    te = level.getBlockEntity(pos.below());
                    if(te instanceof TileEntityPortal)
                    {
                        ((TileEntityPortal)te).setup(false, isOrange(), rayTraceResult.getDirection());
                    }
                }
                PortalGunClassic.eventHandlerServer.getSaveData((ServerLevel) level).set(level, isOrange(), rayTraceResult.getDirection().getAxis() != Direction.Axis.Y ? pos.below() : pos);

                PortalGunClassic.LOGGER.info("Placed portal at: {}", pos);
                level.playSound(null, this.getX(), this.getY(0.5F), this.getZ(), isOrange() ? SoundRegistry.OPEN_RED.get() : SoundRegistry.OPEN_BLUE.get(), SoundSource.BLOCKS, 0.3F, 1.0F);
            }
            else
            {
                PortalGunClassic.LOGGER.info("Invalid portal placement: {}", pos);
                level.playSound(null, this.getX(), this.getY(0.5F), this.getZ(), SoundRegistry.INVALID.get(), SoundSource.NEUTRAL, 0.5F, 1.0F);
            }
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    //@Override
    public int getBrightnessForRender()
    {
        return 15728880;
    }

    
    @Override
    public boolean shouldRenderAtSqrDistance(double distance)
    {
        double d0 = this.getBoundingBox().getSize() * 10D;

        if (Double.isNaN(d0))
        {
            d0 = 1.0D;
        }

        d0 = d0 * 64.0D * getViewScale();
        return distance < d0 * d0;
    }


    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        setOrange(tag.getBoolean("orange"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        tag.putBoolean("orange", isOrange());
    }
}
