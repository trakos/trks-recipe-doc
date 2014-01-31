package trks.recipedoc.modsupport.mods;

import codechicken.nei.recipe.ICraftingHandler;
import trks.recipedoc.api.IDocModSupport;
import trks.recipedoc.api.IRecipeHandlerMachineRegistrar;
import trks.recipedoc.generate.structs.IdDamagePair;
import trks.recipedoc.generate.structs.IdDamagePairWithStack;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trakos on 30.01.14.
 */
public class BuildcraftSupport implements IDocModSupport
{

    @Override
    public Boolean shouldCorrectItemFromMod(String modId)
    {
        return modId.startsWith("BuildCraft|");
    }

    HashMap<String, Float> categories = new HashMap<String, Float>();
    @Override
    public void correctItemStruct(ItemStruct itemStruct, IRecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar)
    {
        String category = itemStruct.mod.substring(itemStruct.mod.lastIndexOf("|") + 1);
        itemStruct.mod = "BuildCraft";
        itemStruct.category = category;
        categories.put(category, 5f);
    }

    @Override
    public boolean isBaseItem(ItemStruct itemStruct)
    {
        return itemStruct.isBaseItem;
    }

    @Override
    public Collection<String> getModsRequiredToBeLoaded()
    {
        return Arrays.asList("BuildCraft|Core");
    }

    @Override
    public Map<String, Float> getNewCategories()
    {
        return categories;
    }

    @Override
    public void correctRecipeStruct(RecipeStruct recipeStruct, ICraftingHandler handler)
    {
        IdDamagePairWithStack result = recipeStruct.getResult();
        if (result.getItemStack().getDisplayName().contains(" Transport Pipe"))
        {
            if (!recipeStruct.hasIngredient(new IdDamagePair(net.minecraft.block.Block.glass.blockID, 0)))
            {
                System.out.println("Removing BuildCraft|Transport recipe from cost calculation: " + recipeStruct.recipeHandlerName);
                recipeStruct.useInRawCostCalculation = false;
            }
        }
    }

    @Override
    public void correctRecipeItemStruct(IdDamagePairWithStack recipeItemStruct)
    {

    }
}
