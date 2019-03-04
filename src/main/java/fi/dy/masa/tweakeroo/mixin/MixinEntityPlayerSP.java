package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.authlib.GameProfile;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;
import net.minecraft.world.World;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer
{
    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile)
    {
        super(worldIn, playerProfile);
    }

    @Shadow
    public MovementInput movementInput;

    @Redirect(method = "livingTick()V",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/gui/GuiScreen;doesGuiPauseGame()Z"))
    private boolean onDoesGuiPauseGame(GuiScreen gui)
    {
        // Spoof the return value to prevent entering the if block
        if (FeatureToggle.TWEAK_NO_PORTAL_GUI_CLOSING.getBooleanValue())
        {
            return true;
        }

        return gui.doesGuiPauseGame();
    }

    @Inject(method = "livingTick", at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/network/NetHandlerPlayClient;sendPacket(Lnet/minecraft/network/Packet;)V"))
    private void fixElytraDeployment(CallbackInfo ci)
    {
        if (Configs.Fixes.ELYTRA_FIX.getBooleanValue())
        {
            this.setFlag(7, true);
        }
    }

    @Inject(method = "livingTick", at = @At(value = "FIELD",
            target = "Lnet/minecraft/entity/player/PlayerCapabilities;allowFlying:Z", ordinal = 1))
    private void overrideSprint(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_PERMANENT_SPRINT.getBooleanValue() &&
            ! this.isSprinting() && ! this.isHandActive() && this.movementInput.moveForward >= 0.8F &&
            (this.getFoodStats().getFoodLevel() > 6.0F || this.abilities.allowFlying) &&
            ! this.isPotionActive(MobEffects.BLINDNESS))
        {
            this.setSprinting(true);
        }
    }

    @Redirect(method = "livingTick", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z"))
    private boolean overrideCollidedHorizontally(EntityPlayerSP player)
    {
        if (FeatureToggle.TWEAK_NO_WALL_UNSPRINT.getBooleanValue())
        {
            return false;
        }

        return player.collidedHorizontally;
    }
}
