package trks.recipedoc.generate.structs;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import net.minecraft.util.MD5String;
import trks.recipedoc.generate.loaders.DataLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class RecipeStruct
{

    public Collection<RecipeItemStruct> items;
    public String recipeHandlerName;
    public boolean visible = true;
    public Collection<String> itemMods;
    public boolean useInRawCostCalculation = true;

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
            for (IdDamagePair itemId : recipeItem.itemIds)
            {
                itemMods.add(DataLoader.getItemModId(itemId.itemId));
            }
        }
        this.itemMods = itemMods;
    }

    public boolean hasIngredient(IdDamagePair itemId)
    {
        for (RecipeItemStruct item : items)
        {
            if (item.elementType != RecipeItemStruct.RecipeElementType.result && item.hasIngredient(itemId))
            {
                return true;
            }
        }
        return false;
    }

    public IdDamagePairWithStack getResult()
    {
        for (RecipeItemStruct item : items)
        {
            if (item.elementType == RecipeItemStruct.RecipeElementType.result)
            {
                return item.itemIds.get(0);
            }
        }
        return null;
    }

    public boolean hasItemContainingLowercaseTextInNameAs(String partOfName, RecipeItemStruct.RecipeElementType elementType)
    {
        for (RecipeItemStruct item : items)
        {
            if (item.elementType == elementType)
            {
                for (IdDamagePairWithStack itemId : item.itemIds)
                {
                    if (itemId.getItemStack().getDisplayName().toLowerCase().contains(partOfName))
                    {
                        return true;
                    }
                }

            }
        }
        return false;
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
            for (IdDamagePairWithStack itemId : item.itemIds)
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
            for (IdDamagePair itemId : item.itemIds)
            {
                stringBuilder.append(itemId.itemId);
                stringBuilder.append(itemId.damageId);
            }
        }

        return (new MD5String("")).getMD5String(stringBuilder.toString());
    }
}
