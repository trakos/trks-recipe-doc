package trks.recipedoc.generate.structs;

import codechicken.nei.PositionedStack;
import net.minecraft.item.ItemStack;
import trks.recipedoc.modsupport.ModSupportHandler;

import java.util.ArrayList;

public class RecipeItemStruct
{
    public enum RecipeElementType
    {
        ingredient, other, result
    }

    public int relativeX;
    public int relativeY;
    public RecipeElementType elementType;
    public ArrayList<IdDamagePairWithStack> itemIds;
    public int amount;

    public RecipeItemStruct(PositionedStack stack, RecipeElementType elementType)
    {
        this.relativeX = stack.relx;
        this.relativeY = stack.rely;
        this.elementType = elementType;

        ArrayList<IdDamagePairWithStack> recipeItems = new ArrayList<IdDamagePairWithStack>();
        for (Object itemO : stack.items)
        {
            ItemStack item = (ItemStack) itemO;
            recipeItems.add(ModSupportHandler.correctRecipeItemStruct(new IdDamagePairWithStack(item.itemID, item.getItemDamage(), item)));
        }
        this.itemIds = recipeItems;
        this.amount = stack.items.length > 0 ? ((ItemStack)(Object)stack.items[0]).stackSize : 0;
    }

    public boolean hasIngredient(IdDamagePair itemId)
    {
        for (IdDamagePairWithStack ingredient : itemIds)
        {
            if (ingredient.equals(itemId))
            {
                return true;
            }
        }
        return false;
    }
}
