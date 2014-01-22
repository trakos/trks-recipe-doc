package trks.recipedoc.generate.structs;

import codechicken.nei.PositionedStack;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class RecipeItemStruct
{
    public enum RecipeElementType
    {
        ingredient, other, result
    }

    public class RecipeItemIdStruct
    {
        public RecipeItemIdStruct(int itemId, int damageId)
        {
            this.itemId = itemId;
            this.damageId = damageId;
        }

        public int itemId;
        public int damageId;
    }

    public int relativeX;
    public int relativeY;
    public RecipeElementType elementType;
    public Collection<RecipeItemIdStruct> itemIds;
    public int amount;

    public RecipeItemStruct(int relativeX, int relativeY, RecipeElementType elementType, Collection<RecipeItemIdStruct> itemIds, int amount)
    {
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.elementType = elementType;
        this.itemIds = itemIds;
        this.amount = amount;
    }

    public RecipeItemStruct(PositionedStack stack, RecipeElementType elementType)
    {
        this.relativeX = stack.relx;
        this.relativeY = stack.rely;
        this.elementType = elementType;

        ArrayList<RecipeItemIdStruct> recipeItems = new ArrayList<RecipeItemIdStruct>();
        for (ItemStack item : stack.items)
        {
            recipeItems.add(new RecipeItemIdStruct(item.itemID, item.getItemDamage()));
        }
        this.itemIds = recipeItems;
        this.amount = stack.items.length > 0 ? stack.items[0].stackSize : 0;
    }
}
