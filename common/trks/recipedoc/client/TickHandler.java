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
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.lang.reflect.Method;
import java.util.EnumSet;

/**
 * Created by trakos on 18.01.14.
 */
public class TickHandler implements ITickHandler
{
    boolean gameStarted = false;
    int tickNumber = 0;

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        if (!gameStarted)
        {
            /*try
            {
                Minecraft.getMinecraft().toggleFullscreen();
                ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), 64, "tempDisplayWidth", "");
                ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), 64, "tempDisplayHeight", "64");
                Minecraft.getMinecraft().toggleFullscreen();

            }
            catch (Exception e)
            {
                System.err.println(e);
            }*/
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
            DataLoader.load();

            mc.shutdown();
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
