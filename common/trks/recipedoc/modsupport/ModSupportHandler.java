package trks.recipedoc.modsupport;

import codechicken.nei.recipe.ICraftingHandler;
import cpw.mods.fml.common.Loader;
import trks.recipedoc.generate.loaders.DataLoader;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class ModSupportHandler
{

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

    static public ItemStruct correctItemStruct(ItemStruct itemStruct)
    {
        removeUnloadedModsSupport();
        String modOrigin = itemStruct.mod;
        for (IDocModSupport docModSupport : API.docModSupports)
        {
            if (docModSupport.shouldCorrectItemFromMod(modOrigin))
            {
                docModSupport.correctItemStruct(itemStruct);
            }
        }
        return itemStruct;
    }

    public static void fillCategories(HashMap<String, Float> categories)
    {
        for (IDocModSupport docModSupport : API.docModSupports)
        {
            categories.putAll(docModSupport.getNewCategories());
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

    public static RecipeItemStruct.RecipeItemIdStruct correctRecipeItemStruct(RecipeItemStruct.RecipeItemIdStruct recipeItemStruct)
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
