package fi.dy.masa.tweakeroo.event;

import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;

public class RenderHandler implements IRenderer
{
    @Override
    public void onRenderGameOverlayPost(float partialTicks)
    {
        Minecraft mc = Minecraft.getInstance();

        if (FeatureToggle.TWEAK_HOTBAR_SWAP.getBooleanValue() &&
            Hotkeys.HOTBAR_SWAP_BASE.getKeybind().isKeybindHeld())
        {
            RenderUtils.renderHotbarSwapOverlay(mc);
        }
        else if (FeatureToggle.TWEAK_HOTBAR_SCROLL.getBooleanValue() &&
                 Hotkeys.HOTBAR_SCROLL.getKeybind().isKeybindHeld())
        {
            RenderUtils.renderHotbarScrollOverlay(mc);
        }

        if (FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() &&
            Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld())
        {
            RenderUtils.renderInventoryOverlay(mc);
        }

        if (FeatureToggle.TWEAK_PLAYER_INVENTORY_PEEK.getBooleanValue() &&
            Hotkeys.PLAYER_INVENTORY_PEEK.getKeybind().isKeybindHeld())
        {
            RenderUtils.renderPlayerInventoryOverlay(mc);
        }
    }

    @Override
    public void onRenderTooltipLast(ItemStack stack, int x, int y)
    {
        if (stack.getItem() instanceof ItemMap)
        {
            if (FeatureToggle.TWEAK_MAP_PREVIEW.getBooleanValue())
            {
                fi.dy.masa.malilib.render.RenderUtils.renderMapPreview(stack, x, y, Configs.Generic.MAP_PREVIEW_SIZE.getIntegerValue());
            }
        }
        else if (FeatureToggle.TWEAK_SHULKERBOX_DISPLAY.getBooleanValue())
        {
            fi.dy.masa.malilib.render.RenderUtils.renderShulkerBoxPreview(stack, x, y, Configs.Generic.SHULKER_DISPLAY_BACKGROUND_COLOR.getBooleanValue());
        }
    }

    @Override
    public void onRenderWorldLast(float partialTicks)
    {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null)
        {
            this.renderOverlays(mc, partialTicks);
        }
    }

    private void renderOverlays(Minecraft mc, float partialTicks)
    {
        if (FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue() &&
            mc.objectMouseOver != null &&
            mc.objectMouseOver.type == RayTraceResult.Type.BLOCK &&
            (Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeybind().isKeybindHeld() ||
             Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeybind().isKeybindHeld()))
        {
            Entity entity = mc.player;
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            //GlStateManager.pushMatrix();
            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture2D();

            Color4f color = Configs.Generic.FLEXIBLE_PLACEMENT_OVERLAY_COLOR.getColor();

            fi.dy.masa.malilib.render.RenderUtils.renderBlockTargetingOverlay(
                    entity,
                    mc.objectMouseOver.getBlockPos(),
                    mc.objectMouseOver.sideHit,
                    mc.objectMouseOver.hitVec,
                    color, partialTicks);

            GlStateManager.enableTexture2D();
            GlStateManager.enableDepthTest();
            //GlStateManager.popMatrix();
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
        }
    }
}
