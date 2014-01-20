package trks.recipedoc.generate;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ScreenShotHelper;

import java.io.File;
import java.io.FilenameFilter;

public class ImageRenderer
{

    static public void deleteAllScreenshots()
    {
        File[] files = getScreenshots();
        for(File file : files)
        {
            if (!file.delete())
            {
                System.err.println("Couldn't delete " + file.getPath());
            }
        }
    }

    static public void saveScreenshot(File target)
    {
        ScreenShotHelper.saveScreenshot(new File(DataSaver.directory), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        moveImageTo(target);
    }

    static protected void moveImageTo(File moveTo)
    {
        if (moveTo.exists())
        {
            if (!moveTo.delete())
            {
                System.err.println("Couldn't delete already existing " + moveTo.getPath());
            }
        }
        File[] files = getScreenshots();
        if (files == null)
        {
            System.err.println("no files!");
        }
        else
        {
            if (files.length != 1)
            {
                System.err.println("more than one file!");
            }
            if (!files[0].renameTo(moveTo))
            {
                System.err.println("renameTo from " + files[0].getPath() + " to " + moveTo.getPath() + " returned false");
            }
        }
    }

    static protected File[] getScreenshots()
    {
        File f = new File(DataSaver.screenshotsDirectory);
        File[] files = f.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.toLowerCase().endsWith(".png");
            }
        });
        return files == null ? new File[0] : files;
    }
}
