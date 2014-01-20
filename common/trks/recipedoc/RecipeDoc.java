package trks.recipedoc;

import codechicken.core.inventory.ItemKey;
import codechicken.nei.ItemList;
import codechicken.nei.ItemPanel;
import codechicken.nei.ItemPanelStack;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.api.API;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraftforge.common.MinecraftForge;
import trks.recipedoc.client.DataLoader;
import trks.recipedoc.client.TickHandler;

import java.io.Console;
import java.util.ArrayList;

@Mod(modid = "TrksRecipeDocId", name = "TrksRecipeDoc", version = "0.0.1")
@NetworkMod(clientSideRequired = true)
public class RecipeDoc
{

    @Instance(value = "TrksRecipeDocId")
    public static RecipeDoc instance;

    @SidedProxy(clientSide = "trks.recipedoc.client.ClientProxy", serverSide = "trks.recipedoc.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        TickRegistry.registerTickHandler(tickHandler, Side.CLIENT);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new DataLoader());
    }


    protected TickHandler tickHandler = new TickHandler();
}
