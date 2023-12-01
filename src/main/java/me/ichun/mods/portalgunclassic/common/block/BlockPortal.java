package me.ichun.mods.portalgunclassic.common.block;

import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.tileentity.TileEntityPortal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BlockPortal extends BaseEntityBlock
{
    public static final AABB EMPTY_AABB = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    public BlockPortal(BlockBehaviour.Properties properties)
    {
        super(properties);
        /*
        Material.CIRCUITS
        setHardness(-1F);
        setResistance(1000000.0F);
        setLightLevel(0.5F);
        setRegistryName(new ResourceLocation(PortalGunClassic.MOD_ID, "portal"));
        setUnlocalizedName("portalgunclassic.block.blockportal");
         */
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, PortalGunClassic.TILE_PORTAL.get(), TileEntityPortal::update);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileEntityPortal(pPos, pState);
    }

    // set render layer to cutout in the proper event if not a full cube
    /*
    @Override
    public boolean isFullCube(BlockState state)
    {
        return false;
    }
     */

    // set no occlusion to true when registering the block properties
    /*
    @Override
    public boolean isOpaqueCube(BlockState state)
    {
        return false;
    }
     */

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }

    // set no collision to true when registering the block properties
    /*
    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return null;
    }
     */

    /*
    @Override
    public BlockFaceShape getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face)
    {
        return BlockFaceShape.UNDEFINED;
    }
     */

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof TileEntityPortal)
        {
            TileEntityPortal portal = (TileEntityPortal)te;
            if(portal.setup)
            {
                if(portal.face.getAxis() == Direction.Axis.Y)
                {
                    if(!isSideSolid(world, pos, portal.face))
                    {
                        PortalGunClassic.eventHandlerServer.getSaveData((ServerLevel) world).kill(world, portal.orange);
                        world.removeBlock(pos, false);
                    }
                }
                else
                {
                    BlockPos other = portal.top ? pos.below() : pos.above();
                    if(!(isSideSolid(world, pos, portal.face) && isSideSolid(world, other, portal.face)) || world.getBlockState(other).getBlock() != this)
                    {
                        PortalGunClassic.eventHandlerServer.getSaveData((ServerLevel) world).kill(world, portal.orange);
                        world.removeBlock(pos, false);
                    }
                }
            }
        }
        else
        {
            world.removeBlock(pos, false);
        }
    }

    public static boolean canPlace(Level world, BlockPos pos, Direction sideHit, boolean isOrange)
    {
        if(world.getBlockState(pos).getMaterial().isReplaceable()
                || world.getBlockEntity(pos) instanceof TileEntityPortal tilePortal
                && tilePortal.setup && tilePortal.orange == isOrange)
        {
            if(sideHit.getAxis() == Direction.Axis.Y) //1 block portal
            {
                return isSideSolid(world, pos, sideHit);
            }
            else
            {
                BlockPos posDown = pos.below();
                return isSideSolid(world, pos, sideHit)
                        && (world.getBlockState(posDown).getMaterial().isReplaceable()
                        || world.getBlockEntity(posDown) instanceof TileEntityPortal tilePortal
                        && tilePortal.setup && tilePortal.orange == isOrange)
                        && isSideSolid(world, posDown, sideHit);
            }
        }
        return false;
    }

    private static boolean isSideSolid(Level world, BlockPos pos, Direction sideHit) {
        BlockPos offset = pos.offset(sideHit.getNormal().multiply(-1));
        BlockState blockState = world.getBlockState(offset);
        return blockState.isFaceSturdy(world, offset, sideHit);
    }


}
