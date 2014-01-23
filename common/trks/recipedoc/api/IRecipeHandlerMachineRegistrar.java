package trks.recipedoc.api;

public interface IRecipeHandlerMachineRegistrar
{
    /**
     *
     * @param craftingHandler class extending IRecipeHandler
     * @param itemId
     * @param damageId
     */
    public void registerRecipeHandlerMachine(Class craftingHandler, int itemId, int damageId);
}
