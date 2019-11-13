package fi.dy.masa.tweakeroo.mixin;

import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityHopper.class)
public class MixinTileEntityHopper {
    @Redirect(method = "isInventoryFull", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxStackSize()I", ordinal = 0))
    private int getMaxStackSizeSidedInventory(ItemStack stack)
    {
        return (FeatureToggle.TWEAK_SHULKERBOX_STACKING.getBooleanValue() && isShulkerBox(stack) ? 1 : stack.getMaxStackSize());
    }

    @Redirect(method = "isInventoryFull", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxStackSize()I", ordinal = 1))
    private int getMaxStackSize(ItemStack stack)
    {
        return (FeatureToggle.TWEAK_SHULKERBOX_STACKING.getBooleanValue() && isShulkerBox(stack) ? 1 : stack.getMaxStackSize());
    }

    @Inject(method = "canCombine", at = @At("HEAD"), cancellable = true)
    private static void canCombine(ItemStack stack1, ItemStack stack2, CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_SHULKERBOX_STACKING.getBooleanValue() && isShulkerBox(stack1))
            cir.setReturnValue(false);
    }

    private static boolean isShulkerBox(ItemStack stack)
    {
        return stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock() instanceof BlockShulkerBox;
    }
}
