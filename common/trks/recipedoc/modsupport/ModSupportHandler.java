package trks.recipedoc.modsupport;

import codechicken.nei.recipe.ICraftingHandler;
import cpw.mods.fml.common.Loader;
import trks.recipedoc.api.API;
import trks.recipedoc.api.IDocModSupport;
import trks.recipedoc.generate.loaders.DataLoader;
import trks.recipedoc.generate.structs.IdDamagePairWithStack;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;
import trks.recipedoc.modsupport.mods.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ModSupportHandler
{
    static
    {
        API.registerModSupport(new AppliedEnergeticsSupport());
        API.registerModSupport(new BuildcraftSupport());
        API.registerModSupport(new CCTurtleSupport());
        API.registerModSupport(new ComputerCraftSupport());
        API.registerModSupport(new DimDoorsSupport());
        API.registerModSupport(new EE3Support());
        API.registerModSupport(new GalactiCraftSupport());
        API.registerModSupport(new LogisticsPipesSupport());
        API.registerModSupport(new MekanismSupport());
        API.registerModSupport(new MineFactoryReloadedSupport());
        API.registerModSupport(new MystcraftSupport());
        API.registerModSupport(new QuantumCraftSupport());
        API.registerModSupport(new ThermalExpansionSupport());
        API.registerModSupport(new VanillaSupport());
    }

    static protected boolean unloadedModsRemoved = false;
    static protected void removeUnloadedModsSupport()
    {
        if (unloadedModsRemoved)
        {
            return;
        }
        Iterator<IDocModSupport> docModSupportIterator = API.docModSupports.iterator();
        while (docModSupportIterator.hasNext())
        {
            IDocModSupport docModSupport = docModSupportIterator.next();
            Collection<String> modsRequiredToBeLoaded = docModSupport.getModsRequiredToBeLoaded();
            if (modsRequiredToBeLoaded == null)
            {
                continue;
            }
            for (String modId : modsRequiredToBeLoaded)
            {
                if (!Loader.isModLoaded(modId))
                {
                    System.out.println("Removing support of (unloaded) " + modId);
                    docModSupportIterator.remove();
                    break;
                }
            }
        }
        unloadedModsRemoved = true;
    }

    static protected RecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar = new RecipeHandlerMachineRegistrar();
    static public ItemStruct correctItemStruct(ItemStruct itemStruct)
    {
        removeUnloadedModsSupport();
        String modOrigin = itemStruct.mod;
        for (IDocModSupport docModSupport : API.docModSupports)
        {
            if (docModSupport.shouldCorrectItemFromMod(modOrigin))
            {
                itemStruct.isBaseItem = itemStruct.isBaseItem || docModSupport.isBaseItem(itemStruct);
                docModSupport.correctItemStruct(itemStruct, recipeHandlerMachineRegistrar);
            }
        }
        return itemStruct;
    }

    public static void fillCategories(HashMap<String, Float> categories)
    {
        for (IDocModSupport docModSupport : API.docModSupports)
        {
            Map<String,Float> newCategories = docModSupport.getNewCategories();
            if (newCategories != null)
            {
                categories.putAll(newCategories);
            }
        }
    }

    public static RecipeStruct correctRecipeStruct(RecipeStruct recipeStruct, ICraftingHandler handler)
    {
        removeUnloadedModsSupport();
        for (IDocModSupport docModSupport : API.docModSupports)
        {
            for (String itemMod : recipeStruct.itemMods)
            {
                if (docModSupport.shouldCorrectItemFromMod(itemMod))
                {
                    docModSupport.correctRecipeStruct(recipeStruct, handler);
                    break;
                }
            }
        }
        return recipeStruct;
    }

    public static IdDamagePairWithStack correctRecipeItemStruct(IdDamagePairWithStack recipeItemStruct)
    {
        removeUnloadedModsSupport();
        for (IDocModSupport docModSupport : API.docModSupports)
        {
            if (docModSupport.shouldCorrectItemFromMod(DataLoader.getItemModId(recipeItemStruct.getItemStack().itemID)))
            {
                docModSupport.correctRecipeItemStruct(recipeItemStruct);
                break;
            }
        }
        return recipeItemStruct;
    }
}
