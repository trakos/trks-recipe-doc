package trks.recipedoc.client;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.Minecraft;
import net.minecraft.stats.StatList;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.FlatGeneratorInfo;
import trks.recipedoc.generate.Generate;
import trks.recipedoc.minecraft.RendererHelper;

import java.util.EnumSet;

public class TickHandler implements ITickHandler
{
    boolean gameStarted = false;
    int tickNumber = 0;

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        if (!gameStarted)
        {
            RendererHelper.resizeWindow(512, 512, false);

            gameStarted = true;
            long seed = 1;
            WorldSettings worldsettings = new WorldSettings(seed, EnumGameType.SURVIVAL, false, false, WorldType.FLAT);
            worldsettings.func_82750_a(FlatGeneratorInfo.getDefaultFlatGenerator().toString());

            Minecraft.getMinecraft().launchIntegratedServer("trksRecipeDoc", "trksRecipeDoc", worldsettings);
            Minecraft.getMinecraft().statFileWriter.readStat(StatList.createWorldStat, 1);

        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if(type.contains(TickType.CLIENT) && mc.theWorld != null)
        {
            tickNumber++;
        }
        // for now it is 1, it seems to work even in the first frame
        if (tickNumber == 10)
        {
            Generate.generate();
            Minecraft.getMinecraft().shutdown();
        }
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }

    @Override
    public String getLabel()
    {
        return null;
    }
}
