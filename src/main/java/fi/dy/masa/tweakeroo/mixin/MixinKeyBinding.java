package fi.dy.masa.tweakeroo.mixin;

import fi.dy.masa.tweakeroo.util.IKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding implements IKeyBinding
{
    @Accessor("keyCode")
    public abstract InputMappings.Input getInput();
}
