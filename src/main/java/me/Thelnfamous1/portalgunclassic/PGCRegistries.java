package me.Thelnfamous1.portalgunclassic;

import com.mojang.datafixers.types.Type;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.block.BlockPortal;
import me.ichun.mods.portalgunclassic.common.entity.EntityPortalProjectile;
import me.ichun.mods.portalgunclassic.common.item.ItemPortalCore;
import me.ichun.mods.portalgunclassic.common.item.ItemPortalGun;
import me.ichun.mods.portalgunclassic.common.tileentity.TileEntityPortal;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PGCRegistries {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PortalGunClassic.MOD_ID);

    public static final RegistryObject<BlockEntityType<TileEntityPortal>> TILE_PORTAL = register("tile_portal", BlockEntityType.Builder.of(TileEntityPortal::new, PGCRegistries.BLOCK_PORTAL.get()));

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String pKey, BlockEntityType.Builder<T> pBuilder) {
        Type<?> type = Util.fetchChoiceType(References.BLOCK_ENTITY, new ResourceLocation(PortalGunClassic.MOD_ID, pKey).toString());
        return BLOCK_ENTITY_TYPES.register(pKey, () -> pBuilder.build(type));
    }

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PortalGunClassic.MOD_ID);

    public static final RegistryObject<EntityType<EntityPortalProjectile>> PORTAL_PROJECTILE = register("portal_projectile",
            EntityType.Builder.<EntityPortalProjectile>of(EntityPortalProjectile::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String pKey, EntityType.Builder<T> pBuilder) {
        return ENTITY_TYPES.register(pKey, () -> pBuilder.build(new ResourceLocation(PortalGunClassic.MOD_ID, pKey).toString()));
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PortalGunClassic.MOD_ID);

    public static final RegistryObject<Item> PORTAL_GUN = ITEMS.register("portalgun", () -> new ItemPortalGun(new Item.Properties().stacksTo(1).durability(0).tab(CreativeModeTab.TAB_TOOLS)));

    public static final RegistryObject<Item> PORTAL_CORE = ITEMS.register("portal_core", () -> new ItemPortalCore(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PortalGunClassic.MOD_ID);

    public static final RegistryObject<Block> BLOCK_PORTAL = BLOCKS.register("portal", () ->
            new BlockPortal(BlockBehaviour.Properties.of(Material.DECORATION)
                    .strength(-1F, 1000000.0F)
                    .lightLevel(state -> 8 /* 0.5F? */)));

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PortalGunClassic.MOD_ID);

    public static final RegistryObject<SoundEvent> ENTER = register("enter");
    public static final RegistryObject<SoundEvent> EXIT = register("exit");
    public static final RegistryObject<SoundEvent> FIZZLE = register("fizzle");
    public static final RegistryObject<SoundEvent> INVALID = register("invalid");
    public static final RegistryObject<SoundEvent> OPEN_BLUE = register("openblue");
    public static final RegistryObject<SoundEvent> OPEN_RED = register("openred");
    public static final RegistryObject<SoundEvent> FIRE_BLUE = register("fireblue");
    public static final RegistryObject<SoundEvent> FIRE_RED = register("firered");
    public static final RegistryObject<SoundEvent> RESET = register("reset");
    public static final RegistryObject<SoundEvent> ACTIVE = register("active");

    private static RegistryObject<SoundEvent> register(String path){
        return SOUNDS.register(path, () -> new SoundEvent(new ResourceLocation(PortalGunClassic.MOD_ID, path)));
    }
}
