package trks.recipedoc.modsupport.mods;

import codechicken.nei.recipe.ICraftingHandler;
import trks.recipedoc.api.IDocModSupport;
import trks.recipedoc.api.IRecipeHandlerMachineRegistrar;
import trks.recipedoc.generate.structs.IdDamagePairWithStack;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class ThermalExpansionSupport implements IDocModSupport
{
    @Override
    public Boolean shouldCorrectItemFromMod(String modId)
    {
        return modId.equals("ThermalExpansion");
    }

    @Override
    public void correctItemStruct(ItemStruct itemStruct, IRecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar)
    {
        // as they are registered in forge ore dict, they are treated as base, but they're not really
        // (some of them are blends)
        //if (itemStruct.rawName.startsWith("item.material.dust") || itemStruct.rawName.equals("item.material.ingotInvar") || itemStruct.rawName.equals("item.material.ingotElectrum"))
        if (!itemStruct.rawName.startsWith("ore.") && !itemStruct.rawName.equals("item.material.sawdust"))
        {
            if (itemStruct.rawName.toLowerCase().contains("ingot"))
            {
                itemStruct.craftingComplexity = .01f;
            }
            else if (itemStruct.isBaseItem)
            {
                itemStruct.craftingComplexity = .1f;
                itemStruct.isBaseItem = false;
            }
        }
    }

    @Override
    public boolean isBaseItem(ItemStruct itemStruct)
    {
        return itemStruct.rawName.equals("item.material.sawdust") || itemStruct.rawName.toLowerCase().contains("ingot");
    }

    @Override
    public Collection<String> getModsRequiredToBeLoaded()
    {
        return Arrays.asList("ThermalExpansion");
    }

    @Override
    public Map<String, Float> getNewCategories()
    {
        return null;
    }

    @Override
    public void correctRecipeStruct(RecipeStruct recipeStruct, ICraftingHandler handler)
    {
        if (recipeStruct.recipeHandlerName.equals("thermalexpansion.plugins.nei.handlers.PulverizerRecipeHandler"))
        {
            for (Iterator<RecipeItemStruct> iterator = recipeStruct.items.iterator(); iterator.hasNext(); )
            {
                RecipeItemStruct item = iterator.next();
                if (item.elementType == RecipeItemStruct.RecipeElementType.other)
                {
                    if (recipeStruct.getResult().getItemStack().getItemName().equals("item.material.dustPlatinum"))
                    {
                        // netherrack - another result, but lets just ignore it, its worthless anyway
                        iterator.remove();
                    }
                    else
                    {
                        recipeStruct.useInRawCostCalculation = false;
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void correctRecipeItemStruct(IdDamagePairWithStack recipeItemStruct)
    {

    }
}
