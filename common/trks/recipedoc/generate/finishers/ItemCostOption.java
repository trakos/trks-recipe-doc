package trks.recipedoc.generate.finishers;

import trks.recipedoc.generate.structs.IdDamagePair;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;

import java.util.HashMap;
import java.util.HashSet;

public class ItemCostOption implements Cloneable
{
    class IngredientInfo
    {
        public float amount = 0;
        public boolean isBaseItem = false;

        IngredientInfo(float amount, boolean isBaseItem)
        {
            this.amount = amount;
            this.isBaseItem = isBaseItem;
        }

        @Override
        public IngredientInfo clone()
        {
            return new IngredientInfo(this.amount, this.isBaseItem);
        }
    }

    public HashMap<IdDamagePair, IngredientInfo> items = new HashMap<IdDamagePair, IngredientInfo>();
    public boolean areAllItemsBase = false;
    public float resultAmount = 1;
    public HashSet<String> craftingHandlers = new HashSet<String>();

    protected ItemCostOption()
    {
    }

    public ItemCostOption(RecipeStruct recipeStruct, HashMap<IdDamagePair, ItemStruct> itemStructHashMap)
    {
        areAllItemsBase = true;
        for (RecipeItemStruct ingredient : recipeStruct.items)
        {
            if (ingredient.elementType == RecipeItemStruct.RecipeElementType.result)
            {
                resultAmount = ingredient.amount;
                continue;
            }
            else if (recipeStruct.recipeHandlerName.equals("Smelting") && ingredient.elementType == RecipeItemStruct.RecipeElementType.other)
            {
                continue;
            }
            IdDamagePair idDamagePairWithStack = ingredient.itemIds.get(0);
            for (IdDamagePair itemId : ingredient.itemIds)
            {
                ItemStruct itemStruct = itemStructHashMap.get(itemId);
                if (itemStruct == null)
                {
                    throw new RuntimeException("no item " + itemId.itemId + ":" + itemId.damageId + " found!");
                }
                if (itemStruct.isBaseItem)
                {
                    idDamagePairWithStack = itemStruct;
                    break;
                }
            }
            addIngredient(idDamagePairWithStack, ingredient.amount, itemStructHashMap.get(idDamagePairWithStack).isBaseItem);
        }
        craftingHandlers.add(recipeStruct.recipeHandlerName);
        _checkIfBase();
    }

    public boolean isThisEqualToOrCheaperThan(ItemCostOption otherOption)
    {
        /*for (String craftingHandler : craftingHandlers)
        {
            if (!otherOption.craftingHandlers.contains(craftingHandler))
            {
                return false;
            }
        }*/


        for (IdDamagePair idDamagePair : items.keySet())
        {
            if (!otherOption.hasItem(idDamagePair) || (otherOption.items.get(idDamagePair).amount) < items.get(idDamagePair).amount)
            {
                return false;
            }
        }
        // other option has every item that we have in bigger or equal quantity = we're cheaper
        return true;
    }

    protected void addIngredient(IdDamagePair item, float amount, boolean isBaseItem)
    {
        if (items.containsKey(item))
        {
            items.get(item).amount += amount;
        }
        else
        {
            items.put(item, new IngredientInfo(amount, isBaseItem));
        }
    }

    @Override
    public ItemCostOption clone()
    {
        ItemCostOption other = new ItemCostOption();
        for (IdDamagePair idDamagePair : items.keySet())
        {
            other.items.put(idDamagePair.clone(), items.get(idDamagePair).clone());
        }
        other.resultAmount = resultAmount;
        other.areAllItemsBase = areAllItemsBase;
        return other;
    }

    public ItemCostOption getCloneWithItemCrafted(IdDamagePair item, ItemCostOption recipeCost)
    {
        if (items.get(item).isBaseItem)
        {
            throw new RuntimeException("Why substitute a base item?");
        }
        ItemCostOption newCostOption = this.clone();
        float recipeNeedAmount = newCostOption.items.get(item).amount;
        newCostOption.items.remove(item);
        for (IdDamagePair idDamagePair : recipeCost.items.keySet())
        {
            float ingredientAmount = recipeCost.items.get(idDamagePair).amount * recipeNeedAmount / recipeCost.resultAmount;
            newCostOption.addIngredient(idDamagePair, ingredientAmount, recipeCost.items.get(idDamagePair).isBaseItem);
        }
        newCostOption._checkIfBase();
        return newCostOption;
    }

    public boolean hasItem(IdDamagePair itemId)
    {
        return items.containsKey(itemId);
    }

    protected void _checkIfBase()
    {
        areAllItemsBase = true;
        for (IdDamagePair idDamagePair : items.keySet())
        {
            if (!items.get(idDamagePair).isBaseItem)
            {
                areAllItemsBase = false;
                break;
            }
        }
    }
}
