package fi.dy.masa.tweakeroo.mixin;

import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.tweaks.PlacementHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.IItemStackLimit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemBlock.class)
public abstract class MixinItemBlock extends Item implements IItemStackLimit
{
    @Shadow
    public abstract IBlockState getStateForPlacement(BlockItemUseContext context);

    public MixinItemBlock(Block blockIn, Item.Properties builder)
    {
        super(builder);
    }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        if (FeatureToggle.TWEAK_SHULKERBOX_STACKING.getBooleanValue() &&
            ((ItemBlock) (Object) this).getBlock() instanceof BlockShulkerBox &&
            InventoryUtils.shulkerBoxHasItems(stack) == false)
        {
            return 64;
        }

        // FIXME How to call the stack-sensitive version on the super class?
        return super.getMaxStackSize();
    }

    @Redirect(method = "tryPlace", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemBlock;getStateForPlacement(" +
                    "Lnet/minecraft/item/BlockItemUseContext;)" +
                    "Lnet/minecraft/block/state/IBlockState;"), require = 0)
    private IBlockState modifyPlacementState(ItemBlock block, BlockItemUseContext context)
    {
        IBlockState stateOriginal = this.getStateForPlacement(context);

        if (Configs.Generic.CLIENT_PLACEMENT_ROTATION.getBooleanValue())
        {
            PlacementHandler.UseContext ctx = PlacementHandler.UseContext.of(
                    context.getWorld(),
                    context.getPos(),
                    context.getFace(),
                    new Vec3d(context.getHitX(), context.getHitY(), context.getHitZ()),
                    context.getPlayer(),
                    null);
            return PlacementHandler.getStateForPlacement(stateOriginal, ctx);
        }

        return stateOriginal;
    }
}
