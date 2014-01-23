package trks.recipedoc.modsupport;

import trks.recipedoc.api.IRecipeHandlerMachineRegistrar;
import trks.recipedoc.generate.loaders.DataLoader;
import trks.recipedoc.generate.structs.RecipeTypeStruct;

public class RecipeHandlerMachineRegistrar implements IRecipeHandlerMachineRegistrar
{
    @Override
    public void registerRecipeHandlerMachine(Class craftingHandler, int itemId, int damageId)
    {
        for (RecipeTypeStruct recipeHandler : DataLoader.getRecipeHandlers())
        {
            if (craftingHandler.isInstance(recipeHandler.getCraftingHandler()))
            {
                recipeHandler.registerMachineHandling(itemId, damageId);
                break;
            }
        }

    }
}
