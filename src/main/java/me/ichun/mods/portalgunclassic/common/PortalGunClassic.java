package me.ichun.mods.portalgunclassic.common;

import com.mojang.datafixers.types.Type;
import me.ichun.mods.portalgunclassic.client.core.EventHandlerClient;
import me.ichun.mods.portalgunclassic.client.core.ProxyClient;
import me.ichun.mods.portalgunclassic.common.block.BlockPortal;
import me.ichun.mods.portalgunclassic.common.core.EventHandlerServer;
import me.ichun.mods.portalgunclassic.common.core.ProxyCommon;
import me.ichun.mods.portalgunclassic.common.entity.EntityPortalProjectile;
import me.ichun.mods.portalgunclassic.common.item.ItemPortalCore;
import me.ichun.mods.portalgunclassic.common.item.ItemPortalGun;
import me.ichun.mods.portalgunclassic.common.tileentity.TileEntityPortal;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(PortalGunClassic.MOD_ID)
public class PortalGunClassic
{
    public static final String MOD_ID = "portalgunclassic";
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final RegistryObject<Block> BLOCK_PORTAL = BLOCKS.register("portal", () ->
            new BlockPortal(BlockBehaviour.Properties.of(Material.DECORATION)
                    .strength(-1F, 1000000.0F)
                    .lightLevel(state -> 8 /* 0.5F? */)
                    .noOcclusion()
                    .noCollission()));
    public static final RegistryObject<BlockEntityType<TileEntityPortal>> TILE_PORTAL = register("tile_portal", BlockEntityType.Builder.of(TileEntityPortal::new, BLOCK_PORTAL.get()));
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Item> PORTAL_GUN = ITEMS.register("portalgun", () -> new ItemPortalGun(new Item.Properties().stacksTo(1).durability(0).tab(CreativeModeTab.TAB_TOOLS)));
    public static final RegistryObject<Item> PORTAL_CORE = ITEMS.register("portal_core", () -> new ItemPortalCore(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final String MOD_NAME = "PortalGunClassic";
    public static final String VERSION = "1.0.0";
    public static final RegistryObject<EntityType<EntityPortalProjectile>> PORTAL_PROJECTILE = register("portal_projectile",
            EntityType.Builder.<EntityPortalProjectile>of(EntityPortalProjectile::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F));

    public static PortalGunClassic instance;

    //@SidedProxy(clientSide = "me.ichun.mods.portalgunclassic.client.core.ProxyClient", serverSide = "me.ichun.mods.portalgunclassic.common.core.ProxyCommon")
    public static ProxyCommon proxy = DistExecutor.safeRunForDist(() -> ProxyClient::new, () -> ProxyCommon::new);

    public static EventHandlerClient eventHandlerClient;
    public static EventHandlerServer eventHandlerServer;

    //public static Item itemPortalGun;
    //public static Item itemPortalCore;

    //public static Block blockPortalGun;

    public static SimpleChannel channel;

    public PortalGunClassic(){
        instance = this;
        this.onPreInit();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String pKey, BlockEntityType.Builder<T> pBuilder) {
        Type<?> type = Util.fetchChoiceType(References.BLOCK_ENTITY, new ResourceLocation(MOD_ID, pKey).toString());
        return BLOCK_ENTITY_TYPES.register(pKey, () -> pBuilder.build(type));
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String pKey, EntityType.Builder<T> pBuilder) {
        return ENTITY_TYPES.register(pKey, () -> pBuilder.build(new ResourceLocation(MOD_ID, pKey).toString()));
    }

    //@Mod.EventHandler
    public void onPreInit()
    {
        proxy.preInit();
    }
}
