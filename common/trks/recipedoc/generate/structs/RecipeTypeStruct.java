package trks.recipedoc.generate.structs;

import codechicken.nei.recipe.FurnaceRecipeHandler;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.ShapedRecipeHandler;
import codechicken.nei.recipe.ShapelessRecipeHandler;
import trks.recipedoc.generate.renderers.utils.RecipeBackgroundRenderer;

import java.util.HashSet;

public class RecipeTypeStruct
{
    public String typeId;
    public String name;
    public String image;
    public HashSet<IdDamagePair> machines = new HashSet<IdDamagePair>();

    public ICraftingHandler getCraftingHandler()
    {
        return craftingHandler;
    }

    protected final ICraftingHandler craftingHandler;
    public RecipeTypeStruct(ICraftingHandler recipeHandler)
    {
        this.craftingHandler = recipeHandler;
        this.typeId = recipeHandler.getRecipeName();
        this.name = recipeHandler.getRecipeName();
        this.image = RecipeBackgroundRenderer.getRecipeHandlerImageName(recipeHandler);

        if (recipeHandler instanceof FurnaceRecipeHandler)
        {
            registerMachineHandling(61, 0);
        }
        if (recipeHandler instanceof ShapelessRecipeHandler || recipeHandler instanceof ShapedRecipeHandler)
        {
            registerMachineHandling(58, 0);
        }
    }

    public void registerMachineHandling(int itemId, int damageId)
    {
        machines.add(new IdDamagePair(itemId, damageId));
    }
}
