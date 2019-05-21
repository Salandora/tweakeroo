package fi.dy.masa.tweakeroo.mixin;

import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.NetherDimension;
import org.spongepowered.asm.mixin.Mixin;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(NetherDimension.class)
public abstract class MixinNetherDimension extends Dimension
{
    @Override
    public boolean doesXZShowFog(int x, int z)
    {
        return ! FeatureToggle.TWEAK_NO_NETHER_FOG.getBooleanValue();
    }
}
