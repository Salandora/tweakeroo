package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockObserver;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

@Mixin(value = BlockObserver.class, priority = 1001)
public abstract class MixinBlockObserver extends BlockDirectional
{
    public MixinBlockObserver(Properties propertiesIn)
    {
        super(propertiesIn);
    }

    @Inject(method = "startSignal", at = @At("HEAD"), cancellable = true)
    private void preventTrigger(IWorld worldIn, BlockPos pos, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_OBSERVER_DISABLE.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
