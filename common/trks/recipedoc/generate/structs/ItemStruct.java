package trks.recipedoc.generate.structs;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import trks.recipedoc.api.API;
import trks.recipedoc.generate.loaders.DataLoader;
import trks.recipedoc.generate.loaders.DataNEIFetcher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStruct extends IdDamagePairWithStack
{
    public String rawName;
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
    /**
     * whether it is uncraftable item
     */
    public boolean isBaseItem;
    public float craftingComplexity = 10;
    public float itemCost;

    public ItemStruct(ItemStack itemStack)
    {
        super(itemStack.itemID, itemStack.getItemDamage(), itemStack.copy());

        this.name = itemStack.getDisplayName();
        this.rawName = itemStack.getItemName();

        this.tooltipDescription = getNameList(itemStack);

        this.mod = DataLoader.getItemModId(itemStack.itemID);
        this.type = DataNEIFetcher.getItemType(itemStack);
        this.category = getTypeCategory(this.type);

        this.showOnList = true;

        this.isBaseItem =  isInOreDictionary(getItemStack()) || isNameSuggestingBeingBase();
    }

    protected boolean isInOreDictionary(ItemStack check)
    {
        HashMap<Integer, ArrayList<ItemStack>> oreStacks;
        try
        {
            Field f = OreDictionary.class.getDeclaredField("oreStacks");
            f.setAccessible(true);
            oreStacks = (HashMap<Integer, ArrayList<ItemStack>>) f.get(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException();
        }

        int idFound = -1;

        for (Map.Entry<Integer, ArrayList<ItemStack>> entry : oreStacks.entrySet())
        {
            for (ItemStack stack : entry.getValue())
            {
                if (stack.isItemEqual(check))
                {
                    idFound = entry.getKey();
                    break;
                }
            }

            if (idFound != -1)
            {
                break;
            }
        }

        if (idFound == -1)
        {
            return false;
        }

        return true;
        //return OreDictionary.getOreName(idFound);
    }

    protected boolean isNameSuggestingBeingBase()
    {
        String name = this.name.toLowerCase();
        return
                name.contains("ingot") /*|| name.contains("dust")*/ || name.contains("ore") /*|| name.contains("paper")*/
                || name.contains("bucket") || name.contains("facade");
    }

    public ArrayList<HashMap<IdDamagePair, Float>> rawCosts = new ArrayList<HashMap<IdDamagePair, Float>>();

    public String getIconName()
    {
        return this.itemId + "_" + this.damageId + ".png";
    }

    static protected String getTypeCategory(String type)
    {
        if (type.startsWith("Items.Tools"))
        {
            return API.STANDARD_CATEGORY_ITEMS;
        }
        else if (type.startsWith("Items.Weapons"))
        {
            return API.STANDARD_CATEGORY_WEAPONS;
        }
        else if (type.startsWith("Items.Armor"))
        {
            return API.STANDARD_CATEGORY_ARMOR;
        }
        else if (type.startsWith("Items.Food"))
        {
            return API.STANDARD_CATEGORY_FOOD;
        }
        else if (type.startsWith("Blocks.Wood"))
        {
            return API.STANDARD_CATEGORY_WOOD;
        }
        else if (type.startsWith("Blocks.Internal") || type.startsWith("Items.Internal") || type.startsWith("Blocks.MobSpawners"))
        {
            return API.STANDARD_CATEGORY_INTERNAL;
        }
        else if (type.startsWith("Blocks."))
        {
            return API.STANDARD_CATEGORY_BLOCKS;
        }
        else if (type.startsWith("Items.Potions"))
        {
            return API.STANDARD_CATEGORY_POTIONS;
        }
        else
        {
            return API.STANDARD_CATEGORY_OTHER;
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
