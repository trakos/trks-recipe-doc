package trks.recipedoc.generate.loaders;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import trks.recipedoc.generate.renderers.utils.RecipeBackgroundRenderer;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;
import trks.recipedoc.generate.structs.RecipeTypeStruct;
import trks.recipedoc.modsupport.API;
import trks.recipedoc.modsupport.ModSupportHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class DataLoader
{
    public static boolean ready;

    static public ArrayList<ItemStruct> items;
    static public ArrayList<RecipeStruct> recipes;
    static public ArrayList<RecipeTypeStruct> recipeHandlers;
    static public ArrayList<String> categories;

    static protected HashMap<Integer, ItemData> itemDataMap;

    static public String getItemModId(int itemID)
    {
        return itemDataMap.get(itemID).getModId();
    }


    static protected void loadModsData()
    {
        itemDataMap = new HashMap<Integer, ItemData>();

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

        items = new ArrayList<ItemStruct>();
        recipes = new ArrayList<RecipeStruct>();
        for (ItemStack item : DataNEIFetcher.getItems())
        {
            items.add(ModSupportHandler.correctItemStruct(new ItemStruct(item)));
            recipes.addAll(loadRecipesByTarget(item));
        }
        recipeHandlers = loadRecipeHandlers(DataNEIFetcher.getActiveCraftingHandlers());
        categories = getItemCategories();
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

    protected static ArrayList<String> getItemCategories()
    {
        HashMap<String, Float> temporaryCategories = new HashMap<String, Float>();
        temporaryCategories.put(API.STANDARD_CATEGORY_ITEMS, 10f);
        temporaryCategories.put(API.STANDARD_CATEGORY_WEAPONS, 30f);
        temporaryCategories.put(API.STANDARD_CATEGORY_ARMOR, 30f);
        temporaryCategories.put(API.STANDARD_CATEGORY_FOOD, 40f);
        temporaryCategories.put(API.STANDARD_CATEGORY_BLOCKS, 50f);
        temporaryCategories.put(API.STANDARD_CATEGORY_OTHER, 100f);
        ModSupportHandler.fillCategories(temporaryCategories);
        // sorts categories by value and put it into this order into categories list
        //noinspection unchecked
        return new ArrayList<String>(ImmutableSortedMap.copyOf(temporaryCategories, Ordering.natural().onResultOf(Functions.forMap(temporaryCategories)).compound((Comparator)Ordering.natural())).keySet());
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
                    recipes.add(ModSupportHandler.correctRecipeStruct(new RecipeStruct(handler, i), handler));
                }
            }
        }
        return recipes;
    }
}
