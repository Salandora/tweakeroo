package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import fi.dy.masa.tweakeroo.util.IItemStackLimit;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public abstract class MixinItem implements IItemStackLimit
{
    @Shadow
    public int getMaxStackSize() { return 0; }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        return this.getMaxStackSize();
    }

    @Accessor("maxStackSize")
    public abstract void setMaxStackSize(int value);
}
