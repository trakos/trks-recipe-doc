package trks.recipedoc.generate.finishers;

import trks.recipedoc.generate.structs.IdDamagePair;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;

import java.util.*;

public class ItemCostCalculator
{
    public HashMap<IdDamagePair, ItemStruct> itemsMap = new HashMap<IdDamagePair, ItemStruct>();
    protected final ArrayList<RecipeStruct> recipes;
    protected final ArrayList<ItemStruct> items;

    protected HashMap<IdDamagePair, ItemCostOptionList> itemCostsMap = new HashMap<IdDamagePair, ItemCostOptionList>();

    public ItemCostCalculator(ArrayList<ItemStruct> items, ArrayList<RecipeStruct> recipeStructs)
    {
        this.items = items;
        this.recipes = recipeStructs;
        mapItemsByIds();
        calculate();

        for (ItemStruct item : this.items)
        {
            ItemCostOptionList itemCostOptionList = itemCostsMap.get(item);
            if (itemCostOptionList != null)
            {
                for (ItemCostOption itemCostOption : itemCostOptionList.itemCostOptionList)
                {
                    if (!itemCostOption.areAllItemsBase)
                    {
                        continue;
                    }
                    HashMap<IdDamagePair, Float> cost = new HashMap<IdDamagePair, Float>();
                    for (IdDamagePair idDamagePair : itemCostOption.items.keySet())
                    {
                        cost.put(idDamagePair, itemCostOption.items.get(idDamagePair).amount / itemCostOption.resultAmount);
                    }
                    item.rawCosts.add(cost);
                }
            }
        }

    }

    protected void calculate()
    {
        HashMap<IdDamagePair, ArrayList<RecipeStruct>> idDamagePairArrayListHashMap = _calculateRecipesMap();
        for (IdDamagePair idDamagePair : idDamagePairArrayListHashMap.keySet())
        {
            ItemCostOptionList itemCostOptionList = new ItemCostOptionList(idDamagePairArrayListHashMap.get(idDamagePair), this.itemsMap);
            if (itemCostOptionList.itemCostOptionList.size() > 0)
            {
                itemCostsMap.put(idDamagePair, itemCostOptionList);
            }
        }

        List<ItemStruct> sortedItems = (List<ItemStruct>) items.clone();
        Collections.sort(sortedItems, new Comparator<ItemStruct>()
        {
            @Override
            public int compare(ItemStruct o1, ItemStruct o2)
            {
                return ((Float) o1.craftingComplexity).compareTo(o2.craftingComplexity);
            }
        });
        for (IdDamagePair resultId : sortedItems)
        {
            ItemCostOptionList costList = itemCostsMap.get(resultId);
            if (costList == null)
            {
                continue;
            }
            System.out.println("Calculating raw cost for " + itemsMap.get(resultId).name + "...");
            boolean somethingChangedInCurrentLoopIteration = true;
            while (somethingChangedInCurrentLoopIteration)
            {
                somethingChangedInCurrentLoopIteration = false;
                for (IdDamagePair ingredientId : costList.getNonBaseItemsFromAllPaths())
                {
                    System.out.println("\tSubstituting " + itemsMap.get(ingredientId).name + "...");
                    ItemCostOptionList ingredientCostOptionsList = itemCostsMap.get(ingredientId);
                    if (ingredientCostOptionsList == null)
                    {
                        continue;
                    }
                    boolean anyChange = costList.substituteItemWithCosts(resultId, ingredientCostOptionsList, ingredientId);
                    somethingChangedInCurrentLoopIteration = somethingChangedInCurrentLoopIteration || anyChange;
                    if (anyChange && costList.itemCostOptionList.size() > 20)
                    {
                        somethingChangedInCurrentLoopIteration = false;
                        System.err.println("\t\tbreaking - " + costList.itemCostOptionList.size() + " options!");
                        break;
                    }
                    System.out.println("\t\t" + costList.itemCostOptionList.size());
                }
            }
            costList.removeUnfinished();
            if (costList.itemCostOptionList.size() > 1)
            {
                System.out.println("\tFinished options: " + costList.itemCostOptionList.size());
            }
        }
    }

    protected HashMap<IdDamagePair, ArrayList<RecipeStruct>> _calculateRecipesMap()
    {
        HashMap<IdDamagePair, ArrayList<RecipeStruct>> recipeMap = new HashMap<IdDamagePair, ArrayList<RecipeStruct>>();
        for (RecipeStruct recipe : this.recipes)
        {
            if (!recipe.useInRawCostCalculation)
            {
                continue;
            }
            for (RecipeItemStruct item : recipe.items)
            {
                if (item.elementType == RecipeItemStruct.RecipeElementType.result)
                {
                    IdDamagePair idDamagePair = item.itemIds.get(0);
                    if (itemsMap.get(idDamagePair) != null && !itemsMap.get(idDamagePair).isBaseItem)
                    {
                        if (recipe.hasIngredient(idDamagePair))
                        {
                            System.out.println("circular recipe for " + idDamagePair.itemId + ":" + idDamagePair.damageId);
                        }
                        else
                        {
                            if (!recipeMap.containsKey(idDamagePair))
                            {
                                recipeMap.put(idDamagePair, new ArrayList<RecipeStruct>());
                            }
                            recipeMap.get(idDamagePair).add(recipe);
                        }
                    }
                    break;
                }
            }
        }
        return recipeMap;
    }

    public void _printNonCraftableNonBaseItems()
    {
        HashMap<IdDamagePair, ArrayList<RecipeStruct>> recipeMap = _calculateRecipesMap();
        for (ItemStruct item : items)
        {
            IdDamagePair idObject = new IdDamagePair(item.itemId, item.damageId);
            if (!recipeMap.containsKey(idObject) && !item.isBaseItem && item.showOnList)
            {
                System.out.println("Not found: " + item.name);
            }
        }
    }

    public void _printNotCraftedItems()
    {
        for (IdDamagePair idDamagePair : itemCostsMap.keySet())
        {
            ItemCostOptionList itemCostOptionList = itemCostsMap.get(idDamagePair);
            if (!itemCostOptionList.hasAnyFinishedPath())
            {
                ItemStruct itemStruct = itemsMap.get(idDamagePair);
                System.out.println(itemStruct.name + " (" + itemStruct.itemId + ":" + itemStruct.damageId + ")");
            }

        }

    }

    public void mapItemsByIds()
    {
        for (ItemStruct item : this.items)
        {
            itemsMap.put(new IdDamagePair(item.itemId, item.damageId), item);
        }
    }
}
