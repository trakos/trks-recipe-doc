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

public class GalactiCraftSupport implements IDocModSupport
{
    @Override
    public Boolean shouldCorrectItemFromMod(String modId)
    {
        return modId.equals("GalacticraftCore") || modId.equals("GalacticraftMars");
    }

    @Override
    public void correctItemStruct(ItemStruct itemStruct, IRecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar)
    {
        if (itemStruct.rawName.equals("tile.moonBlock.tinmoon") || itemStruct.rawName.equals("tile.moonBlock.coppermoon"))
        {
            itemStruct.isBaseItem = false;
        }
    }

    @Override
    public boolean isBaseItem(ItemStruct itemStruct)
    {
        return itemStruct.rawName.equals("tile.mars.marsstone")
                || itemStruct.rawName.equals("tile.mars.marsdirt")
                || itemStruct.rawName.equals("tile.mars.marsgrass")
                || itemStruct.rawName.equals("tile.mars.deshmars");
    }

    @Override
    public Collection<String> getModsRequiredToBeLoaded()
    {
        return Arrays.asList("GalacticraftCore");
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
