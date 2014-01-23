package trks.recipedoc.modsupport;

import trks.recipedoc.modsupport.mods.MekanismSupport;

import java.util.ArrayList;

public class API
{
    public static String STANDARD_CATEGORY_ITEMS = "Items";
    public static String STANDARD_CATEGORY_WEAPONS = "Weapons";
    public static String STANDARD_CATEGORY_ARMOR = "Armor";
    public static String STANDARD_CATEGORY_FOOD = "Food";
    public static String STANDARD_CATEGORY_BLOCKS = "Blocks";
    public static String STANDARD_CATEGORY_OTHER = "Other";
    static
    {
        docModSupports = new ArrayList<IDocModSupport>();
        registerModSupport(new MekanismSupport());
    }

    static public final ArrayList<IDocModSupport> docModSupports;

    static public void registerModSupport(IDocModSupport modSupport)
    {
        docModSupports.add(modSupport);
    }
}
