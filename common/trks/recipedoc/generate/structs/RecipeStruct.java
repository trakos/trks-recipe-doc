package trks.recipedoc.generate.structs;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;

import java.awt.*;
import java.util.*;

public class RecipeStruct
{
    public Collection<RecipeItemStruct> items;
    public String recipeHandlerName;

    public RecipeStruct(ICraftingHandler craftingHandler, int recipeNumber)
    {
        recipeHandlerName = craftingHandler.getRecipeName();
        items = getIngredients(craftingHandler, recipeNumber);
    }

    static protected ArrayList<RecipeItemStruct> getIngredients(IRecipeHandler recipeHandler, int recipeNumber)
    {
        Point p = new Point(5, 16);
        ArrayList<RecipeItemStruct> recipeItems = new ArrayList<RecipeItemStruct>();

        java.util.List<PositionedStack> stacks = recipeHandler.getIngredientStacks(recipeNumber);
        for(PositionedStack stack : stacks)
        {
            recipeItems.add(new RecipeItemStruct(stack, RecipeItemStruct.RecipeElementType.ingredient));
        }
        stacks = recipeHandler.getOtherStacks(recipeNumber);
        for(PositionedStack stack : stacks)
        {
            recipeItems.add(new RecipeItemStruct(stack, RecipeItemStruct.RecipeElementType.other));
        }
        PositionedStack result = recipeHandler.getResultStack(recipeNumber);
        if(result != null)
        {
            recipeItems.add(new RecipeItemStruct(result, RecipeItemStruct.RecipeElementType.result));
        }

        return recipeItems;
    }
}
