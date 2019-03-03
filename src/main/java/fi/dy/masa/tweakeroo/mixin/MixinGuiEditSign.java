package fi.dy.masa.tweakeroo.mixin;

import fi.dy.masa.tweakeroo.util.IKeyBinding;
import net.minecraft.client.util.InputMappings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;

@Mixin(GuiEditSign.class)
public abstract class MixinGuiEditSign
{
    @Inject(method = "initGui", at = @At("HEAD"), cancellable = true)
    private void preventGuiOpen(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_NO_SIGN_GUI.getBooleanValue())
        {
            Minecraft mc = Minecraft.getInstance();
            mc.displayGuiScreen(null);

            // Update the keybind state, because opening the GUI resets them all.
            // Also, KeyBinding.updateKeyBindState() only works for keyboard keys
            InputMappings.Input key = ((IKeyBinding) mc.gameSettings.keyBindUseItem).getInput();
            KeyBinding.setKeyBindState(key, KeybindMulti.isKeyDown(key));

            ci.cancel();
        }
    }
}
