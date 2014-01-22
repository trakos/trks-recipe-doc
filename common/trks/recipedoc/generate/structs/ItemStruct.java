package trks.recipedoc.generate.structs;

import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import trks.recipedoc.generate.loaders.DataLoader;
import trks.recipedoc.generate.loaders.DataNEIFetcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStruct
{
    protected final ItemStack sourceItemStack;

    public int id;
    public int damage;
    public String name;
    public List<String> tooltipDescription = new ArrayList<String>();
    public String description;
    public Map<String, String> attributes = new HashMap<String, String>();
    public String mod;
    /**
     * Type guessed in a way NEI does (dot separated nested categories)
     */
    public String type;
    /**
     * Flat, easier to use category
     */
    public String category;
    public boolean showOnList;

    public ItemStruct(ItemStack itemStack)
    {
        this.id = itemStack.itemID;
        this.name = itemStack.getDisplayName();

        this.tooltipDescription = getNameList(itemStack);
        //noinspection unchecked
        fillAttributes(this.attributes, itemStack.getAttributeModifiers());

        this.mod = DataLoader.getItemModId(itemStack.itemID);
        this.type = DataNEIFetcher.getItemType(itemStack);
        this.category = getTypeCategory(this.type);

        this.damage = itemStack.getItemDamage();

        this.showOnList = true;

        this.sourceItemStack = itemStack.copy();
    }

    public String getIconName()
    {
        return this.id + "_" + this.damage + ".png";
    }

    public ItemStack getSourceItemStack()
    {
        return this.sourceItemStack;
    }

    static protected void fillAttributes(Map<String, String> map, Multimap attributes)
    {
        for (Object key : attributes.keys())
        {
            //noinspection unchecked
            for (Object w : attributes.get(key))
            {
                AttributeModifier value = (AttributeModifier) w;
                map.put(value.getName(), Double.toString(value.getAmount()));
            }
        }
    }

    static protected String getTypeCategory(String type)
    {
        if (type.startsWith("Items.Tools"))
        {
            return "Items";
        }
        else if (type.startsWith("Items.Weapons"))
        {
            return "Weapons";
        }
        else if (type.startsWith("Items.Armor"))
        {
            return "Armor";
        }
        else if (type.startsWith("Items.Food"))
        {
            return "Food";
        }
        else if (type.startsWith("Blocks.Regular"))
        {
            return "Blocks";
        }
        else
        {
            return "Other";
        }
    }


    protected static List<String> getNameList(ItemStack itemstack)
    {
        List<String> nameList = null;
        try
        {
            //noinspection unchecked
            nameList = itemstack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        }
        catch (Throwable ignored)
        {

        }

        if (nameList == null)
        {
            nameList = new ArrayList<String>();
        }

        if (nameList.size() == 0)
        {
            nameList.add("Unnamed");
        }

        if (nameList.get(0) == null || nameList.get(0).equals(""))
        {
            nameList.set(0, "Unnamed");
        }

        nameList.set(0, "\247" + Integer.toHexString(itemstack.getRarity().rarityColor) + nameList.get(0));
        for (int i = 1; i < nameList.size(); i++)
        {
            nameList.set(i, "\u00a77" + nameList.get(i));
        }

        return nameList;
    }
}
