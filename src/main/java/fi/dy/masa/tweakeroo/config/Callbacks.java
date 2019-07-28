package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.IConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.IConfigInteger;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeyCallbackAdjustable;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.gui.GuiConfigs;
import fi.dy.masa.tweakeroo.mixin.IMixinBlock;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.IItemStackLimit;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import fi.dy.masa.tweakeroo.util.PlacementRestrictionMode;
import fi.dy.masa.tweakeroo.util.SnapAimMode;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

import java.util.Arrays;

public class Callbacks
{
    public static boolean skipWorldRendering;

    public static void init(Minecraft mc)
    {
        FeatureToggle.TWEAK_GAMMA_OVERRIDE.setValueChangeCallback(new FeatureCallbackGamma(FeatureToggle.TWEAK_GAMMA_OVERRIDE, mc));
        Configs.Disable.DISABLE_SLIME_BLOCK_SLOWDOWN.setValueChangeCallback(new FeatureCallbackSlime(Configs.Disable.DISABLE_SLIME_BLOCK_SLOWDOWN));

        FeatureCallbackSpecial featureCallback = new FeatureCallbackSpecial();
        FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getKeybind().setCallback(new KeyCallbackToggleFastMode(FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT));
        FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.setValueChangeCallback(featureCallback);
        FeatureToggle.TWEAK_FREE_CAMERA.setValueChangeCallback(featureCallback);
        FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.setValueChangeCallback(featureCallback);

        IHotkeyCallback callbackGeneric = new KeyCallbackHotkeysGeneric(mc);
        IHotkeyCallback callbackMessage = new KeyCallbackHotkeyWithMessage(mc);

        Hotkeys.BREAKING_RESTRICTION_MODE_COLUMN.getKeybind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_DIAGONAL.getKeybind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_FACE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_LAYER.getKeybind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_LINE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_PLANE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.COPY_SIGN_TEXT.getKeybind().setCallback(callbackGeneric);
        Hotkeys.FLY_PRESET_1.getKeybind().setCallback(callbackGeneric);
        Hotkeys.FLY_PRESET_2.getKeybind().setCallback(callbackGeneric);
        Hotkeys.FLY_PRESET_3.getKeybind().setCallback(callbackGeneric);
        Hotkeys.FLY_PRESET_4.getKeybind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SWAP_1.getKeybind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SWAP_2.getKeybind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SWAP_3.getKeybind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SCROLL.getKeybind().setCallback(callbackGeneric);
        Hotkeys.OPEN_CONFIG_GUI.getKeybind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_COLUMN.getKeybind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_DIAGONAL.getKeybind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_FACE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_LAYER.getKeybind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_LINE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_PLANE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.TOOL_PICK.getKeybind().setCallback(callbackGeneric);

        Hotkeys.SKIP_ALL_RENDERING.getKeybind().setCallback(callbackMessage);
        Hotkeys.SKIP_WORLD_RENDERING.getKeybind().setCallback(callbackMessage);

        FeatureToggle.TWEAK_AFTER_CLICKER.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_AFTER_CLICKER));
        FeatureToggle.TWEAK_BREAKING_GRID.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_BREAKING_GRID));
        FeatureToggle.TWEAK_FLY_SPEED.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_FLY_SPEED));
        FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE));
        FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER));
        FeatureToggle.TWEAK_PLACEMENT_GRID.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_PLACEMENT_GRID));
        FeatureToggle.TWEAK_PLACEMENT_LIMIT.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_PLACEMENT_LIMIT));
        FeatureToggle.TWEAK_SNAP_AIM.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_SNAP_AIM));
        FeatureToggle.TWEAK_ZOOM.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_ZOOM));

        Configs.Generic.POTION_STACKING.setValueChangeCallback(new ConfigSetMaxStackSize(Items.POTION, Items.LINGERING_POTION, Items.SPLASH_POTION));
        Configs.Generic.MINECART_STACKING.setValueChangeCallback(new ConfigSetMaxStackSize(Items.MINECART, Items.CHEST_MINECART, Items.FURNACE_MINECART, Items.TNT_MINECART, Items.HOPPER_MINECART));
        Configs.Generic.FILLED_BUCKETS_STACKING.setValueChangeCallback(new ConfigSetMaxStackSize(Items.LAVA_BUCKET, Items.WATER_BUCKET, Items.MILK_BUCKET, Items.COD_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET, Items.TROPICAL_FISH_BUCKET));
        Configs.Generic.MORE_EMPTY_BUCKETS.setValueChangeCallback(new ConfigSetMaxStackSize(Items.BUCKET));
        Configs.Generic.MORE_ENDER_PEARLS.setValueChangeCallback(new ConfigSetMaxStackSize(Items.ENDER_PEARL));
    }

    public static class FeatureCallbackGamma implements IValueChangeCallback<IConfigBoolean>
    {
        private final Minecraft mc;

        public FeatureCallbackGamma(FeatureToggle feature, Minecraft mc)
        {
            this.mc = mc;

            if (this.mc.gameSettings.gammaSetting <= 1.0F)
            {
                Configs.Internal.GAMMA_VALUE_ORIGINAL.setDoubleValue(this.mc.gameSettings.gammaSetting);
            }

            // If the feature is enabled on game launch, apply it here
            if (feature.getBooleanValue())
            {
                this.mc.gameSettings.gammaSetting = Configs.Generic.GAMMA_OVERRIDE_VALUE.getIntegerValue();
            }
        }

        @Override
        public void onValueChanged(IConfigBoolean config)
        {
            if (config.getBooleanValue())
            {
                Configs.Internal.GAMMA_VALUE_ORIGINAL.setDoubleValue(this.mc.gameSettings.gammaSetting);
                this.mc.gameSettings.gammaSetting = Configs.Generic.GAMMA_OVERRIDE_VALUE.getIntegerValue();
            }
            else
            {
                this.mc.gameSettings.gammaSetting = (float) Configs.Internal.GAMMA_VALUE_ORIGINAL.getDoubleValue();
            }
        }
    }

    public static class FeatureCallbackSlime implements IValueChangeCallback<ConfigBoolean>
    {
        public FeatureCallbackSlime(ConfigBoolean feature)
        {
            Configs.Internal.SLIME_BLOCK_SLIPPERINESS_ORIGINAL.setDoubleValue(Blocks.SLIME_BLOCK.getSlipperiness());

            // If the feature is enabled on game launch, apply the overridden value here
            if (feature.getBooleanValue())
            {
                ((IMixinBlock) Blocks.SLIME_BLOCK).setSlipperiness(Blocks.STONE.getSlipperiness());
            }
        }

        @Override
        public void onValueChanged(ConfigBoolean config)
        {
            if (config.getBooleanValue())
            {
                ((IMixinBlock) Blocks.SLIME_BLOCK).setSlipperiness(Blocks.STONE.getSlipperiness());
            }
            else
            {
                ((IMixinBlock) Blocks.SLIME_BLOCK).setSlipperiness((float) Configs.Internal.SLIME_BLOCK_SLIPPERINESS_ORIGINAL.getDoubleValue());
            }
        }
    }

    public static class FeatureCallbackSpecial implements IValueChangeCallback<IConfigBoolean>
    {
        public FeatureCallbackSpecial()
        {
        }

        @Override
        public void onValueChanged(IConfigBoolean config)
        {
            if (config == FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT &&
                Configs.Generic.PLACEMENT_RESTRICTION_TIED_TO_FAST.getBooleanValue())
            {
                FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.setBooleanValue(config.getBooleanValue());
            }
            else if (config == FeatureToggle.TWEAK_FREE_CAMERA)
            {
                if (config.getBooleanValue())
                {
                    CameraEntity.createCamera(Minecraft.getInstance());
                }
                else
                {
                    CameraEntity.removeCamera();
                }

                if (Configs.Generic.FREE_CAMERA_MOTION_TOGGLE.getBooleanValue())
                {
                    FeatureToggle.TWEAK_FREE_CAMERA_MOTION.setBooleanValue(config.getBooleanValue());
                }
            }
        }
    }

    public static class KeyCallbackHotkeyWithMessage implements IHotkeyCallback
    {
        private final Minecraft mc;

        public KeyCallbackHotkeyWithMessage(Minecraft mc)
        {
            this.mc = mc;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            if (key == Hotkeys.SKIP_ALL_RENDERING.getKeybind())
            {
                this.mc.skipRenderWorld = ! this.mc.skipRenderWorld;

                String pre = mc.skipRenderWorld ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
                String status = StringUtils.translate("tweakeroo.message.value." + (this.mc.skipRenderWorld ? "on" : "off"));
                String message = StringUtils.translate("tweakeroo.message.toggled", "Skip All Rendering", pre + status + GuiBase.TXT_RST);
                InfoUtils.printActionbarMessage(message);
            }
            else if (key == Hotkeys.SKIP_WORLD_RENDERING.getKeybind())
            {
                skipWorldRendering = ! skipWorldRendering;

                boolean enabled = skipWorldRendering;
                String pre = enabled ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
                String status = StringUtils.translate("tweakeroo.message.value." + (enabled ? "on" : "off"));
                String message = StringUtils.translate("tweakeroo.message.toggled", "Skip World Rendering", pre + status + GuiBase.TXT_RST);
                InfoUtils.printActionbarMessage(message);
            }

            return true;
        }
    }

    private static class KeyCallbackHotkeysGeneric implements IHotkeyCallback
    {
        private final Minecraft mc;

        public KeyCallbackHotkeysGeneric(Minecraft mc)
        {
            this.mc = mc;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            if (key == Hotkeys.TOOL_PICK.getKeybind())
            {
                if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.type == RayTraceResult.Type.BLOCK)
                {
                    InventoryUtils.trySwitchToEffectiveTool(this.mc.objectMouseOver.getBlockPos());
                    return true;
                }
            }
            else if (key == Hotkeys.COPY_SIGN_TEXT.getKeybind())
            {
                RayTraceResult trace = this.mc.objectMouseOver;

                if (trace != null && trace.type == RayTraceResult.Type.BLOCK)
                {
                    BlockPos pos = trace.getBlockPos();
                    TileEntity te = this.mc.world.getTileEntity(pos);

                    if (te instanceof TileEntitySign)
                    {
                        MiscUtils.copyTextFromSign((TileEntitySign) te);
                        InfoUtils.printActionbarMessage("tweakeroo.message.sign_text_copied");
                    }
                }
                return true;
            }
            else if (key == Hotkeys.HOTBAR_SWAP_1.getKeybind())
            {
                if (FeatureToggle.TWEAK_HOTBAR_SWAP.getBooleanValue())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 0);
                    return true;
                }
            }
            else if (key == Hotkeys.HOTBAR_SWAP_2.getKeybind())
            {
                if (FeatureToggle.TWEAK_HOTBAR_SWAP.getBooleanValue())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 1);
                    return true;
                }
            }
            else if (key == Hotkeys.HOTBAR_SWAP_3.getKeybind())
            {
                if (FeatureToggle.TWEAK_HOTBAR_SWAP.getBooleanValue())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 2);
                    return true;
                }
            }
            else if (key == Hotkeys.FLY_PRESET_1.getKeybind())
            {
                this.setFlySpeedPreset(0);
                return true;
            }
            else if (key == Hotkeys.FLY_PRESET_2.getKeybind())
            {
                this.setFlySpeedPreset(1);
                return true;
            }
            else if (key == Hotkeys.FLY_PRESET_3.getKeybind())
            {
                this.setFlySpeedPreset(2);
                return true;
            }
            else if (key == Hotkeys.FLY_PRESET_4.getKeybind())
            {
                this.setFlySpeedPreset(3);
                return true;
            }
            else if (key == Hotkeys.HOTBAR_SCROLL.getKeybind())
            {
                if (FeatureToggle.TWEAK_HOTBAR_SCROLL.getBooleanValue())
                {
                    int currentRow = Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW.getIntegerValue();
                    InventoryUtils.swapHotbarWithInventoryRow(mc.player, currentRow);
                    return true;
                }
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_COLUMN.getKeybind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.COLUMN);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_DIAGONAL.getKeybind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.DIAGONAL);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_FACE.getKeybind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.FACE);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_LAYER.getKeybind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.LAYER);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_LINE.getKeybind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.LINE);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_PLANE.getKeybind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.PLANE);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_COLUMN.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.COLUMN);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_DIAGONAL.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.DIAGONAL);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_FACE.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.FACE);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_LAYER.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.LAYER);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_LINE.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.LINE);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_PLANE.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.PLANE);
                return true;
            }
            else if (key == Hotkeys.OPEN_CONFIG_GUI.getKeybind())
            {
                GuiBase.openGui(new GuiConfigs());
                return true;
            }

            return false;
        }

        private void setFlySpeedPreset(int preset)
        {
            Configs.Internal.FLY_SPEED_PRESET.setIntegerValue(preset);

            float speed = (float) Configs.getActiveFlySpeedConfig().getDoubleValue();
            String strPreset = GuiBase.TXT_GREEN + (preset + 1) + GuiBase.TXT_RST;
            String strSpeed = String.format("%s%.3f%s", GuiBase.TXT_GREEN, speed, GuiBase.TXT_RST);
            InfoUtils.printActionbarMessage("tweakeroo.message.set_fly_speed_preset_to", strPreset, strSpeed);
        }

        private void setBreakingRestrictionMode(PlacementRestrictionMode mode)
        {
            Configs.Generic.BREAKING_RESTRICTION_MODE.setOptionListValue(mode);

            String str = GuiBase.TXT_GREEN + mode.name() + GuiBase.TXT_RST;
            InfoUtils.printActionbarMessage("tweakeroo.message.set_breaking_restriction_mode_to", str);
        }

        private void setPlacementRestrictionMode(PlacementRestrictionMode mode)
        {
            Configs.Generic.PLACEMENT_RESTRICTION_MODE.setOptionListValue(mode);

            String str = GuiBase.TXT_GREEN + mode.name() + GuiBase.TXT_RST;
            InfoUtils.printActionbarMessage("tweakeroo.message.set_placement_restriction_mode_to", str);
        }
    }

    private static class KeyCallbackToggleFastMode implements IHotkeyCallback
    {
        private final FeatureToggle feature;

        private KeyCallbackToggleFastMode(FeatureToggle feature)
        {
            this.feature = feature;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            this.feature.toggleBooleanValue();

            boolean enabled = this.feature.getBooleanValue();
            String strStatus = StringUtils.translate("tweakeroo.message.value." + (enabled ? "on" : "off"));
            String preGreen = GuiBase.TXT_GREEN;
            String preRed = GuiBase.TXT_RED;
            String rst = GuiBase.TXT_RST;
            strStatus = (enabled ? preGreen : preRed) + strStatus + rst;

            if (enabled)
            {
                String strMode = ((PlacementRestrictionMode) Configs.Generic.PLACEMENT_RESTRICTION_MODE.getOptionListValue()).name();
                InfoUtils.printActionbarMessage("tweakeroo.message.toggled_fast_placement_mode_on", strStatus, preGreen + strMode + rst);
            }
            else
            {
                InfoUtils.printActionbarMessage("tweakeroo.message.toggled", this.feature.getPrettyName(), strStatus);
            }

            return true;
        }
    }

    private static class KeyCallbackAdjustableFeature implements IHotkeyCallback
    {
        private final IConfigBoolean config;

        private static IHotkeyCallback createCallback(IConfigBoolean config)
        {
            return new KeyCallbackAdjustable(config, new KeyCallbackAdjustableFeature(config));
        }

        private KeyCallbackAdjustableFeature(IConfigBoolean config)
        {
            this.config = config;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            this.config.toggleBooleanValue();

            boolean enabled = this.config.getBooleanValue();
            String strStatus = StringUtils.translate("tweakeroo.message.value." + (enabled ? "on" : "off"));
            String preGreen = GuiBase.TXT_GREEN;
            String preRed = GuiBase.TXT_RED;
            String rst = GuiBase.TXT_RST;
            String prettyName = this.config.getPrettyName();
            strStatus = (enabled ? preGreen : preRed) + strStatus + rst;

            if (key == FeatureToggle.TWEAK_AFTER_CLICKER.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getStringValue();
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_after_clicker_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_FLY_SPEED.getKeybind())
            {
                if (enabled)
                {
                    String strPreset = preGreen + (Configs.Internal.FLY_SPEED_PRESET.getIntegerValue() + 1) + rst;
                    String strSpeed = String.format("%s%.3f%s", preGreen, Configs.getActiveFlySpeedConfig().getDoubleValue(), rst);
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_fly_speed_on", strStatus, strPreset, strSpeed);
                }
                else
                {
                    EntityPlayer player = Minecraft.getInstance().player;

                    if (player != null)
                    {
                        player.abilities.setFlySpeed(0.05f);
                    }

                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_PLACEMENT_LIMIT.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.PLACEMENT_LIMIT.getStringValue();
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_placement_limit_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.getStringValue();
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_slot_cycle_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.HOTBAR_SLOT_RANDOMIZER_MAX.getStringValue();
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_slot_randomizer_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_PLACEMENT_GRID.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.PLACEMENT_GRID_SIZE.getStringValue();
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_placement_grid_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_BREAKING_GRID.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.BREAKING_GRID_SIZE.getStringValue();
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_breaking_grid_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_SNAP_AIM.getKeybind())
            {
                if (enabled)
                {
                    SnapAimMode mode = (SnapAimMode) Configs.Generic.SNAP_AIM_MODE.getOptionListValue();

                    if (mode == SnapAimMode.YAW)
                    {
                        String yaw = String.valueOf(Configs.Generic.SNAP_AIM_YAW_STEP.getDoubleValue());
                        InfoUtils.printActionbarMessage("tweakeroo.message.toggled_snap_aim_on_yaw", strStatus, preGreen + yaw + rst);
                    }
                    else if (mode == SnapAimMode.PITCH)
                    {
                        String pitch = String.valueOf(Configs.Generic.SNAP_AIM_PITCH_STEP.getDoubleValue());
                        InfoUtils.printActionbarMessage("tweakeroo.message.toggled_snap_aim_on_pitch", strStatus, preGreen + pitch + rst);
                    }
                    else
                    {
                        String yaw = String.valueOf(Configs.Generic.SNAP_AIM_YAW_STEP.getDoubleValue());
                        String pitch = String.valueOf(Configs.Generic.SNAP_AIM_PITCH_STEP.getDoubleValue());
                        InfoUtils.printActionbarMessage("tweakeroo.message.toggled_snap_aim_on_both", strStatus, preGreen + yaw + rst, preGreen + pitch + rst);
                    }
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_ZOOM.getKeybind())
            {
                if (enabled)
                {
                    String strValue = String.format("%s%.1f%s", preGreen, Configs.Generic.ZOOM_FOV.getDoubleValue(), rst);
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_zoom_on", strStatus, strValue);
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }

            return true;
        }
    }

    public static class ConfigSetMaxStackSize implements IValueChangeCallback<ConfigInteger>
    {
        private final Item[] items;

        public ConfigSetMaxStackSize(Item... items) {
            this.items = items;
        }

        @Override
        public void onValueChanged(ConfigInteger config) {
            int value = config.getIntegerValue();
            Arrays.stream(items).forEach((i) -> ((IItemStackLimit)i).setMaxStackSize(value));
        }
    }
}
