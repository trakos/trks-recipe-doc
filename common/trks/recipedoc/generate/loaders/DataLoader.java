package trks.recipedoc.generate.loaders;

import codechicken.nei.ItemList;
import codechicken.nei.MultiItemRange;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.w3c.dom.Element;
import trks.recipedoc.generate.renderers.DataRenderer;
import trks.recipedoc.generate.renderers.utils.IconRenderer;
import trks.recipedoc.generate.renderers.utils.RecipeBackgroundRenderer;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;
import trks.recipedoc.generate.structs.RecipeTypeStruct;

import java.awt.*;
import java.util.*;
import java.util.List;

public class DataLoader
{
    public static boolean ready;

    static public ArrayList<ItemStruct> items = new ArrayList<ItemStruct>();
    static public ArrayList<RecipeStruct> recipes = new ArrayList<RecipeStruct>();
    static public ArrayList<RecipeTypeStruct> recipeHandlers = new ArrayList<RecipeTypeStruct>();
    static public HashMap<Integer, ItemData> itemDataMap = new HashMap<Integer, ItemData>();

    static protected void loadModsData()
    {
        NBTTagList itemDataList = new NBTTagList();
        GameData.writeItemData(itemDataList);
        for(int i = 0; i < itemDataList.tagCount(); i++)
        {
            ItemData itemData = new ItemData((NBTTagCompound) itemDataList.tagAt(i));
            itemDataMap.put(itemData.getItemId(), itemData);
        }
    }

    static public void load()
    {
        loadModsData();

        for (ItemStack item : DataNEIFetcher.getItems())
        {
            items.add(getItemStruct(item));
            recipes.addAll(loadRecipesByTarget(item));
        }
        recipeHandlers = loadRecipeHandlers(DataNEIFetcher.getActiveCraftingHandlers());
    }

    private static ArrayList<RecipeTypeStruct> loadRecipeHandlers(ArrayList<ICraftingHandler> activeCraftingHandlers)
    {
        ArrayList<RecipeTypeStruct> recipeHandlers = new ArrayList<RecipeTypeStruct>();
        for (ICraftingHandler recipeHandler : activeCraftingHandlers)
        {
            recipeHandlers.add(new RecipeTypeStruct(recipeHandler.getRecipeName(), recipeHandler.getRecipeName(), RecipeBackgroundRenderer.getRecipeHandlerImageName(recipeHandler)));
        }
        return recipeHandlers;
    }

    static protected ItemStruct getItemStruct(ItemStack itemStack)
    {
        ItemStruct item = new ItemStruct();

        item.id = itemStack.itemID;
        item.name = itemStack.getDisplayName();

        item.tooltipDescription = getNameList(itemStack);
        //noinspection unchecked
        fillAttributes(item.attributes, itemStack.getAttributeModifiers());

        item.mod = itemDataMap.get(itemStack.itemID).getModId();
        item.type = DataNEIFetcher.getItemType(itemStack);

        item.icon = IconRenderer.getIconName(itemStack);
        item.damage = itemStack.getItemDamage();

        return item;
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

    static protected ArrayList<RecipeStruct> loadRecipesByTarget(ItemStack itemStack)
    {
        ArrayList<RecipeStruct> recipes = new ArrayList<RecipeStruct>();
        for (ICraftingHandler craftingHandler : GuiCraftingRecipe.craftinghandlers)
        {
            ICraftingHandler handler = craftingHandler.getRecipeHandler("item", itemStack.copy());
            if (handler.numRecipes() > 0)
            {
                for(int i = 0; i < handler.numRecipes(); i++)
                {
                    recipes.add(new RecipeStruct(handler, i));
                }
            }
        }
        return recipes;
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
