package trks.recipedoc.modsupport.mods;

import codechicken.nei.recipe.FurnaceRecipeHandler;
import codechicken.nei.recipe.ICraftingHandler;
import com.google.common.collect.ImmutableMap;
import mekanism.api.gas.IGasItem;
import mekanism.client.nei.*;
import mekanism.common.IFactory;
import mekanism.common.Mekanism;
import mekanism.common.block.BlockMachine;
import mekanism.common.item.*;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.MekanismGenerators;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import trks.recipedoc.api.API;
import trks.recipedoc.api.IDocModSupport;
import trks.recipedoc.api.IRecipeHandlerMachineRegistrar;
import trks.recipedoc.generate.structs.IdDamagePairWithStack;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;
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
    public void correctItemStruct(ItemStruct itemStruct, IRecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar)
    {
        ItemStack itemStack = itemStruct.getItemStack();

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
        if ((itemStack.getItem() instanceof ItemEnergized || itemStack.getItem() instanceof ItemBlockEnergyCube) && itemStack.getItemDamage() == 100)
        {
            itemStruct.showOnList = false;
            itemStruct.damageId = -1;
        }
        else
        {
            itemStruct.damageId = getOverwrittenItemDamage(itemStack);
        }

        RecipeTypes machineRecipeType = getMachineRecipeType(itemStack);
        if (machineRecipeType != null)
        {
            recipeHandlerMachineRegistrar.registerRecipeHandlerMachine(machineRecipeType.getCraftingHandler(), itemStruct.itemId, itemStruct.damageId);
            itemStruct.attributes.put("Machine action", machineRecipeType.getDescription());
        }

        fixItemComplexity(itemStruct);
    }

    private void fixItemComplexity(ItemStruct itemStruct)
    {
        if (itemStruct.category.equals(CATEGORY_MACHINES))
        {

            if(BlockMachine.MachineType.get(itemStruct.getItemStack()) == BlockMachine.MachineType.DIGITAL_MINER)
            {
                itemStruct.craftingComplexity = 250f;
            }
            else
            {
                itemStruct.craftingComplexity = 100f;
            }
        }
        else if (itemStruct.category.equals(CATEGORY_INDUCTION))
        {
            itemStruct.craftingComplexity = 100f;
        }
        else if (itemStruct.category.equals(CATEGORY_GENERATORS))
        {
            itemStruct.craftingComplexity = 100f;
        }
        else if (itemStruct.category.equals(CATEGORY_DUSTS_CLAMPS))
        {
            itemStruct.craftingComplexity = 1f;
        }
        else if (itemStruct.category.equals(CATEGORY_INGOTS))
        {
            itemStruct.craftingComplexity = 1f;
        }
        else if (itemStruct.category.equals(CATEGORY_UPGRADES))
        {
            itemStruct.craftingComplexity = 40f;
        }
        else if (itemStruct.category.equals(CATEGORY_E_TOOLS))
        {
            if (itemStruct.getItemStack().getItem() instanceof ItemRobit)
            {
                itemStruct.craftingComplexity = 200f;
            }
            else
            {
                itemStruct.craftingComplexity = 50f;
            }
        }
        else if (itemStruct.category.equals(CATEGORY_E_CUBE))
        {
            // damageId has tier filled in already
            itemStruct.craftingComplexity = 200f + itemStruct.damageId;
        }
        else if (itemStruct.category.equals(CATEGORY_FACTORIES))
        {
            // damageId is raising with tiers
            itemStruct.craftingComplexity = 200f + itemStruct.damageId / 1000;
        }
        else if (itemStruct.category.equals(CATEGORY_PIPES_CABLES))
        {
            itemStruct.craftingComplexity = 50f;
        }
        else if (itemStruct.category.equals(API.STANDARD_CATEGORY_ORE))
        {
            itemStruct.craftingComplexity = .5f;
        }
        else
        {
            itemStruct.craftingComplexity = 10f;
        }
    }

    @Override
    public boolean isBaseItem(ItemStruct itemStruct)
    {
        return
                itemStruct.itemId == Item.itemsList[Mekanism.oreBlockID].itemID;
                //|| itemStruct.getItemStack().getItem() instanceof ItemIngot;
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
                .build();
    }

    @Override
    public void correctRecipeItemStruct(IdDamagePairWithStack recipeItemStruct)
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
                recipeStruct.useInRawCostCalculation = false;
            }
            else
            {
                foundElectricRecipeHashes.add(recipeHash);
            }
        }
        if (handler instanceof EnrichmentChamberRecipeHandler)
        {
            boolean hasCharcoal = false;
            for (RecipeItemStruct item : recipeStruct.items)
            {
                for (IdDamagePairWithStack itemId : item.itemIds)
                {
                    if (itemId.itemId == Mekanism.Clump.itemID)
                    {
                        recipeStruct.recipeMethod = RecipeStruct.RecipeMethod.METHOD_A;
                    }
                    else if (itemId.itemId == Mekanism.oreBlockID
                             || itemId.itemId == Block.oreIron.blockID
                             || itemId.itemId == Block.oreGold.blockID
                             || itemId.itemId == Block.oreEmerald.blockID
                             || itemId.itemId == Block.oreCoal.blockID)
                    {
                        recipeStruct.recipeMethod = RecipeStruct.RecipeMethod.METHOD_B;
                    }
                    if (itemId.itemId == Item.coal.itemID && itemId.damageId != 0)
                    {
                        hasCharcoal = true;
                        break;
                    }
                }
                if (hasCharcoal) break;
            }
            recipeStruct.useInRawCostCalculation = !hasCharcoal;
        }
        else if (handler instanceof CrusherRecipeHandler)
        {
            boolean ignoreRecipeInCalculation = true;
            for (RecipeItemStruct item : recipeStruct.items)
            {
                for (IdDamagePairWithStack itemId : item.itemIds)
                {
                    if (itemId.itemId == Mekanism.Clump.itemID)
                    {
                        recipeStruct.recipeMethod = RecipeStruct.RecipeMethod.METHOD_A;
                    }
                    if (itemId.itemId == Item.diamond.itemID
                        || itemId.itemId == Mekanism.Clump.itemID
                        || itemId.itemId == MekanismGenerators.BioFuel.itemID)
                    {
                        ignoreRecipeInCalculation = false;
                        break;
                    }
                }
                if (!ignoreRecipeInCalculation) break;
            }
            recipeStruct.useInRawCostCalculation = !ignoreRecipeInCalculation;
        }
    }

    protected IFactory.RecipeType getFactoryRecipeType(ItemStack itemStack)
    {
        if (!isTypeFactory(itemStack))
        {
            throw new RuntimeException("!");
        }
        return IFactory.RecipeType.values()[((ItemBlockMachine) itemStack.getItem()).getRecipeType(itemStack)];
    }

    protected Boolean isTypeFactory(ItemStack itemStack)
    {
        return itemStack.getItem() instanceof ItemBlockMachine && isTypeFactory(BlockMachine.MachineType.get(itemStack));
    }

    protected Boolean isTypeFactory(BlockMachine.MachineType machineType)
    {
        return machineType == BlockMachine.MachineType.BASIC_FACTORY || machineType == BlockMachine.MachineType.ADVANCED_FACTORY || machineType == BlockMachine.MachineType.ELITE_FACTORY;
    }

    protected enum RecipeTypes
    {
        ENRICHING(EnrichmentChamberRecipeHandler.class, "enriching"),
        COMPRESSING(OsmiumCompressorRecipeHandler.class, "compressing"),
        SMELTING(FurnaceRecipeHandler.class, "smelting"),
        COMBINING(CombinerRecipeHandler.class, "combining"),
        CRUSHING(CrusherRecipeHandler.class, "crushing"),
        PURIFYING(PurificationChamberRecipeHandler.class, "purifying"),
        INFUSING(MetallurgicInfuserRecipeHandler.class, "infusing");

        Class craftingHandler;
        String description;

        RecipeTypes(Class craftingHandler, String description)
        {
            this.craftingHandler = craftingHandler;
            this.description = description;
        }

        public Class getCraftingHandler()
        {
            return craftingHandler;
        }

        public String getDescription()
        {
            return description;
        }
    }

    protected RecipeTypes getMachineRecipeType(ItemStack itemStack)
    {
        if (itemStack.getItem() instanceof ItemBlockMachine)
        {
            if (isTypeFactory(itemStack))
            {
                switch (getFactoryRecipeType(itemStack))
                {
                    case SMELTING:
                        return RecipeTypes.SMELTING;
                    case ENRICHING:
                        return RecipeTypes.ENRICHING;
                    case CRUSHING:
                        return RecipeTypes.CRUSHING;
                    case COMPRESSING:
                        return RecipeTypes.COMPRESSING;
                    case COMBINING:
                        return RecipeTypes.COMBINING;
                    case PURIFYING:
                        return RecipeTypes.PURIFYING;
                }
            }
            else
            {
                switch (BlockMachine.MachineType.get(itemStack))
                {
                    case ENRICHMENT_CHAMBER:
                        return RecipeTypes.ENRICHING;
                    case OSMIUM_COMPRESSOR:
                        return RecipeTypes.COMPRESSING;
                    case COMBINER:
                        return RecipeTypes.COMBINING;
                    case CRUSHER:
                        return RecipeTypes.CRUSHING;
                    case METALLURGIC_INFUSER:
                        return RecipeTypes.INFUSING;
                    case PURIFICATION_CHAMBER:
                        return RecipeTypes.PURIFYING;
                    case ENERGIZED_SMELTER:
                        return RecipeTypes.SMELTING;
                }
            }
        }
        return null;
    }

    protected void fixMachinesNameAndDescription(ItemStruct itemStruct)
    {
        ItemStack itemStack = itemStruct.getItemStack();
        if (itemStruct.getItemStack().getItem() instanceof ItemBlockMachine)
        {
            BlockMachine.MachineType type = BlockMachine.MachineType.get(itemStack);
            if (isTypeFactory(type))
            {
                itemStruct.name += " (" + getFactoryRecipeType(itemStack).getName() + ")";
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
            itemStruct.category = API.STANDARD_CATEGORY_ORE;
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
        if (itemStruct.description == null)
        {
            itemStruct.description = "";
        }
        if (itemStack.getItem() instanceof ItemBlockTransmitter)
        {
            if (itemStack.getItemDamage() == 0)
            {
                itemStruct.attributes.put("Transfers", "Oxygen, Hydrogen");
            }
            else if (itemStack.getItemDamage() == 1)
            {
                itemStruct.attributes.put("Transfers", "RF (ThermalExpansion), EU (IndustrialCraft), MJ (BuildCraft), Joules (Mekanism, UE)");
            }
            else if (itemStack.getItemDamage() == 2)
            {
                itemStruct.attributes.put("Transfers", "mB (FluidRegistry)");
            }
            else if (itemStack.getItemDamage() == 3)
            {
                itemStruct.attributes.put("Transfers", "Items, Blocks");
            }
            else if (itemStack.getItemDamage() == 4)
            {
                itemStruct.attributes.put("Transfers", "Items, Blocks");
                itemStruct.description += "Only used if no other paths available";
            }
            else if (itemStack.getItemDamage() == 5)
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
