package trks.recipedoc.generate.structs;

import codechicken.nei.PositionedStack;
import net.minecraft.item.ItemStack;
import trks.recipedoc.modsupport.ModSupportHandler;

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
        protected final ItemStack itemStack;

        public RecipeItemIdStruct(int itemId, int damageId, ItemStack itemStack)
        {
            this.itemId = itemId;
            this.damageId = damageId;
            this.itemStack = itemStack;
        }

        public int itemId;
        public int damageId;


        public ItemStack getItemStack()
        {
            return itemStack;
        }
    }

    public int relativeX;
    public int relativeY;
    public RecipeElementType elementType;
    public Collection<RecipeItemIdStruct> itemIds;
    public int amount;

    public RecipeItemStruct(PositionedStack stack, RecipeElementType elementType)
    {
        this.relativeX = stack.relx;
        this.relativeY = stack.rely;
        this.elementType = elementType;

        ArrayList<RecipeItemIdStruct> recipeItems = new ArrayList<RecipeItemIdStruct>();
        for (ItemStack item : stack.items)
        {
            recipeItems.add(ModSupportHandler.correctRecipeItemStruct(new RecipeItemIdStruct(item.itemID, item.getItemDamage(), item)));
        }
        this.itemIds = recipeItems;
        this.amount = stack.items.length > 0 ? stack.items[0].stackSize : 0;
    }
}
