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
import java.util.Map;

/**
 * Created by trakos on 30.01.14.
 */
public class BuildcraftTransportSupport implements IDocModSupport
{

    @Override
    public Boolean shouldCorrectItemFromMod(String modId)
    {
        return modId.equals("BuildCraft|Transport");
    }

    @Override
    public void correctItemStruct(ItemStruct itemStruct, IRecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar)
    {
    }

    @Override
    public boolean isBaseItem(ItemStruct itemStruct)
    {
        return itemStruct.isBaseItem;
    }

    @Override
    public Collection<String> getModsRequiredToBeLoaded()
    {
        return Arrays.asList("BuildCraft|Transport");
    }

    @Override
    public Map<String, Float> getNewCategories()
    {
        return null;
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
