package trks.recipedoc.client;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.Minecraft;
import net.minecraft.stats.StatList;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.FlatGeneratorInfo;
import trks.recipedoc.generate.DataLoader;
import trks.recipedoc.generate.DataSaver;

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
            DataSaver.init();

            int iconWidth = 512;
            int iconHeight = 512;
            if (Minecraft.getMinecraft().displayWidth != iconWidth || Minecraft.getMinecraft().displayWidth != iconHeight)
            {
                try
                {
                    Minecraft.getMinecraft().toggleFullscreen();
                    ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), iconWidth, "tempDisplayWidth", "field_71436_X");
                    ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), iconHeight, "tempDisplayHeight", "field_71435_Y");
                    Minecraft.getMinecraft().toggleFullscreen();
                }
                catch (Exception e)
                {
                    System.err.println(e);
                }
            }

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
        System.out.println(tickNumber);
        if (tickNumber == 10)
        {
            DataLoader.ready = true;
            DataLoader.load();
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
