package trks.recipedoc.modsupport.mods;

import codechicken.nei.recipe.FurnaceRecipeHandler;
import codechicken.nei.recipe.ICraftingHandler;
import trks.recipedoc.api.IDocModSupport;
import trks.recipedoc.api.IRecipeHandlerMachineRegistrar;
import trks.recipedoc.generate.structs.IdDamagePairWithStack;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Created by trakos on 30.01.14.
 */
public class AppliedEnergeticsSupport implements IDocModSupport
{

    @Override
    public Boolean shouldCorrectItemFromMod(String modId)
    {
        return modId.equals("AppliedEnergistics");
    }

    @Override
    public void correctItemStruct(ItemStruct itemStruct, IRecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar)
    {
        if (itemStruct.rawName.equals("AppEng.Materials.FluxQuartzDust") || itemStruct.rawName.equals("AppEng.Materials.GoldDust") || itemStruct.rawName.equals("AppEng.Materials.IronDust"))
        {
            itemStruct.craftingComplexity = .1f;
            itemStruct.isBaseItem = false;
        }
    }

    @Override
    public boolean isBaseItem(ItemStruct itemStruct)
    {
        if (itemStruct.getItemStack().getDisplayName().equals("Quartz Cutting Knife"))
        {
            return true;
        }
        return itemStruct.isBaseItem;
    }

    @Override
    public Collection<String> getModsRequiredToBeLoaded()
    {
        return Arrays.asList("AppliedEnergistics");
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
        if (result.getItemStack().getItemName().equals("AppEng.Blocks.Cable"))
        {
            if (recipeStruct.items.size() < 4)
            {
                // undoing coloring cables
                recipeStruct.useInRawCostCalculation = false;
            }
        }
        else if (result.getItemStack().getDisplayName().equals("Silicon"))
        {
            if (handler instanceof FurnaceRecipeHandler)
            {
                recipeStruct.useInRawCostCalculation = false;
            }
            else
            {
                for (RecipeItemStruct item : recipeStruct.items)
                {
                    if (item.elementType == RecipeItemStruct.RecipeElementType.result)
                    {
                        continue;
                    }
                    for (IdDamagePairWithStack recipeItemStruct : item.itemIds)
                    {
                        if (!recipeItemStruct.getItemStack().getDisplayName().equals("Nether Quartz Dust"))
                        {
                            recipeStruct.useInRawCostCalculation = false;
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void correctRecipeItemStruct(IdDamagePairWithStack recipeItemStruct)
    {

    }
}
