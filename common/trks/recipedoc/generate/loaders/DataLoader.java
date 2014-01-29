package trks.recipedoc.generate.loaders;

import codechicken.nei.recipe.ICraftingHandler;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;
import trks.recipedoc.generate.structs.RecipeTypeStruct;
import trks.recipedoc.modsupport.ModSupportHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

public class DataLoader
{

    static protected HashMap<Integer, ItemData> itemDataMap;
    public static String getItemModId(int itemID)
    {
        if (itemDataMap == null)
        {
            loadModsData();
        }
        return itemDataMap.get(itemID).getModId();
    }

    protected static void loadModsData()
    {
        if (itemDataMap != null) return;
        itemDataMap = new HashMap<Integer, ItemData>();

        NBTTagList itemDataList = new NBTTagList();
        GameData.writeItemData(itemDataList);
        for(int i = 0; i < itemDataList.tagCount(); i++)
        {
            ItemData itemData = new ItemData((NBTTagCompound) itemDataList.tagAt(i));
            itemDataMap.put(itemData.getItemId(), itemData);
        }
    }


    static protected ArrayList<RecipeTypeStruct> recipeHandlersList = null;
    static public Collection<RecipeTypeStruct> getRecipeHandlers()
    {
        if (recipeHandlersList == null)
        {
            recipeHandlersList = loadRecipeHandlers(DataNEIFetcher.getActiveCraftingHandlers());
        }
        return recipeHandlersList;
    }

    static protected ArrayList<RecipeTypeStruct> loadRecipeHandlers(ArrayList<ICraftingHandler> activeCraftingHandlers)
    {
        ArrayList<RecipeTypeStruct> recipeHandlers = new ArrayList<RecipeTypeStruct>();
        for (ICraftingHandler recipeHandler : activeCraftingHandlers)
        {
            recipeHandlers.add(new RecipeTypeStruct(recipeHandler));
        }
        return recipeHandlers;
    }

    public ArrayList<ItemStruct> items;
    public ArrayList<RecipeStruct> recipes;
    public ArrayList<String> categories;

    public DataLoader()
    {
        items = new ArrayList<ItemStruct>();
        recipes = new ArrayList<RecipeStruct>();
        for (ItemStack item : DataNEIFetcher.getItems())
        {
            items.add(ModSupportHandler.correctItemStruct(new ItemStruct(item)));
            recipes.addAll(loadRecipesByTarget(item));
        }
        categories = getItemCategories();
    }

    protected ArrayList<String> getItemCategories()
    {
        HashMap<String, Float> temporaryCategories = new HashMap<String, Float>();
        ModSupportHandler.fillCategories(temporaryCategories);
        // sorts categories by value and put it into this order into categories list
        //noinspection unchecked
        return new ArrayList<String>(ImmutableSortedMap.copyOf(temporaryCategories, Ordering.natural().onResultOf(Functions.forMap(temporaryCategories)).compound((Comparator)Ordering.natural())).keySet());
    }

    protected ArrayList<RecipeStruct> loadRecipesByTarget(ItemStack itemStack)
    {
        ArrayList<RecipeStruct> recipes = new ArrayList<RecipeStruct>();
        for (ICraftingHandler craftingHandler : DataNEIFetcher.getAllNEICraftingHandlers())
        {
            ICraftingHandler handler = craftingHandler.getRecipeHandler("item", itemStack.copy());
            if (handler.numRecipes() > 0)
            {
                for(int i = 0; i < handler.numRecipes(); i++)
                {
                    recipes.add(ModSupportHandler.correctRecipeStruct(new RecipeStruct(handler, i), handler));
                }
            }
        }
        return recipes;
    }
}
