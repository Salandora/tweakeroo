package fi.dy.masa.tweakeroo.util;

import net.minecraft.item.ItemStack;

public interface IItemStackLimit
{
    default int getMaxStackSize(ItemStack stack) { return 0; };

    void setMaxStackSize(int value);
}
