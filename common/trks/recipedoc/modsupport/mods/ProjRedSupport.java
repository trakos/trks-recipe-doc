package trks.recipedoc.modsupport.mods;

import codechicken.nei.recipe.ICraftingHandler;
import net.minecraft.item.ItemStack;
import trks.recipedoc.api.API;
import trks.recipedoc.api.IDocModSupport;
import trks.recipedoc.api.IRecipeHandlerMachineRegistrar;
import trks.recipedoc.generate.structs.IdDamagePairWithStack;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ProjRedSupport implements IDocModSupport
{

    @Override
    public Boolean shouldCorrectItemFromMod(String modId)
    {
        return modId.startsWith("ProjRed");
    }

    @Override
    public void correctItemStruct(ItemStruct itemStruct, IRecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar)
    {
        ItemStack itemStack = itemStruct.getItemStack();

        if (itemStruct.rawName.startsWith("item.projectred.core.part.gem"))
        {
            itemStruct.category = API.STANDARD_CATEGORY_ORE;
            itemStruct.isBaseItem = true;
        }
    }

    @Override
    public boolean isBaseItem(ItemStruct itemStruct)
    {
        return itemStruct.rawName.startsWith("item.projectred.core.part.gem");
    }

    @Override
    public Collection<String> getModsRequiredToBeLoaded()
    {
        return Arrays.asList("ProjRed");
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
