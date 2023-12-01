package me.ichun.mods.portalgunclassic.common.item;

import me.ichun.mods.portalgunclassic.common.entity.EntityPortalProjectile;
import me.ichun.mods.portalgunclassic.common.sounds.SoundRegistry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemPortalGun extends Item
{
    public ItemPortalGun(Item.Properties properties)
    {
        super(properties);
        /*
        setMaxStackSize(1);
        setHasSubtypes(true);
        setMaxDamage(0);
        setRegistryName(new ResourceLocation(PortalGunClassic.MOD_ID, "portalgun"));
        setUnlocalizedName("portalgunclassic.item.portalgun");
        setCreativeTab(CreativeTabs.TOOLS);
         */
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(!pLevel.isClientSide)
        {
            ItemStack is = pPlayer.getItemInHand(pUsedHand);
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getEyeY(), pPlayer.getZ(), isBlue(is) ? SoundRegistry.FIRE_BLUE.get() : SoundRegistry.FIRE_RED.get(), SoundSource.PLAYERS, 0.3F, 1.0F);
            pLevel.addFreshEntity(new EntityPortalProjectile(pLevel, pPlayer, isOrange(is)));
        }
        return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pUsedHand), pLevel.isClientSide);
    }

    public static boolean isBlue(ItemStack is) {
        return !isOrange(is);
    }

    public static boolean isOrange(ItemStack is) {
        return is.getTag() != null && is.getTag().getBoolean("Orange");
    }

    public static void setBlue(ItemStack is) {
        is.getOrCreateTag().putBoolean("Orange", false);
    }

    public static void setOrange(ItemStack is) {
        is.getOrCreateTag().putBoolean("Orange", true);
    }
}
