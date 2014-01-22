package trks.recipedoc.generate.structs;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import net.minecraft.util.MD5String;
import trks.recipedoc.generate.loaders.DataLoader;
import trks.recipedoc.modsupport.ModSupportHandler;

import java.awt.*;
import java.util.*;
import java.util.List;

public class RecipeStruct
{
    public Collection<RecipeItemStruct> items;
    public String recipeHandlerName;
    public boolean visible = true;
    public Collection<String> itemMods;

    public RecipeStruct(ICraftingHandler craftingHandler, int recipeNumber)
    {
        recipeHandlerName = craftingHandler.getRecipeName();
        getIngredients(craftingHandler, recipeNumber);
    }

    protected void getIngredients(IRecipeHandler recipeHandler, int recipeNumber)
    {
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

        this.items = recipeItems;

        HashSet<String> itemMods = new HashSet<String>();
        for (RecipeItemStruct recipeItem : recipeItems)
        {
            for (RecipeItemStruct.RecipeItemIdStruct itemId : recipeItem.itemIds)
            {
                itemMods.add(DataLoader.getItemModId(itemId.itemId));
            }
        }
        this.itemMods = itemMods;
    }

    /**
     * Just an util method returning true if any ingredient or result is "instanceof" it
     * @param classType class for
     * @return true if found
     */
    public boolean containsItemOfClass(Class classType)
    {
        for (RecipeItemStruct item : items)
        {
            for (RecipeItemStruct.RecipeItemIdStruct itemId : item.itemIds)
            {
                if (classType.isInstance(itemId.getItemStack().getItem()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Should be the same for the same recipe
     * @return hash of the concatenated items data
     */
    public String generateRecipeHash()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(recipeHandlerName);
        for (RecipeItemStruct item : items)
        {
            stringBuilder.append(item.amount);
            stringBuilder.append(item.elementType.toString());
            stringBuilder.append(item.relativeX);
            stringBuilder.append(item.relativeY);
            for (RecipeItemStruct.RecipeItemIdStruct itemId : item.itemIds)
            {
                stringBuilder.append(itemId.itemId);
                stringBuilder.append(itemId.damageId);
            }
        }

        return (new MD5String("")).getMD5String(stringBuilder.toString());
    }
}
