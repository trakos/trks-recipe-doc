package trks.recipedoc.generate;

import trks.recipedoc.generate.exporter.PhpExporter;
import trks.recipedoc.generate.exporter.XmlExporter;
import trks.recipedoc.generate.finishers.ItemCostCalculator;
import trks.recipedoc.generate.loaders.DataLoader;
import trks.recipedoc.generate.renderers.DataRenderer;

import java.io.File;


public class Generate
{
    static public final String directory = "C:/test";
    public static final String iconDirectory = directory + "/icons";
    public static final String screenshotsDirectory = directory + "/screenshots";
    public static final String craftingHandlersBackgroundsDirectory = directory + "/gui_backgrounds";
    static public final String xmlFile = directory + "/recipes.xml";
    static public final String phpFile = directory + "/recipes.php";

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

    static protected void init()
    {
        mkdirIfNotExists(new File(directory));
        mkdirIfNotExists(new File(iconDirectory));
        mkdirIfNotExists(new File(craftingHandlersBackgroundsDirectory));
    }

    static public void generate()
    {
        init();
        DataLoader dataLoader = new DataLoader();
        DataRenderer.render(dataLoader.items);
        ItemCostCalculator dataCalculator = new ItemCostCalculator(dataLoader.items, dataLoader.recipes);
        dataCalculator._printNotCraftedItems();

        XmlExporter.export(dataLoader.items, dataLoader.recipes, DataLoader.getRecipeHandlers(), dataLoader.categories, new File(xmlFile));
        PhpExporter.export(dataLoader.items, dataLoader.recipes, DataLoader.getRecipeHandlers(), dataLoader.categories, new File(phpFile));
    }
}
