package fi.dy.masa.tweakeroo.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import fi.dy.masa.malilib.util.Constants;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.Tweakeroo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.mixin.IMixinSlot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class InventoryUtils
{
    private static final HashSet<Item> UNSTACKING_ITEMS = new HashSet<>();

    public static void setUnstackingItems(List<String> names)
    {
        UNSTACKING_ITEMS.clear();

        for (String name : names)
        {
            try
            {
                Item item = IRegistry.ITEM.get(new ResourceLocation(name));

                if (item != null && item != Items.AIR)
                {
                    UNSTACKING_ITEMS.add(item);
                }
            }
            catch (Exception e)
            {
                Tweakeroo.logger.warn("Failed to set an unstacking protected item from name '{}'", name, e);
            }
        }
    }

    public static void swapHotbarWithInventoryRow(EntityPlayer player, int row)
    {
        Container container = player.inventoryContainer;
        row = MathHelper.clamp(row, 0, 2);
        int slot = row * 9 + 9;

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++)
        {
            fi.dy.masa.malilib.util.InventoryUtils.swapSlots(container, slot, hotbarSlot);
            slot++;
        }
    }

    public static void restockNewStackToHand(EntityPlayer player, EnumHand hand, ItemStack stackReference, boolean allowHotbar)
    {
        int slotWithItem = findSlotWithItem(player.inventoryContainer, stackReference, allowHotbar, true);

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
        }
    }

    public static void trySwapCurrentToolIfNearlyBroken()
    {
        if (FeatureToggle.TWEAK_SWAP_ALMOST_BROKEN_TOOLS.getBooleanValue())
        {
            Minecraft mc = Minecraft.getInstance();
            EntityPlayer player = mc.player;

            for (EnumHand hand : EnumHand.values())
            {
                ItemStack stack = player.getHeldItem(hand);

                if (stack.isEmpty() == false)
                {
                    int minDurability = getMinDurability(stack);

                    if (isItemAtLowDurability(stack, minDurability))
                    {
                        swapItemWithHigherDurabilityToHand(player, hand, stack, minDurability);
                    }
                }
            }
        }
    }

    public static void trySwitchToEffectiveTool(BlockPos pos)
    {
        if (FeatureToggle.TWEAK_TOOL_SWITCH.getBooleanValue())
        {
            Minecraft mc = Minecraft.getInstance();
            EntityPlayer player = mc.player;
            IBlockState state = mc.world.getBlockState(pos);
            ItemStack stack = player.getHeldItemMainhand();

            if (stack.isEmpty() || stack.getDestroySpeed(state) <= 1f)
            {
                Container container = player.inventoryContainer;
                int slotNumber = findSlotWithEffectiveItemWithDurabilityLeft(container, state);

                if (slotNumber != -1)
                {
                    swapItemToHand(player, EnumHand.MAIN_HAND, slotNumber);
                }
            }
        }
    }

    private static boolean isItemAtLowDurability(ItemStack stack, int minDurability)
    {
        return stack.getItem().isDamageable() && stack.getDamage() >= stack.getMaxDamage() - minDurability;
    }

    private static int getMinDurability(ItemStack stack)
    {
        int minDurability = Configs.Generic.ITEM_SWAP_DURABILITY_THRESHOLD.getIntegerValue();

        // For items with low maximum durability, use 5% as the threshold,
        // if the configured durability threshold is over that.
        if ((double) minDurability / (double) stack.getMaxDamage() >= 0.05D)
        {
            minDurability = (int) (stack.getMaxDamage() * 0.05);
        }

        return minDurability;
    }

    private static void swapItemWithHigherDurabilityToHand(EntityPlayer player, EnumHand hand, ItemStack stackReference, int minDurabilityLeft)
    {
        Minecraft mc = Minecraft.getInstance();
        int slotWithItem = findSlotWithIdenticalItemWithDurabilityLeft(player.inventoryContainer, stackReference, minDurabilityLeft);

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
            mc.ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation("tweakeroo.message.swapped_low_durability_item_for_better_durability"));
            return;
        }

        slotWithItem = fi.dy.masa.malilib.util.InventoryUtils.findEmptySlotInPlayerInventory(player.inventoryContainer, false, false);

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
            mc.ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation("tweakeroo.message.swapped_low_durability_item_off_players_hand"));
            return;
        }

        Container container = player.inventoryContainer;

        for (Slot slot : container.inventorySlots)
        {
            if (slot.slotNumber <= 8)
            {
                continue;
            }

            ItemStack stack = slot.getStack();

            if (stack.isEmpty() == false && stack.getItem().isDamageable() == false)
            {
                slotWithItem = slot.slotNumber;
                break;
            }
        }

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
            mc.ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation("tweakeroo.message.swapped_low_durability_item_for_dummy_item"));
        }
    }

    public static void repairModeSwapItems(EntityPlayer player)
    {
        if (player.openContainer instanceof ContainerPlayer)
        {
            repairModeHandleHand(player, EnumHand.MAIN_HAND);
            repairModeHandleHand(player, EnumHand.OFF_HAND);
        }
    }

    private static void repairModeHandleHand(EntityPlayer player, EnumHand hand)
    {
        ItemStack stackHand = player.getHeldItem(hand);

        if (stackHand.isEmpty() == false &&
            (stackHand.isDamageable() == false ||
             stackHand.isDamaged() == false ||
             EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stackHand) <= 0))
        {
            int slotNumber = findRepairableItemNotInHand(player);

            if (slotNumber != -1)
            {
                swapItemToHand(player, hand, slotNumber);
                StringUtils.printActionbarMessage("tweakeroo.message.repair_mode.swapped_repairable_item_to_hand");
            }
        }
    }

    private static int findRepairableItemNotInHand(EntityPlayer player)
    {
        Container containerPlayer = player.openContainer;
        int slotHand = player.inventory.currentItem;

        for (Slot slot : containerPlayer.inventorySlots)
        {
            if (slot.slotNumber != slotHand && slot.getHasStack())
            {
                ItemStack stack = slot.getStack();

                if (stack.isDamageable() && stack.isDamaged() &&
                    EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0)
                {
                    return slot.slotNumber;
                }
            }
        }

        return -1;
    }

    /**
     * Finds a slot with an identical item than <b>stackReference</b>, ignoring the durability
     * of damageable items. Does not allow crafting or armor slots or the offhand slot
     * in the ContainerPlayer container.
     * @param container
     * @param stackReference
     * @param reverse
     * @return the slot number, or -1 if none were found
     */
    public static int findSlotWithItem(Container container, ItemStack stackReference, boolean allowHotbar, boolean reverse)
    {
        final int startSlot = reverse ? container.inventorySlots.size() - 1 : 0;
        final int endSlot = reverse ? -1 : container.inventorySlots.size();
        final int increment = reverse ? -1 : 1;
        final boolean isPlayerInv = container instanceof ContainerPlayer;

        for (int slotNum = startSlot; slotNum != endSlot; slotNum += increment)
        {
            Slot slot = container.inventorySlots.get(slotNum);

            if ((isPlayerInv == false || fi.dy.masa.malilib.util.InventoryUtils.isRegularInventorySlot(slot.slotNumber, false)) &&
                (allowHotbar || isHotbarSlot(slot) == false) &&
                fi.dy.masa.malilib.util.InventoryUtils.areStacksEqualIgnoreDurability(slot.getStack(), stackReference))
            {
                return slot.slotNumber;
            }
        }

        return -1;
    }

    private static boolean isHotbarSlot(Slot slot)
    {
        // This isn't correct for modded Forge IItemHandler-based inventories
        return (slot.inventory instanceof InventoryPlayer) && ((IMixinSlot) slot).getSlotIndex() <= 8;
    }

    private static void swapItemToHand(EntityPlayer player, EnumHand hand, int slotNumber)
    {
        if (slotNumber != -1)
        {
            Minecraft mc = Minecraft.getInstance();
            Container container = player.inventoryContainer;

            if (hand == EnumHand.MAIN_HAND)
            {
                int currentHotbarSlot = player.inventory.currentItem;
                mc.playerController.windowClick(container.windowId, slotNumber, currentHotbarSlot, ClickType.SWAP, mc.player);
            }
            else if (hand == EnumHand.OFF_HAND)
            {
                int currentHotbarSlot = player.inventory.currentItem;
                // Swap the requested slot to the current hotbar slot
                mc.playerController.windowClick(container.windowId, slotNumber, currentHotbarSlot, ClickType.SWAP, mc.player);

                // Swap the requested item from the hotbar slot to the offhand
                mc.playerController.windowClick(container.windowId, 45, currentHotbarSlot, ClickType.SWAP, mc.player);

                // Swap the original item back to the hotbar slot
                mc.playerController.windowClick(container.windowId, slotNumber, currentHotbarSlot, ClickType.SWAP, mc.player);
            }
        }
    }

    private static int findSlotWithIdenticalItemWithDurabilityLeft(Container container, ItemStack stackReference, int minDurabilityLeft)
    {
        for (Slot slot : container.inventorySlots)
        {
            ItemStack stackSlot = slot.getStack();

            if (stackSlot.isItemEqualIgnoreDurability(stackReference) &&
                stackSlot.getMaxDamage() - stackSlot.getDamage() > minDurabilityLeft &&
                ItemStack.areItemStackTagsEqual(stackSlot, stackReference))
            {
                return slot.slotNumber;
            }
        }

        return -1;
    }

    private static int findSlotWithEffectiveItemWithDurabilityLeft(Container container, IBlockState state)
    {
        int slotNum = -1;
        float bestSpeed = -1f;

        for (Slot slot : container.inventorySlots)
        {
            // Don't consider armor and crafting slots
            if (slot.slotNumber <= 8 || slot.getHasStack() == false)
            {
                continue;
            }

            ItemStack stack = slot.getStack();

            if (stack.getMaxDamage() - stack.getItemDamage() > getMinDurability(stack))
            {
                float speed = stack.getDestroySpeed(state);
                int effLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);

                if (effLevel > 0)
                {
                    speed += (effLevel * effLevel) + 1;
                }

                if (speed > 1f && (slotNum == -1 || speed > bestSpeed))
                {
                    slotNum = slot.slotNumber;
                    bestSpeed = speed;
                }
            }
        }

        return slotNum;
    }

    private static void tryCombineStacksInInventory(EntityPlayer player, ItemStack stackReference)
    {
        List<Slot> slots = new ArrayList<>();
        Container container = player.inventoryContainer;
        Minecraft mc = Minecraft.getInstance();

        for (Slot slot : container.inventorySlots)
        {
            // Inventory crafting and armor slots are not valid
            if (slot.slotNumber < 8)
            {
                continue;
            }

            ItemStack stack = slot.getStack();

            if (stack.getCount() < stack.getMaxStackSize() && fi.dy.masa.malilib.util.InventoryUtils.areStacksEqual(stackReference, stack))
            {
                slots.add(slot);
            }
        }

        for (int i = 0; i < slots.size(); ++i)
        {
            Slot slot1 = slots.get(i);

            for (int j = i + 1; j < slots.size(); ++j)
            {
                Slot slot2 = slots.get(j);
                ItemStack stack = slot1.getStack();

                if (stack.getCount() < stack.getMaxStackSize())
                {
                    // Pick up the item from slot1 and try to put it in slot2
                    mc.playerController.windowClick(container.windowId, slot1.slotNumber, 0, ClickType.PICKUP, player);
                    mc.playerController.windowClick(container.windowId, slot2.slotNumber, 0, ClickType.PICKUP, player);

                    // If the items didn't all fit, return the rest
                    if (player.inventory.getCurrentItem().isEmpty() == false)
                    {
                        mc.playerController.windowClick(container.windowId, slot1.slotNumber, 0, ClickType.PICKUP, player);
                    }

                    if (slot2.getStack().getCount() >= slot2.getStack().getMaxStackSize())
                    {
                        slots.remove(j);
                        --j;
                    }
                }

                if (slot1.getHasStack() == false)
                {
                    break;
                }
            }
        }
    }

    public static boolean canUnstackingItemNotFitInInventory(ItemStack stack, EntityPlayer player)
    {
        if (FeatureToggle.TWEAK_ITEM_UNSTACKING_PROTECTION.getBooleanValue() &&
            stack.getCount() > 1 &&
            UNSTACKING_ITEMS.contains(stack.getItem()))
        {
            if (fi.dy.masa.malilib.util.InventoryUtils.findEmptySlotInPlayerInventory(player.inventoryContainer, false, false) == -1)
            {
                tryCombineStacksInInventory(player, stack);

                if (fi.dy.masa.malilib.util.InventoryUtils.findEmptySlotInPlayerInventory(player.inventoryContainer, false, false) == -1)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static void switchToPickedBlock()
    {
        Minecraft mc  = Minecraft.getInstance();
        EntityPlayer player = mc.player;
        World world = mc.world;
        double reach = mc.playerController.getBlockReachDistance();
        boolean isCreative = player.abilities.isCreativeMode;
        RayTraceResult trace = player.rayTrace(reach, mc.getRenderPartialTicks(), RayTraceFluidMode.NEVER);

        if (trace != null && trace.type == RayTraceResult.Type.BLOCK)
        {
            BlockPos pos = trace.getBlockPos();
            IBlockState stateTargeted = world.getBlockState(pos);
            ItemStack stack = stateTargeted.getBlock().getItem(world, pos, stateTargeted);

            if (stack.isEmpty() == false)
            {
                /*
                if (isCreative)
                {
                    TileEntity te = world.getTileEntity(pos);

                    if (te != null)
                    {
                        mc.storeTEInStack(stack, te);
                    }
                }
                */

                if (isCreative)
                {
                    player.inventory.setPickedItemStack(stack);
                    mc.playerController.sendSlotPacket(player.getHeldItem(EnumHand.MAIN_HAND), 36 + player.inventory.currentItem);
                }
                else
                {
                    int slot = fi.dy.masa.malilib.util.InventoryUtils.findSlotWithItem(player.inventoryContainer, stack, true); //player.inventory.getSlotFor(stack);

                    if (slot != -1)
                    {
                        int currentHotbarSlot = player.inventory.currentItem;
                        mc.playerController.windowClick(player.inventoryContainer.windowId, slot, currentHotbarSlot, ClickType.SWAP, mc.player);

                        /*
                        if (InventoryPlayer.isHotbar(slot))
                        {
                            player.inventory.currentItem = slot;
                        }
                        else
                        {
                            mc.playerController.pickItem(slot);
                        }
                        */
                    }
                }
            }
        }
    }

    public static boolean cleanUpShulkerBoxNBT(ItemStack stack)
    {
        boolean changed = false;
        NBTTagCompound nbt = stack.getTagCompound();

        if (nbt != null)
        {
            if (nbt.hasKey("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
            {
                NBTTagCompound tag = nbt.getCompoundTag("BlockEntityTag");

                if (tag.hasKey("Items", Constants.NBT.TAG_LIST) &&
                    tag.getTagList("Items", Constants.NBT.TAG_COMPOUND).tagCount() == 0)
                {
                    tag.removeTag("Items");
                    changed = true;
                }

                if (tag.isEmpty())
                {
                    nbt.removeTag("BlockEntityTag");
                }
            }

            if (nbt.isEmpty())
            {
                stack.setTagCompound(null);
                changed = true;
            }
        }

        return changed;
    }
}
