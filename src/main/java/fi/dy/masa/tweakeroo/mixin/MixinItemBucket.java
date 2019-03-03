package fi.dy.masa.tweakeroo.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ItemBucket.class)
public abstract class MixinItemBucket extends Item {

    public MixinItemBucket(Item.Properties builder) {
        super(builder);
    }

    /**
     * @author
     */
    @Overwrite
    protected ItemStack emptyBucket(ItemStack itemstack, EntityPlayer player)
    {
        if (player.abilities.isCreativeMode)
            return itemstack;

        itemstack.shrink(1);
        if (itemstack.isEmpty())
        {
            return new ItemStack(Items.BUCKET);
        }
        else
        {
            if (!player.inventory.addItemStackToInventory(new ItemStack(Items.BUCKET)))
            {
                player.dropItem(new ItemStack(Items.BUCKET), false);
            }

            return itemstack;
        }
    }
}
