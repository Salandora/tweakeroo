package fi.dy.masa.tweakeroo.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.gui.button.ButtonOnOff;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

@Mixin(GuiCommandBlock.class)
public abstract class MixinGuiCommandBlock extends GuiScreen
{
    @Shadow
    @Final
    private TileEntityCommandBlock commandBlock;

    @Shadow protected GuiButton doneButton;
    @Shadow protected GuiButton cancelButton;
    @Shadow private GuiButton modeBtn;
    @Shadow private GuiButton conditionalBtn;
    @Shadow private GuiButton autoExecBtn;

    private GuiTextField textFieldName;
    private ButtonOnOff buttonUpdateExec;
    private boolean updateExecValue;

    private static ButtonOnOff createOnOff(int x, int y, int btnWidth, boolean rightAlign, String translationKey, boolean isCurrentlyOn, Consumer<GuiButton> consumer, String... hoverStrings)
    {
        if (btnWidth < 0)
        {
            Minecraft mc = Minecraft.getInstance();
            int w1 = mc.fontRenderer.getStringWidth(ButtonOnOff.getDisplayStringForStatus(translationKey, true));
            int w2 = mc.fontRenderer.getStringWidth(ButtonOnOff.getDisplayStringForStatus(translationKey, false));
            btnWidth = Math.max(w1, w2) + 10;
        }

        if (rightAlign)
        {
            x -= btnWidth;
        }

        return new ButtonOnOff(x, y, btnWidth, 20, translationKey, isCurrentlyOn, hoverStrings) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                consumer.accept(this);
            }
        };
    }

    @Inject(method = "initGui", at = @At("RETURN"))
    private void addExtraFields(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_COMMAND_BLOCK_EXTRA_FIELDS.getBooleanValue())
        {
            int x1 = this.width / 2 - 152;
            int x2 = x1 + 204;
            int y = 158;
            int width = 200;

            // Move the vanilla buttons a little bit tighter, otherwise the large GUI scale is a mess
            this.modeBtn.y = y;
            this.conditionalBtn.y = y;
            this.autoExecBtn.y = y;

            y += 46;
            this.doneButton.y = y;
            this.cancelButton.y = y;

            String str = I18n.format("tweakeroo.gui.button.misc.command_block.set_name");
            int widthBtn = this.fontRenderer.getStringWidth(str) + 10;

            y = 181;
            this.textFieldName = new GuiTextField(100, this.fontRenderer, x1, y, width, 20);
            this.textFieldName.setText(this.commandBlock.getCommandBlockLogic().getName().getString());
            this.children.add(this.textFieldName);

            this.addButton(new GuiButton(101, x2, y, widthBtn, 20, str) {
                @Override
                public void onClick(double mouseX, double mouseY) {
                    handleButton(this);
                }
            });

            this.updateExecValue = MiscUtils.getUpdateExec(this.commandBlock);
            str = "tweakeroo.gui.button.misc.command_block.update_execution";
            String hover = "tweakeroo.gui.button.misc.command_block.hover.update_execution";
            this.buttonUpdateExec = createOnOff(x2 + widthBtn + 4, y, -1, false, str, ! this.updateExecValue, this::handleButton, hover);
            this.buttonUpdateExec.id = 102;
            this.addButton(this.buttonUpdateExec);
        }
    }

    // This is needed because otherwise the name updating is delayed by "one GUI opening" >_>
    @Inject(method = "updateGui", at = @At("RETURN"))
    private void onUpdateGui(CallbackInfo ci)
    {
        if (this.textFieldName != null)
        {
            this.textFieldName.setText(this.commandBlock.getCommandBlockLogic().getName().toString());
        }

        if (this.buttonUpdateExec != null)
        {
            this.updateExecValue = MiscUtils.getUpdateExec(this.commandBlock);
            this.buttonUpdateExec.updateDisplayString(! this.updateExecValue);
        }
    }

    /*@Inject(method = "keyPressed", at = @At("RETURN"))
    private void onKeyTyped(char typedChar, int keyCode, CallbackInfo ci)
    {
        if (this.textFieldName != null)
        {
            this.textFieldName.textboxKeyTyped(typedChar, keyCode);
        }
    }*/

    /*@Inject(method = "mouseClicked", at = @At("RETURN"))
    private void onMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci)
    {
        if (this.textFieldName != null)
        {
            this.textFieldName.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }*/

    @Inject(method = "render", at = @At("RETURN"))
    private void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci)
    {
        if (this.textFieldName != null)
        {
            this.textFieldName.drawTextField(mouseX, mouseY, partialTicks);
        }

        if (this.buttonUpdateExec != null && this.buttonUpdateExec.isMouseOver())
        {
            RenderUtils.drawHoverText(mouseX, mouseY, this.buttonUpdateExec.getHoverStrings());
        }
    }

    private void handleButton(GuiButton button)
    {
        if (FeatureToggle.TWEAK_COMMAND_BLOCK_EXTRA_FIELDS.getBooleanValue())
        {
            EntityPlayerSP player = this.mc.player;

            if (player != null)
            {
                BlockPos pos = this.commandBlock.getPos();

                // Set name
                if (button.id == 101 && this.textFieldName != null)
                {
                    String name = this.textFieldName.getText();
                    player.sendChatMessage(String.format("/blockdata %d %d %d {\"CustomName\":\"%s\"}", pos.getX(), pos.getY(), pos.getZ(), name));
                }
                // Toggle Update Last Execution
                else if (button.id == 102 && this.buttonUpdateExec != null)
                {
                    this.updateExecValue = ! this.updateExecValue;
                    this.buttonUpdateExec.updateDisplayString(! this.updateExecValue);

                    String cmd = String.format("/blockdata %d %d %d {\"UpdateLastExecution\":%s}",
                            pos.getX(), pos.getY(), pos.getZ(), this.updateExecValue ? "1b" : "0b");
                    player.sendChatMessage(cmd);
                }
            }
        }
    }
}
