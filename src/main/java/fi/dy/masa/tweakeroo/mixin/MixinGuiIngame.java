package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame extends Gui
{
    @Inject(method = "renderAttackIndicator", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/GameSettings;showDebugInfo:Z", ordinal = 0), cancellable = true)
    private void overrideCursorRender(float partialTicks, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_F3_CURSOR.getBooleanValue())
        {
            RenderUtils.renderDirectionsCursor(this.zLevel, partialTicks);
            ci.cancel();
        }
    }

    @Inject(method = "renderPumpkinOverlay", at = @At(value="HEAD"), cancellable = true)
    private void overridePumpkinOverlay(CallbackInfo ci) {
        if (FeatureToggle.TWEAK_NO_PUMPKIN_OVERLAY.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
