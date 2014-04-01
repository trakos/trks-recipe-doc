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

public class ForgeMicroblockSupport implements IDocModSupport
{
    @Override
    public Boolean shouldCorrectItemFromMod(String modId)
    {
        return modId.equals("ForgeMicroblock");
    }

    @Override
    public void correctItemStruct(ItemStruct itemStruct, IRecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar)
    {
        itemStruct.showOnList = false;
    }

    @Override
    public boolean isBaseItem(ItemStruct itemStruct)
    {
        return true;
    }

    @Override
    public Collection<String> getModsRequiredToBeLoaded()
    {
        return Arrays.asList("ForgeMicroblock");
    }

    @Override
    public Map<String, Float> getNewCategories()
    {
        return null;
    }

    @Override
    public void correctRecipeStruct(RecipeStruct recipeStruct, ICraftingHandler handler)
    {
        recipeStruct.useInRawCostCalculation = false;
    }

    @Override
    public void correctRecipeItemStruct(IdDamagePairWithStack recipeItemStruct)
    {

    }
}
