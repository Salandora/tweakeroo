package fi.dy.masa.tweakeroo.mixin;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;

import java.util.List;
import java.util.function.Predicate;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer
{
    @Shadow
    @Final
    private Minecraft mc;

    @Nullable
    private Entity renderViewEntityOriginal;

    @Inject(method = "renderWorld(FJ)V", at = @At("HEAD"), cancellable = true)
    private void onRenderWorld(CallbackInfo ci)
    {
        if (Callbacks.skipWorldRendering)
        {
            ci.cancel();
        }
    }

    @Inject(method = "getFOVModifier", at = @At("HEAD"), cancellable = true)
    private void zoom(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Double> cir)
    {
        if (FeatureToggle.TWEAK_ZOOM.getBooleanValue() && Hotkeys.ZOOM_ACTIVATE.getKeybind().isKeybindHeld())
        {
            cir.setReturnValue(Configs.Generic.ZOOM_FOV.getDoubleValue());
        }
        else if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            cir.setReturnValue(this.mc.gameSettings.fovSetting);
        }
    }

    @Inject(method = "renderRainSnow", at = @At("HEAD"), cancellable = true)
    private void cancelRainRender(float partialTicks, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_NO_RAIN_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "addRainParticles", at = @At("HEAD"), cancellable = true)
    private void cancelRainRender(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_NO_RAIN_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderWorld(FJ)V", at = @At(
                value = "INVOKE", shift = Shift.AFTER,
                target = "Lnet/minecraft/client/renderer/GameRenderer;getMouseOver(F)V"))
    private void overrideRenderViewEntityPre(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            Entity camera = CameraEntity.getCamera();

            if (camera != null)
            {
                this.renderViewEntityOriginal = this.mc.getRenderViewEntity();
                this.mc.setRenderViewEntity(camera);
            }
        }
    }

    @Inject(method = "renderWorld(FJ)V", at = @At("RETURN"))
    private void overrideRenderViewEntityPost(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && this.renderViewEntityOriginal != null)
        {
            this.mc.setRenderViewEntity(this.renderViewEntityOriginal);
            this.renderViewEntityOriginal = null;
        }
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void removeHandRendering(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "updateCameraAndRender(FJ)V", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/WorldRenderer;setupTerrain(Lnet/minecraft/entity/Entity;FLnet/minecraft/client/renderer/culling/ICamera;IZ)V"))
    private void preSetupTerrain(float partialTicks, long finishTimeNano, CallbackInfo ci)
    {
        MiscUtils.setFreeCameraSpectator(true);
    }

    @Inject(method = "updateCameraAndRender(FJ)V", at = @At(
                value = "INVOKE", shift = At.Shift.AFTER,
                target = "Lnet/minecraft/client/renderer/WorldRenderer;setupTerrain(Lnet/minecraft/entity/Entity;FLnet/minecraft/client/renderer/culling/ICamera;IZ)V"))
    private void postSetupTerrain(float partialTicks, long finishTimeNano, CallbackInfo ci)
    {
        MiscUtils.setFreeCameraSpectator(false);
    }
}
