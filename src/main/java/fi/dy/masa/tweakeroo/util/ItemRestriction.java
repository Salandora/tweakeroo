package fi.dy.masa.tweakeroo.util;

import java.util.HashSet;
import java.util.List;
import fi.dy.masa.tweakeroo.LiteModTweakeroo;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemRestriction
{
    private ListType type = ListType.NONE;
    private final HashSet<Item> blackList = new HashSet<>();
    private final HashSet<Item> whiteList = new HashSet<>();

    public void setValues(ListType type, List<String> namesBlacklist, List<String> namesWhitelist)
    {
        this.type = type;
        this.setValuesForList(ListType.BLACKLIST, namesBlacklist);
        this.setValuesForList(ListType.WHITELIST, namesWhitelist);
    }

    protected void setValuesForList(ListType type, List<String> names)
    {
        HashSet<Item> set = type == ListType.WHITELIST ? this.whiteList : this.blackList;
        set.clear();

        for (String name : names)
        {
            Item item = Item.REGISTRY.getObject(new ResourceLocation(name));

            if (item != null && item != Items.AIR)
            {
                set.add(item);
            }
            else
            {
                LiteModTweakeroo.logger.warn("Invalid item name in a black- or whitelist: '{}", name);
            }
        }
    }

    public boolean isItemAllowed(ItemStack stack)
    {
        switch (this.type)
        {
            case BLACKLIST:
                return this.blackList.contains(stack.getItem()) == false;

            case WHITELIST:
                return this.whiteList.contains(stack.getItem());

            default:
                return true;
        }
    }
}
