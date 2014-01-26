package trks.recipedoc.api;

import java.util.ArrayList;

public class API
{
    public static final String STANDARD_CATEGORY_POTIONS = "Potions";
    public static final String STANDARD_CATEGORY_ITEMS = "Items";
    public static final String STANDARD_CATEGORY_WEAPONS = "Weapons";
    public static final String STANDARD_CATEGORY_ARMOR = "Armor";
    public static final String STANDARD_CATEGORY_FOOD = "Food";
    public static final String STANDARD_CATEGORY_ORE = "Ores";
    public static final String STANDARD_CATEGORY_PLANTS = "Plants";
    public static final String STANDARD_CATEGORY_BLOCKS = "Blocks";
    public static final String STANDARD_CATEGORY_WOOD = "Wood";
    public static final String STANDARD_CATEGORY_OTHER = "Other";
    public static final String STANDARD_CATEGORY_INTERNAL = "Internal";


    static public final ArrayList<IDocModSupport> docModSupports = new ArrayList<IDocModSupport>();

    static public void registerModSupport(IDocModSupport modSupport)
    {
        docModSupports.add(modSupport);
    }
}
