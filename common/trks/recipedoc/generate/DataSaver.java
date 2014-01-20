package trks.recipedoc.generate;

import java.io.File;

public class DataSaver
{
    static public final String directory = "C:/test";
    public static final String iconDirectory = DataSaver.directory + "/icons";
    public static final String screenshotsDirectory = DataSaver.directory + "/screenshots";
    public static final String recipeDirectory = DataSaver.directory + "/recipes";
    public static final String guiHandlersDirectory = DataSaver.directory + "/gui_backgrounds";

    static protected void mkdirIfNotExists(File file)
    {
        if (!file.exists())
        {
            if (!file.mkdirs())
            {
                System.err.println("Can't mkdir " + file.getPath());
            }
        }
    }

    static public void init()
    {
        mkdirIfNotExists(new File(DataSaver.directory));
        mkdirIfNotExists(new File(DataSaver.iconDirectory));
        mkdirIfNotExists(new File(DataSaver.recipeDirectory));
        mkdirIfNotExists(new File(DataSaver.guiHandlersDirectory));
    }
}
