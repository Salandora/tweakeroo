package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.network.NetHandlerPlayServer;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer
{
/*    @ModifyConstant(method = "processUpdateStructureBlock",
            slice = @Slice(from = @At(value = "INVOKE",
                                      target = "Lnet/minecraft/tileentity/TileEntityStructure;setPosition(Lnet/minecraft/util/math/BlockPos;)V"),
                           to = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/tileentity/TileEntityStructure;setSize(Lnet/minecraft/util/math/BlockPos;)V")),
            constant = @Constant(intValue = 32))
    private int overrideStructureBlockSizeLimit(int original)
    {
        if (FeatureToggle.TWEAK_STRUCTURE_BLOCK_LIMIT.getBooleanValue())
        {
            return Configs.Generic.STRUCTURE_BLOCK_MAX_SIZE.getIntegerValue();
        }

        return original;
    }*/
}
