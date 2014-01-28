package trks.recipedoc.generate.finishers;

import trks.recipedoc.generate.structs.IdDamagePair;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;

import java.util.*;

public class ItemCostOptionList
{
    public boolean allPathsFinished;
    public ArrayList<ItemCostOption> itemCostOptionList = new ArrayList<ItemCostOption>();
    protected ArrayList<ItemCostOption> optionsRemoved = new ArrayList<ItemCostOption>();

    public ItemCostOptionList(ArrayList<RecipeStruct> recipes, HashMap<IdDamagePair, ItemStruct> itemStructHashMap)
    {
        for (RecipeStruct recipe : recipes)
        {
            try
            {
                itemCostOptionList.add(new ItemCostOption(recipe, itemStructHashMap));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        calculateIfAllPathsFinished();
    }

    public boolean hasAnyFinishedPath()
    {
        for (ItemCostOption itemCostOption : itemCostOptionList)
        {
            if (itemCostOption.areAllItemsBase)
            {
                return true;
            }
        }
        return false;
    }

    protected void calculateIfAllPathsFinished()
    {
        allPathsFinished = true;
        for (ItemCostOption itemCostOption : itemCostOptionList)
        {
            if (!itemCostOption.areAllItemsBase)
            {
                allPathsFinished = false;
                break;
            }
        }

    }

    public Collection<IdDamagePair> getNonBaseItemsFromAllPaths()
    {
        if (allPathsFinished)
        {
            return new ArrayList<IdDamagePair>(0);
        }
        HashSet<IdDamagePair> items = new HashSet<IdDamagePair>();
        for (ItemCostOption itemCostOption : itemCostOptionList)
        {
            if (itemCostOption.areAllItemsBase)
            {
                continue;
            }
            for (IdDamagePair idDamagePair : itemCostOption.items.keySet())
            {
                if (!itemCostOption.items.get(idDamagePair).isBaseItem)
                {
                    items.add(idDamagePair);
                }
            }
        }
        return items;
    }

    /**
     *
     *
     * @param resultId resultId
     * @param ingredientCostList ingredientCostList
     * @param ingredientItemId ingredientItemId
     * @return whether anything has changed (it might not, if all paths were worse)
     */
    public boolean substituteItemWithCosts(IdDamagePair resultId, ItemCostOptionList ingredientCostList, IdDamagePair ingredientItemId)
    {
        boolean anythingAdded = false;
        HashSet<ItemCostOption> optionsToRemove = new HashSet<ItemCostOption>();
        HashSet<ItemCostOption> optionsToAdd = new HashSet<ItemCostOption>();
        for (Iterator<ItemCostOption> iterator1 = itemCostOptionList.iterator(); iterator1.hasNext(); )
        {
            ItemCostOption resultCostOption = iterator1.next();
            if (optionsToRemove.contains(resultCostOption))
            {
                continue;
            }
            if (!resultCostOption.areAllItemsBase && resultCostOption.hasItem(ingredientItemId))
            {
                for (ItemCostOption ingredientCostOption : ingredientCostList.itemCostOptionList)
                {
                    if (ingredientCostOption.hasItem(resultId))
                    {
                        continue;
                    }
                    ItemCostOption newResultCostOption = resultCostOption.getCloneWithItemCrafted(ingredientItemId, ingredientCostOption);
                    boolean isWorthAdding = true;
                    for (ItemCostOption resultCostOptionToCompare : itemCostOptionList)
                    {
                        if (resultCostOptionToCompare.isThisEqualToOrCheaperThan(newResultCostOption))
                        {
                            isWorthAdding = false;
                        }
                        else if (newResultCostOption.isThisEqualToOrCheaperThan(resultCostOptionToCompare))
                        {
                            optionsToRemove.add(resultCostOptionToCompare);
                        }
                    }
                    if (isWorthAdding)
                    {
                        for (ItemCostOption resultCostOptionToCompare : optionsRemoved)
                        {
                            if (resultCostOptionToCompare.isThisEqualToOrCheaperThan(newResultCostOption))
                            {
                                isWorthAdding = false;
                            }
                        }
                        if (isWorthAdding)
                        {
                            optionsToAdd.add(newResultCostOption);
                            anythingAdded = true;
                        }
                    }
                }
                optionsRemoved.add(resultCostOption);
                iterator1.remove();
            }
        }
        for (ItemCostOption costOptionToRemove : optionsToRemove)
        {
            for (Iterator<ItemCostOption> iterator = itemCostOptionList.iterator(); iterator.hasNext(); )
            {
                ItemCostOption costOption = iterator.next();
                if (costOption == costOptionToRemove)
                {
                    iterator.remove();
                    break;
                }
            }
        }
        for (ItemCostOption costOptionToAdd : optionsToAdd)
        {
            itemCostOptionList.add(costOptionToAdd);
        }

        calculateIfAllPathsFinished();
        return anythingAdded;
    }

    public void removeUnfinished()
    {
        for (Iterator<ItemCostOption> iterator = itemCostOptionList.iterator(); iterator.hasNext(); )
        {
            ItemCostOption itemCostOption = iterator.next();
            if (!itemCostOption.areAllItemsBase)
            {
                iterator.remove();
            }
        }
        allPathsFinished = true;
    }
}
