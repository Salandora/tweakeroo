package fi.dy.masa.tweakeroo.mixin;

import net.minecraft.client.MainWindow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame extends Gui
{
    @Shadow
    @Final
    private GuiPlayerTabOverlay overlayPlayerList;

    @Shadow
    @Final
    private Minecraft mc;

    @Inject(method = "renderAttackIndicator", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/GameSettings;showDebugInfo:Z", ordinal = 0), cancellable = true)
    private void overrideCursorRender(float partialTicks, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_F3_CURSOR.getBooleanValue())
        {
            RenderUtils.renderDirectionsCursor(mc.mainWindow, this.zLevel, partialTicks);
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

    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;setVisible(Z)V",
                     ordinal = 0, shift = At.Shift.AFTER))
    private void alwaysRenderPlayerList(float partialTicks, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_PLAYER_LIST_ALWAYS_ON.getBooleanValue())
        {
            Scoreboard scoreboard = this.mc.world.getScoreboard();
            ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(0);
            MainWindow win = this.mc.mainWindow;
            int width = win.getScaledWidth();

            this.overlayPlayerList.setVisible(true);
            this.overlayPlayerList.render(width, scoreboard, objective);
        }
    }
}
