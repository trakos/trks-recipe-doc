package trks.recipedoc.generate.loaders;

import codechicken.nei.ItemList;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.*;

import java.util.*;

public class DataNEIFetcher
{
    static protected ArrayList<ItemStack> items = null;

    static public ArrayList<ItemStack> getItems()
    {
        if (items == null)
        {
            ItemList.ThreadLoadItems thread = new ItemList.ThreadLoadItems();
            thread.run();

            items = ItemList.items;
        }
        return items;
    }

    static protected ArrayList<ICraftingHandler> craftingHandlers = null;
    static public ArrayList<ICraftingHandler> getCraftingHandlers()
    {
        if (craftingHandlers == null)
        {
            craftingHandlers = getActiveCraftingHandlers();
        }
        return craftingHandlers;
    }

    static protected ArrayList<ICraftingHandler> getActiveCraftingHandlers()
    {
        ArrayList<ICraftingHandler> foundHandlers = new ArrayList<ICraftingHandler>();
        ArrayList<ICraftingHandler> remainingHandlers = new ArrayList<ICraftingHandler>(GuiCraftingRecipe.craftinghandlers);
        for (ItemStack item : getItems())
        {
            Iterator<ICraftingHandler> craftingHandlerIterator = remainingHandlers.iterator();
            while (craftingHandlerIterator.hasNext())
            {
                ICraftingHandler craftingHandler = craftingHandlerIterator.next();
                ICraftingHandler handler = craftingHandler.getRecipeHandler("item", item.copy());
                if (handler.numRecipes() > 0)
                {
                    foundHandlers.add(handler);
                    craftingHandlerIterator.remove();
                }
            }
            if (remainingHandlers.isEmpty())
            {
                break;
            }
        }
        if (foundHandlers.isEmpty()) throw new RuntimeException("No recipe handlers found; called too early?");
        return foundHandlers;
    }

    /**
     * Based on the way NEI categorizes stuff
     *
     * @param itemStack itemStack
     *
     * @return dot - separated category string
     */
    static public String getItemType(ItemStack itemStack)
    {
        Item item = itemStack.getItem();
        int itemID = item.itemID;
        if(itemID < Block.blocksList.length && (Block.blocksList[itemID] != null && Block.blocksList[itemID].blockMaterial != Material.air))
        {
            // block
            if (itemID == 52)
            {
                return "Blocks.MobSpawners";
            }
            else
            {
                return "Blocks.Regular";
            }
        }
        else
        {
            if(item.isDamageable())
            {
                if(item instanceof ItemPickaxe)
                {
                    return "Items.Tools.Pickaxes";
                }
                else if(item instanceof ItemSpade)
                {
                    return "Items.Tools.Shovels";
                }
                else if(item instanceof ItemAxe)
                {
                    return "Items.Tools.Axes";
                }
                else if(item instanceof ItemHoe)
                {
                    return "Items.Tools.Hoes";
                }
                else if(item instanceof ItemSword)
                {
                    return "Items.Weapons.Swords";
                }
                else if(item instanceof ItemArmor)
                {
                    switch(((ItemArmor) item).armorType)
                    {
                        case 0:
                            return "Items.Armor.Helmets";
                        case 1:
                            return "Items.Armor.ChestPlates";
                        case 2:
                            return "Items.Armor.Leggings";
                        case 3:
                            return "Items.Armor.Boots";
                    }
                }
                else if(item == Item.arrow || item == Item.bow)
                {
                    return "Items.Weapons.Ranged";
                }
                else if(item == Item.fishingRod || item == Item.flintAndSteel || item == Item.shears)
                {
                    return "Items.Tools.Other";
                }
            }

            if(item instanceof ItemFood)
            {
                return "Items.Food";
            }

            if (item instanceof ItemPotion)
            {
                return "Items.Potions";
            }

            return "Items.Other";
        }
    }
}
