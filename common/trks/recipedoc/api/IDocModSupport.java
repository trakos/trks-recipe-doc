package trks.recipedoc.api;
import codechicken.nei.recipe.ICraftingHandler;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;

import java.util.Collection;
import java.util.Map;

public interface IDocModSupport
{
    /**
     * @param modId mod id
     * @return Whether items from this mod should be processed by this class
     */
    public Boolean shouldCorrectItemFromMod(String modId);

    /**
     *
     * @param itemStruct structure filled with standard data (and, maybe already corrected by previous IDocModSupports)
     *                   which this method is expected to enhance, for instance by assigning more fitting category
     *                   and adding new attributes;
     *                   don't forget to register all added categories through getNewCategorie smethod
     */
    public void correctItemStruct(ItemStruct itemStruct, IRecipeHandlerMachineRegistrar recipeHandlerMachineRegistrar);

    /**
     * @return List of mods which have to be loaded - otherwise, this class won't be used
     */
    public Collection<String> getModsRequiredToBeLoaded();

    /**
     * Contrary to its name, you can also overwrite already existing categories' priorities
     *
     * @return List of categories (keys) with their priorities as values (standard categories range is 10f-100f)
     */
    public Map<String, Float> getNewCategories();

    /**
     * Called for every recipe which has at least one ingredient with mod that returns true when given to
     * shouldCorrectItemFromMod
     * Note: called after correctRecipeItemStruct for every item
     *
     * @param recipeStruct recipeStruct to correct
     * @param handler craftingHandler with this recipe
     */
    void correctRecipeStruct(RecipeStruct recipeStruct, ICraftingHandler handler);

    /**
     * Called for items from mods that return true when given to shouldCorrectItemFromMod
     * Note: called before correctRecipeStruct
     *
     * @param recipeItemStruct recipeItemStruct to correct
     *
     */
    void correctRecipeItemStruct(RecipeItemStruct.RecipeItemIdStruct recipeItemStruct);
}
