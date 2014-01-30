package trks.recipedoc.modsupport.mods;

import codechicken.nei.recipe.ICraftingHandler;
import trks.recipedoc.api.IDocModSupport;
import trks.recipedoc.api.IRecipeHandlerMachineRegistrar;
import trks.recipedoc.generate.structs.IdDamagePairWithStack;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Created by trakos on 30.01.14.
 */
public class QuantumCraftSupport implements IDocModSupport
{
    @Override
    public Boolean shouldCorrectItemFromMod(String modId)
    {
        return modId.equals("QuantumCraft");
    }

    @Override
    public void correctItemStruct(ItemStruct itemStruct, IRecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar)
    {

    }

    @Override
    public boolean isBaseItem(ItemStruct itemStruct)
    {
        return itemStruct.rawName.equals("item.quantumdust");
    }

    @Override
    public Collection<String> getModsRequiredToBeLoaded()
    {
        return Arrays.asList("QuantumCraft");
    }

    @Override
    public Map<String, Float> getNewCategories()
    {
        return null;
    }

    @Override
    public void correctRecipeStruct(RecipeStruct recipeStruct, ICraftingHandler handler)
    {

    }

    @Override
    public void correctRecipeItemStruct(IdDamagePairWithStack recipeItemStruct)
    {

    }
}
