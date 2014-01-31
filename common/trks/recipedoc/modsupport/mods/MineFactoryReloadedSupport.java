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
import java.util.Map;

/**
 * Created by trakos on 30.01.14.
 */
public class MineFactoryReloadedSupport implements IDocModSupport
{
    @Override
    public Boolean shouldCorrectItemFromMod(String modId)
    {
        return modId.equals("MineFactoryReloaded");
    }

    @Override
    public void correctItemStruct(ItemStruct itemStruct, IRecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar)
    {
        if (itemStruct.rawName.equals("item.mfr.plastic.sheet") || itemStruct.rawName.equals("item.mfr.plastic.raw") || itemStruct.rawName.equals("item.mfr.rubber.bar"))
        {
            itemStruct.isBaseItem = false;
        }
    }

    @Override
    public boolean isBaseItem(ItemStruct itemStruct)
    {
        return itemStruct.rawName.equals("item.mfr.rubber.raw") || itemStruct.rawName.equals("item.mfr.pinkslimeball") || itemStruct.rawName.equals("tile.ice");
    }

    @Override
    public Collection<String> getModsRequiredToBeLoaded()
    {
        return Arrays.asList("MineFactoryReloaded");
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
        if (result != null && result.getItemStack().getDisplayName().equals("Hardened Glass")
            && recipeStruct.hasItemContainingLowercaseTextInNameAs("pulverized lead", RecipeItemStruct.RecipeElementType.ingredient))
        {
            recipeStruct.useInRawCostCalculation = false;
        }
    }

    @Override
    public void correctRecipeItemStruct(IdDamagePairWithStack recipeItemStruct)
    {

    }
}
