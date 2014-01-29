package trks.recipedoc;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import trks.recipedoc.client.TickHandler;

@Mod(modid = "TrksRecipeDocId", name = "TrksRecipeDoc", version = "0.0.1", dependencies = "after:Mekanism")
@NetworkMod(clientSideRequired = true)
public class RecipeDoc
{

    @Instance(value = "TrksRecipeDocId")
    public static RecipeDoc instance;

    @SidedProxy(clientSide = "trks.recipedoc.client.ClientProxy", serverSide = "trks.recipedoc.CommonProxy")
    public static CommonProxy proxy;

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
    }

    @Mod.Init
    public void load(FMLInitializationEvent event)
    {
        TickRegistry.registerTickHandler(tickHandler, Side.CLIENT);
    }

    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent event)
    {
    }


    protected TickHandler tickHandler = new TickHandler();
}
