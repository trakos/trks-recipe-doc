package trks.recipedoc.modsupport.mods;

import codechicken.nei.recipe.ICraftingHandler;
import com.google.common.collect.ImmutableMap;
import mekanism.api.gas.IGasItem;
import mekanism.common.IFactory;
import mekanism.common.Mekanism;
import mekanism.common.block.BlockMachine;
import mekanism.common.item.*;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;
import trks.recipedoc.modsupport.API;
import trks.recipedoc.modsupport.IDocModSupport;
import universalelectricity.core.item.IItemElectric;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class MekanismSupport implements IDocModSupport
{

    static final String CATEGORY_MACHINES = "Machines";
    static final String CATEGORY_DUSTS_CLAMPS = "Dusts & Clamps";
    static final String CATEGORY_INGOTS = "Ingots";
    static final String CATEGORY_ORES = "Ores";
    static final String CATEGORY_INDUCTION = "Induction";
    static final String CATEGORY_GENERATORS = "Generators";
    static final String CATEGORY_UPGRADES = "Machine Upgrades";
    static final String CATEGORY_E_TOOLS = "Energized Tools";
    static final String CATEGORY_E_CUBE = "Energy Cubes";
    private static final String CATEGORY_FACTORIES = "Factory Machines";
    private static final String CATEGORY_PIPES_CABLES = "Pipes & Cables";

    @Override
    public Boolean shouldCorrectItemFromMod(String modId)
    {
        return modId.startsWith("Mekanism");
    }

    @Override
    public void correctItemStruct(ItemStruct itemStruct)
    {
        ItemStack itemStack = itemStruct.getSourceItemStack();

        fixItemCategory(itemStack, itemStruct);
        if (itemStruct.mod.equals("MekanismGenerators"))
        {
            itemStruct.mod = "Mekanism";
            itemStruct.category = itemStruct.category.equals(API.STANDARD_CATEGORY_BLOCKS)
                    ? CATEGORY_GENERATORS
                    : API.STANDARD_CATEGORY_OTHER;
        }
        if (itemStruct.mod.equals("MekanismInduction"))
        {
            itemStruct.mod = "Mekanism";
            itemStruct.category = itemStruct.category.equals(API.STANDARD_CATEGORY_BLOCKS)
                    ? CATEGORY_INDUCTION
                    : API.STANDARD_CATEGORY_OTHER;
        }
        addAttributes(itemStack, itemStruct);
        fixMachinesNameAndDescription(itemStruct);
        if (itemStack.getItem() instanceof ItemEnergized && itemStack.getItemDamage() == 100)
        {
            itemStruct.showOnList = false;
        }
        itemStruct.damage = getOverwrittenItemDamage(itemStack);
    }

    @Override
    public Collection<String> getModsRequiredToBeLoaded()
    {
        return Arrays.asList("Mekanism");
    }

    @Override
    public Map<String, Float> getNewCategories()
    {
        return (new ImmutableMap.Builder<String, Float>())
                .put(CATEGORY_MACHINES, .51f)
                .put(CATEGORY_INDUCTION, .52f)
                .put(CATEGORY_GENERATORS, .53f)
                .put(CATEGORY_E_CUBE, .535f)
                .put(CATEGORY_FACTORIES, .535f)
                .put(CATEGORY_E_TOOLS, .54f)
                .put(CATEGORY_UPGRADES, .55f)
                .put(CATEGORY_PIPES_CABLES, .555f)
                .put(CATEGORY_DUSTS_CLAMPS, .56f)
                .put(CATEGORY_INGOTS, .57f)
                .put(CATEGORY_ORES, .58f)
                .build();
    }

    @Override
    public void correctRecipeItemStruct(RecipeItemStruct.RecipeItemIdStruct recipeItemStruct)
    {
        recipeItemStruct.damageId = getOverwrittenItemDamage(recipeItemStruct.getItemStack());
    }

    protected HashSet<String> foundElectricRecipeHashes = new HashSet<String>();
    @Override
    public void correctRecipeStruct(RecipeStruct recipeStruct, ICraftingHandler handler)
    {
        if (recipeStruct.containsItemOfClass(IItemElectric.class))
        {
            String recipeHash = recipeStruct.generateRecipeHash();
            if (foundElectricRecipeHashes.contains(recipeHash))
            {
                recipeStruct.visible = false;
            }
            else
            {
                foundElectricRecipeHashes.add(recipeHash);
            }
        }
    }

    protected String getMachineRecipeTypeName(ItemStack itemStack)
    {
        if (!(itemStack.getItem() instanceof ItemBlockMachine))
        {
            throw new RuntimeException("!");
        }
        return IFactory.RecipeType.values()[((ItemBlockMachine) itemStack.getItem()).getRecipeType(itemStack)].getName();
    }

    protected Boolean isTypeFactory(BlockMachine.MachineType machineType)
    {
        return machineType == BlockMachine.MachineType.BASIC_FACTORY || machineType == BlockMachine.MachineType.ADVANCED_FACTORY || machineType == BlockMachine.MachineType.ELITE_FACTORY;
    }

    protected void fixMachinesNameAndDescription(ItemStruct itemStruct)
    {
        ItemStack itemStack = itemStruct.getSourceItemStack();
        if (itemStruct.getSourceItemStack().getItem() instanceof ItemBlockMachine)
        {
            BlockMachine.MachineType type = BlockMachine.MachineType.get(itemStack);
            if (isTypeFactory(type))
            {
                itemStruct.name += " (" + getMachineRecipeTypeName(itemStack) + ")";
                itemStruct.attributes.put("Machine type", getMachineRecipeTypeName(itemStack));
            }
            itemStruct.description = type.getDescription().replaceAll("!n", "");
        }
    }

    protected int getOverwrittenItemDamage(ItemStack itemStack)
    {
        Item item = itemStack.getItem();
        if (item instanceof IItemElectric)
        {
            if (item instanceof ItemBlockEnergyCube)
            {
                switch (((ItemBlockEnergyCube) item).getEnergyCubeTier(itemStack))
                {
                    case BASIC:
                        return 0;
                    case ADVANCED:
                        return 1;
                    case ELITE:
                        return 2;
                    case ULTIMATE:
                        return 3;
                    default:
                        return 100;
                }
            }
            else if (item instanceof ItemEnergized)
            {
                return 0;
            }
            else if (item instanceof ItemBlockMachine)
            {
                if (isTypeFactory(BlockMachine.MachineType.get(itemStack)))
                {
                    int recipeType = ((ItemBlockMachine) item).getRecipeType(itemStack);
                    return 1000 + (itemStack.getItemDamage() * 100) + recipeType;
                }
            }
        }
        else if (item instanceof IGasItem)
        {
            return 0;
        }
        return itemStack.getItemDamage();
    }

    protected void fixItemCategory(ItemStack itemStack, ItemStruct itemStruct)
    {
        Item item = itemStack.getItem();
        if (item instanceof ItemBlockTransmitter)
        {
            itemStruct.category = CATEGORY_PIPES_CABLES;
        }
        else if (item instanceof ItemBlockEnergyCube)
        {
            itemStruct.category = CATEGORY_E_CUBE;
        }
        else if (item instanceof ItemBlockMachine)
        {
            BlockMachine.MachineType type = BlockMachine.MachineType.get(itemStack);
            if (type == BlockMachine.MachineType.BASIC_FACTORY || type == BlockMachine.MachineType.ADVANCED_FACTORY || type == BlockMachine.MachineType.ELITE_FACTORY)
            {
                itemStruct.category = CATEGORY_FACTORIES;
            }
            else
            {
                itemStruct.category = CATEGORY_MACHINES;
            }
        }
        else if (item instanceof ItemDust || item instanceof ItemClump || item instanceof ItemDirtyDust)
        {
            itemStruct.category = CATEGORY_DUSTS_CLAMPS;
        }
        else if (item instanceof ItemIngot)
        {
            itemStruct.category = CATEGORY_INGOTS;
        }
        else if (item instanceof ItemBlockOre)
        {
            itemStruct.category = CATEGORY_ORES;
        }
        else if (item instanceof ItemMachineUpgrade)
        {
            itemStruct.category = CATEGORY_UPGRADES;
        }
        else if (item instanceof ItemEnergized)
        {
            itemStruct.category = CATEGORY_E_TOOLS;
        }
    }

    protected void addAttributes(ItemStack itemStack, ItemStruct itemStruct)
    {
        if (itemStack.getItem() instanceof ItemBlockTransmitter)
        {
            if(itemStack.getItemDamage() == 0)
            {
                itemStruct.attributes.put("Transfers", "Oxygen, Hydrogen");
            }
            else if(itemStack.getItemDamage() == 1)
            {
                itemStruct.attributes.put("Transfers", "RF (ThermalExpansion), EU (IndustrialCraft), MJ (BuildCraft), Joules (Mekanism, UE)");
            }
            else if(itemStack.getItemDamage() == 2)
            {
                itemStruct.attributes.put("Transfers", "mB (FluidRegistry)");
            }
            else if(itemStack.getItemDamage() == 3)
            {
                itemStruct.attributes.put("Transfers", "Items, Blocks");
            }
            else if(itemStack.getItemDamage() == 4)
            {
                itemStruct.attributes.put("Transfers", "Items, Blocks");
                itemStruct.description += "Only used if no other paths available";
            }
            else if(itemStack.getItemDamage() == 5)
            {
                itemStruct.attributes.put("Transfers", "Items, Blocks");
                itemStruct.description += "Controllable by redstone";
            }
        }
        if (itemStack.getItem() instanceof IItemElectric)
        {
            IItemElectric item = (IItemElectric) itemStack.getItem();
            itemStruct.attributes.put("Battery capacity", MekanismUtils.getEnergyDisplay(item.getMaxElectricityStored(itemStack) * Mekanism.FROM_UE));
            itemStruct.attributes.put("Voltage", item.getVoltage(itemStack) + " v");
        }
    }
}
