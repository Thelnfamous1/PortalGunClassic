package me.ichun.mods.portalgunclassic.common.sounds;

import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry
{

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
