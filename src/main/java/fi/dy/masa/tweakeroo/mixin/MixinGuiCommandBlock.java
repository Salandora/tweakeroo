/*
package fi.dy.masa.tweakeroo.mixin;

import java.util.Arrays;

import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;

@Mixin(GuiCommandBlock.class)
public abstract class MixinGuiCommandBlock extends GuiCommandBlockBase
{
    @Shadow
    @Final
    private TileEntityCommandBlock commandBlock;

    //@Shadow protected GuiButton doneButton;
    //@Shadow protected GuiButton cancelButton;
    @Shadow private GuiButton modeBtn;
    @Shadow private GuiButton conditionalBtn;
    @Shadow private GuiButton autoExecBtn;

    private GuiTextField textFieldName;
    private GuiButton buttonUpdateExec;
    private boolean updateExecValue;

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

            str = this.getDisplayStringForCurrentStatus();
            width = this.mc.fontRenderer.getStringWidth(str) + 10;
            //this.buttonUpdateExec = new GuiButton(102, x2 + widthBtn + 4, y, width, 20, str);

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
            this.buttonUpdateExec.displayString = this.getDisplayStringForCurrentStatus();
            this.buttonUpdateExec.setWidth(this.mc.fontRenderer.getStringWidth(this.buttonUpdateExec.displayString) + 10);
        }
    }

    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void onKeyTyped(char typedChar, int keyCode, CallbackInfo ci)
    {
        if (this.textFieldName != null)
        {
            this.textFieldName.textboxKeyTyped(typedChar, keyCode);
        }
    }


    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void onMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci)
    {
        if (this.textFieldName != null)
        {
            this.textFieldName.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }


    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (this.textFieldName != null)
        {
            this.textFieldName.drawTextField(mouseX, mouseY, partialTicks);
        }

        if (this.buttonUpdateExec != null && this.buttonUpdateExec.isMouseOver())
        {
            String hover = "tweakeroo.gui.button.misc.command_block.hover.update_execution";
            RenderUtils.drawHoverText(mouseX, mouseY, Arrays.asList(I18n.format(hover)));
        }
    }

    public void handleButton(GuiButton button)
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
                    this.buttonUpdateExec.displayString = this.getDisplayStringForCurrentStatus();
                    this.buttonUpdateExec.setWidth(this.mc.fontRenderer.getStringWidth(this.buttonUpdateExec.displayString) + 10);

                    String cmd = String.format("/blockdata %d %d %d {\"UpdateLastExecution\":%s}",
                            pos.getX(), pos.getY(), pos.getZ(), this.updateExecValue ? "1b" : "0b");
                    player.sendChatMessage(cmd);
                }
            }
        }
    }

    private String getDisplayStringForCurrentStatus()
    {
        String translationKey = "tweakeroo.gui.button.misc.command_block.update_execution";
        boolean isCurrentlyOn = ! this.updateExecValue;
        String strStatus = "malilib.gui.label_colored." + (isCurrentlyOn ? "on" : "off");
        return I18n.format(translationKey, I18n.format(strStatus));
    }
}
*/
