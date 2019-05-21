package fi.dy.masa.tweakeroo.mixin;

import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Callbacks;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer
{
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
            cir.cancel();
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

    @Redirect(method = "getMouseOver",
                at = @At(value="INVOKE",
                         target="Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(" +
                                "Lnet/minecraft/entity/Entity;" +
                                "Lnet/minecraft/util/math/AxisAlignedBB;" +
                                "Ljava/util/function/Predicate;" +
                                ")Ljava/util/List;")
    )
    private List<Entity> ignoreDeadEntities(WorldClient client, Entity entity, AxisAlignedBB boundingbox, Predicate<Entity> predicate)
    {
        if (FeatureToggle.TWEAK_NO_DEAD_MOB_TARGETING.getBooleanValue())
            predicate = predicate.and(EntitySelectors.IS_ALIVE);

        return client.getEntitiesInAABBexcluding(entity, boundingbox, predicate);
    }
}
