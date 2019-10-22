package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(TileEntityRendererDispatcher.class)
public abstract class MixinTileEntityRendererDispatcher
{
    @Inject(method = "render(Lnet/minecraft/tileentity/TileEntity;FI)V", at = @At("HEAD"), cancellable = true)
    private void preventTileEntityRendering(TileEntity tileentityIn, float partialTicks, int destroyStage, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "render(Lnet/minecraft/tileentity/TileEntity;DDDF)V", at = @At("HEAD"), cancellable = true)
    private void preventTileEntityRendering(TileEntity tileEntityIn, double x, double y, double z, float partialTicks, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "render(Lnet/minecraft/tileentity/TileEntity;DDDFIZ)V", at = @At("HEAD"), cancellable = true)
    private void preventTileEntityRendering(TileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage, boolean hasNoBlock, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Redirect(method = "render(Lnet/minecraft/tileentity/TileEntity;FI)V",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/tileentity/TileEntity;getMaxRenderDistanceSquared()D"))
    private double overrideRenderDistance(TileEntity tileEntityIn)
    {
        if (FeatureToggle.TWEAK_TILE_RENDER_DISTANCE.getBooleanValue())
        {
            return Double.MAX_VALUE;
        }

        return tileEntityIn.getMaxRenderDistanceSquared();
    }
}
