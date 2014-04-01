package trks.recipedoc.modsupport.mods;

import codechicken.nei.ItemMobSpawner;
import codechicken.nei.recipe.FireworkRecipeHandler;
import codechicken.nei.recipe.ICraftingHandler;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;
import trks.recipedoc.api.API;
import trks.recipedoc.api.IDocModSupport;
import trks.recipedoc.api.IRecipeHandlerMachineRegistrar;
import trks.recipedoc.generate.structs.IdDamagePairWithStack;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;

import java.util.Collection;
import java.util.Map;

public class VanillaSupport implements IDocModSupport
{

    @Override
    public Boolean shouldCorrectItemFromMod(String modId)
    {
        return modId.toLowerCase().equals("minecraft");
    }

    @Override
    public void correctItemStruct(ItemStruct itemStruct, IRecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar)
    {
        if (itemStruct.category.equals(API.STANDARD_CATEGORY_INTERNAL))
        {
            itemStruct.showOnList = false;
        }
        if (isPlantBlock(itemStruct) || isPlantDrop(itemStruct) || isPlantRawDye(itemStruct))
        {
            itemStruct.category = API.STANDARD_CATEGORY_PLANTS;
        }
        if (isOre(itemStruct))
        {
            itemStruct.category = API.STANDARD_CATEGORY_ORE;
        }
        if (isIngot(itemStruct))
        {
            itemStruct.craftingComplexity = .01f;
        }
        else
        {
            itemStruct.craftingComplexity = 1f;
        }

        if (itemStruct.itemId == Item.bootsChain.itemID || itemStruct.itemId == Item.helmetChain.itemID
                || itemStruct.itemId == Item.legsChain.itemID || itemStruct.itemId == Item.plateChain.itemID)
        {
            itemStruct.showOnList = false;
        }
    }

    @Override
    public boolean isBaseItem(ItemStruct itemStruct)
    {
        return isOre(itemStruct)
               //|| isIngot(itemStruct)
               || isRawFood(itemStruct)
               || isBaseBlock(itemStruct)
               || isPlantBlock(itemStruct)
               || isWool(itemStruct)
               || isWood(itemStruct)
               || isBreakableBlockDrop(itemStruct)
               || isBucket(itemStruct)
               || isMobDrop(itemStruct)
               || isExclusiveDungeonLoot(itemStruct)
               || isSpawner(itemStruct)
               || isPlantRawDye(itemStruct)
               || isNonPlantRawDye(itemStruct)
               || isPlantDrop(itemStruct)
               || isHeadOrSkull(itemStruct)
               || itemStruct.rawName.equals("item.paper") // too many options to craft, just ignore them
               || itemStruct.category.equals(API.STANDARD_CATEGORY_POTIONS); // not worth fixing it, so just ignore
    }

    @Override
    public Collection<String> getModsRequiredToBeLoaded()
    {
        return null;
    }

    @Override
    public Map<String, Float> getNewCategories()
    {
        return (new ImmutableMap.Builder<String, Float>())
                .put(API.STANDARD_CATEGORY_ITEMS, 10f)
                .put(API.STANDARD_CATEGORY_ORE, 15f)
                .put(API.STANDARD_CATEGORY_WEAPONS, 20f)
                .put(API.STANDARD_CATEGORY_ARMOR, 30f)
                .put(API.STANDARD_CATEGORY_FOOD, 40f)
                .put(API.STANDARD_CATEGORY_WOOD, 45f)
                .put(API.STANDARD_CATEGORY_PLANTS, 46f)
                .put(API.STANDARD_CATEGORY_BLOCKS, 50f)
                .put(API.STANDARD_CATEGORY_POTIONS, 60f)
                .put(API.STANDARD_CATEGORY_OTHER, 100f)
                .build();
    }

    @Override
    public void correctRecipeStruct(RecipeStruct recipeStruct, ICraftingHandler handler)
    {
        if (handler instanceof FireworkRecipeHandler)
        {
            recipeStruct.useInRawCostCalculation = false;
        }
    }

    @Override
    public void correctRecipeItemStruct(IdDamagePairWithStack recipeItemStruct)
    {

    }

    protected boolean isOre(ItemStruct itemStruct)
    {
        if (!(itemStruct.getItemStack().getItem() instanceof ItemBlock))
        {
            return false;
        }
        int itemId = ((ItemBlock) itemStruct.getItemStack().getItem()).getBlockID();
        return (
                itemId == Block.oreCoal.blockID
                || itemId == Block.oreDiamond.blockID
                || itemId == Block.oreEmerald.blockID
                || itemId == Block.oreGold.blockID || itemId == Block.oreIron.blockID
                || itemId == Block.oreLapis.blockID || itemId == Block.oreNetherQuartz.blockID
                || itemId == Block.oreRedstone.blockID
                || itemStruct.rawName.equals("item.netherquartz")
        );
    }

    protected boolean isIngot(ItemStruct itemStruct)
    {
        int blockId = itemStruct.getItemStack().itemID;
        return
                blockId == Item.ingotGold.itemID
                || blockId == Item.ingotIron.itemID
        ;
    }

    protected boolean isWool(ItemStruct itemStruct)
    {
        return itemStruct.getItemStack().getItem() instanceof ItemCloth;
    }

    protected boolean isRawFood(ItemStruct itemStruct)
    {
        if (!(itemStruct.getItemStack().getItem() instanceof ItemFood))
        {
            return false;
        }
        int blockId = itemStruct.getItemStack().itemID;
        return !(
                blockId == Item.bread.itemID
                || blockId == Item.goldenCarrot.itemID
                || blockId == Item.appleGold.itemID
                || blockId == Item.porkCooked.itemID
                || blockId == Item.bakedPotato.itemID
                || blockId == Item.pumpkinPie.itemID
                || blockId == Item.bowlSoup.itemID
        );
    }

    protected boolean isBaseBlock(ItemStruct itemStruct)
    {
        if (!(itemStruct.getItemStack().getItem() instanceof ItemBlock))
        {
            return false;
        }
        int blockId = ((ItemBlock) itemStruct.getItemStack().getItem()).getBlockID();
        return blockId == Block.dirt.blockID
               || blockId == Block.grass.blockID
               || blockId == Block.tilledField.blockID
               || blockId == Block.cobblestone.blockID
               || blockId == Block.gravel.blockID
               || blockId == Block.sandStone.blockID
               || blockId == Block.sponge.blockID
               || blockId == Block.netherrack.blockID
               || blockId == Block.glowStone.blockID
               || blockId == Block.bedrock.blockID
               || blockId == Block.ice.blockID
               || blockId == Block.web.blockID
               || blockId == Block.mycelium.blockID
               || blockId == Block.obsidian.blockID
               || blockId == Block.sand.blockID
               || blockId == Block.slowSand.blockID
               || blockId == Block.ice.blockID;
    }

    protected boolean isPlantBlock(ItemStruct itemStruct)
    {
        if (!(itemStruct.getItemStack().getItem() instanceof ItemBlock))
        {
            return false;
        }
        ItemBlock block = ((ItemBlock) itemStruct.getItemStack().getItem());
        int blockId = block.getBlockID();
        return blockId == Block.cactus.blockID
               || blockId == Block.deadBush.blockID
               || blockId == Block.netherStalk.blockID
               || blockId == Block.melon.blockID
               || blockId == Block.pumpkin.blockID
               || blockId == Block.sapling.blockID
               || blockId == Block.mushroomBrown.blockID
               || blockId == Block.mushroomCapBrown.blockID
               || blockId == Block.mushroomCapRed.blockID
               || blockId == Block.mushroomRed.blockID
               || blockId == Block.grass.blockID
               || blockId == Block.cocoaPlant.blockID
               || blockId == Block.plantRed.blockID
               || blockId == Block.plantYellow.blockID
               || blockId == Block.vine.blockID
               || blockId == Block.waterlily.blockID
               || blockId == Block.leaves.blockID;
    }

    protected boolean isWood(ItemStruct itemStruct)
    {
        if (!(itemStruct.getItemStack().getItem() instanceof ItemBlock))
        {
            return false;
        }
        int blockId = ((ItemBlock) itemStruct.getItemStack().getItem()).getBlockID();
        return blockId == Block.wood.blockID || blockId == Block.planks.blockID;
    }

    protected boolean isBreakableBlockDrop(ItemStruct itemStruct)
    {
        int itemId = itemStruct.getItemStack().itemID;
        return itemId == Item.clay.itemID
               || itemId == Item.coal.itemID
               || itemId == Item.diamond.itemID
               || itemId == Item.emerald.itemID
               || itemId == Item.redstone.itemID
               || itemId == Item.flint.itemID
               || itemId == Item.snowball.itemID
               || itemId == Item.glowstone.itemID;
    }

    protected boolean isBucket(ItemStruct itemStruct)
    {
        Item item = itemStruct.getItemStack().getItem();
        return item instanceof ItemBucket || item instanceof ItemBucketMilk;
    }

    protected boolean isMobDrop(ItemStruct itemStruct)
    {
        int itemId = itemStruct.getItemStack().itemID;
        return itemId == Item.silk.itemID
               || itemId == Item.feather.itemID
               || itemId == Item.gunpowder.itemID
               || itemId == Item.leather.itemID
               || itemId == Item.slimeBall.itemID
               || itemId == Item.egg.itemID
               || (itemId == Item.dyePowder.itemID && itemStruct.damageId == 0)
               || itemId == Item.bone.itemID
               || itemId == Item.enderPearl.itemID
               || itemId == Item.blazeRod.itemID
               || itemId == Item.spiderEye.itemID
               || itemId == Item.ghastTear.itemID
               || itemId == Item.netherStar.itemID;
    }

    protected boolean isPlantRawDye(ItemStruct itemStruct)
    {
        if (itemStruct.getItemStack().itemID == Item.dyePowder.itemID)
        {

            String colorName = ItemDye.dyeColorNames[MathHelper.clamp_int(itemStruct.getItemStack().getItemDamage(), 0, 15)];
            return colorName.equals("red") // rose
                   || colorName.equals("yellow") // dandellion
                   || colorName.equals("brown"); // cocoa beans
        }
        return false;

    }


    protected boolean isNonPlantRawDye(ItemStruct itemStruct)
    {
        if (itemStruct.getItemStack().itemID == Item.dyePowder.itemID)
        {
            String colorName = ItemDye.dyeColorNames[MathHelper.clamp_int(itemStruct.getItemStack().getItemDamage(), 0, 15)];
            return colorName.equals("black") // squid
                   || colorName.equals("blue"); // lapis lazuli
        }
        return false;

    }

    protected boolean isHeadOrSkull(ItemStruct itemStruct)
    {
        return itemStruct.getItemStack().getItem() instanceof ItemSkull;
    }

    protected boolean isExclusiveDungeonLoot(ItemStruct itemStruct)
    {
        int itemId = itemStruct.getItemStack().itemID;
        return itemId == Item.saddle.itemID
               || itemId == Item.horseArmorDiamond.itemID
               || itemId == Item.horseArmorGold.itemID
               || itemId == Item.horseArmorIron.itemID
               || itemId == Item.expBottle.itemID
               || itemId == Item.enchantedBook.itemID
               || itemStruct.getItemStack().getItem() instanceof ItemRecord;
    }

    protected boolean isSpawner(ItemStruct itemStruct)
    {
        return itemStruct.getItemStack().getItem() instanceof ItemMobSpawner
               || itemStruct.getItemStack().getItem() instanceof ItemMonsterPlacer;
    }

    protected boolean isPlantDrop(ItemStruct itemStruct)
    {
        Item item = itemStruct.getItemStack().getItem();
        int itemId = itemStruct.getItemStack().itemID;
        return item instanceof ItemLeaves
               || itemId == Item.seeds.itemID
               || itemId == Item.reed.itemID
               || itemId == Item.netherStalkSeeds.itemID
               || itemId == Item.wheat.itemID;
    }
}
