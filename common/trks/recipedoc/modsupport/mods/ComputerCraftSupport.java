package trks.recipedoc.modsupport.mods;

import codechicken.nei.recipe.ICraftingHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagShort;
import trks.recipedoc.api.IDocModSupport;
import trks.recipedoc.api.IRecipeHandlerMachineRegistrar;
import trks.recipedoc.generate.structs.IdDamagePairWithStack;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;

import java.util.*;

/**
 * Created by trakos on 30.01.14.
 */
public class ComputerCraftSupport implements IDocModSupport
{

    @Override
    public Boolean shouldCorrectItemFromMod(String modId)
    {
        return modId.equals("ComputerCraft");
    }

    @Override
    public void correctItemStruct(ItemStruct itemStruct, IRecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar)
    {
        itemStruct.damageId = getOverwrittenItemDamage(itemStruct.getItemStack());
        System.out.println(itemStruct.getItemStack().getDisplayName());
    }

    @Override
    public boolean isBaseItem(ItemStruct itemStruct)
    {
        // its not crafted regularly
        return itemStruct.rawName.equals("item.ccpagesingle");
    }

    @Override
    public Collection<String> getModsRequiredToBeLoaded()
    {
        return Arrays.asList("ComputerCraft");
    }

    @Override
    public Map<String, Float> getNewCategories()
    {
        return null;
    }

    HashSet<String> recipeHashes = new HashSet<String>();
    @Override
    public void correctRecipeStruct(RecipeStruct recipeStruct, ICraftingHandler handler)
    {
        // there are many duplicates for floppies
        String recipeHash = recipeStruct.generateRecipeHash();
        if (recipeHashes.contains(recipeHash))
        {
            recipeStruct.visible = false;
            recipeStruct.useInRawCostCalculation = false;
        }
        else
        {
            recipeHashes.add(recipeHash);
        }
    }

    @Override
    public void correctRecipeItemStruct(IdDamagePairWithStack recipeItemStruct)
    {
        recipeItemStruct.damageId = getOverwrittenItemDamage(recipeItemStruct.getItemStack());
    }

    protected int getFloppyFromIndex(int index)
    {
        int k = 0;
        for (Integer floppy : floppies)
        {
            if (k == index)
            {
                return floppy;
            }
            k++;
        }
        throw new RuntimeException("out of bounds");
    }

    HashSet<Integer> floppies = new HashSet<Integer>();
    protected int getOverwrittenItemDamage(ItemStack itemStack)
    {
        if (floppies.isEmpty()) floppies.add(0);
        if (itemStack.getUnlocalizedName().equals("item.ccdisk"))
        {
            if (itemStack.hasTagCompound())
            {
                for (Object o : itemStack.stackTagCompound.getTags())
                {
                    if (o instanceof net.minecraft.nbt.NBTBase)
                    {
                        if (((NBTBase) o).getName().equals("color"))
                        {
                            if (o instanceof NBTTagShort)
                            {
                                floppies.add((int) ((NBTTagShort) o).data);
                                if (((NBTTagShort) o).data > 100)
                                return ((NBTTagShort) o).data;
                            }
                            else if (o instanceof NBTTagInt)
                            {
                                floppies.add(((NBTTagInt) o).data);
                                if (((NBTTagInt) o).data > 100)
                                return ((NBTTagInt) o).data;
                            }
                        }
                    }
                }
            }
            if (itemStack.getItemDamage() < floppies.size())
            {
                return getFloppyFromIndex(itemStack.getItemDamage());
            }

        }
        return itemStack.getItemDamage();
    }
}
