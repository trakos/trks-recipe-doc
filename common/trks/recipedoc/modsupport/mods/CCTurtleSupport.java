package trks.recipedoc.modsupport.mods;

import codechicken.nei.recipe.ICraftingHandler;
import net.minecraft.item.ItemStack;
import trks.recipedoc.api.IDocModSupport;
import trks.recipedoc.api.IRecipeHandlerMachineRegistrar;
import trks.recipedoc.generate.structs.IdDamagePairWithStack;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;

import java.util.*;

/**
 * Created by trakos on 30.01.14.
 */
public class CCTurtleSupport implements IDocModSupport
{

    @Override
    public Boolean shouldCorrectItemFromMod(String modId)
    {
        return modId.equals("CCTurtle");
    }

    @Override
    public void correctItemStruct(ItemStruct itemStruct, IRecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar)
    {
        itemStruct.damageId = getOverwrittenItemDamage(itemStruct.getItemStack());
    }

    @Override
    public boolean isBaseItem(ItemStruct itemStruct)
    {
        return itemStruct.isBaseItem;
    }

    @Override
    public Collection<String> getModsRequiredToBeLoaded()
    {
        return Arrays.asList("CCTurtle");
    }

    @Override
    public Map<String, Float> getNewCategories()
    {
        return null;
    }

    HashSet<Integer> recipesForTurtleAdded = new HashSet<Integer>();
    @Override
    public void correctRecipeStruct(RecipeStruct recipeStruct, ICraftingHandler handler)
    {
        IdDamagePairWithStack result = recipeStruct.getResult();
        if (result != null && result.getItemStack().getItemName().equals("tile.ccturtle"))
        {
            if (recipesForTurtleAdded.contains(result.damageId))
            {
                recipeStruct.useInRawCostCalculation = false;
            }
            else
            {
                recipesForTurtleAdded.add(result.damageId);
            }
        }
    }

    @Override
    public void correctRecipeItemStruct(IdDamagePairWithStack recipeItemStruct)
    {
        recipeItemStruct.damageId = getOverwrittenItemDamage(recipeItemStruct.getItemStack());
    }

    int nextTurtleAddedDamage = 1000;
    HashMap<String, Integer> turtleCustomDamages = new HashMap<String, Integer>();

    protected int getOverwrittenItemDamage(ItemStack itemStack)
    {
        if (itemStack.getItemName().equals("tile.ccturtle"))
        {
            Integer damageId = turtleCustomDamages.get(itemStack.getDisplayName());
            if (damageId == null)
            {
                damageId = nextTurtleAddedDamage++;
                turtleCustomDamages.put(itemStack.getDisplayName(), damageId);
            }
            return damageId;
        }
        return itemStack.getItemDamage();
    }
}
