package trks.recipedoc.generate.exporter;

import com.google.common.collect.ImmutableMap;
import trks.recipedoc.generate.structs.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;

public class PhpExporter extends PrintWriter
{
    public PhpExporter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException
    {
        super(file, csn);
    }

    protected int indentation = 0;
    public void indent()
    {
        indentation++;
    }
    public void undoIndent()
    {
        indentation--;
    }

    public void printlnAndIndent(String text)
    {
        println(text);
        indent();
    }
    public void undoIndentAndPrintln(String text)
    {
        undoIndent();
        println(text);
    }

    public void println(String text)
    {
        for (int i = 0; i < indentation; i++)
        {
            super.print("\t");
        }
        super.println(text);
    }

    public void printArrayAssign(String key, String value)
    {
        println("'" + key + "' => '" + (value != null ? value.replace("'", "\\'") : "") + "',");
    }

    public void printArrayAssign(String key, int value)
    {
        printArrayAssign(key, Integer.toString(value));
    }

    public void printArrayAssign(String key, float value)
    {
        printArrayAssign(key, Float.toString(value));
    }

    public void printArrayAssign(String key, boolean value)
    {
        // we can't call string printArrayAssign, because it would put true/false into quotes
        println("'" + key + "' => " + (value ? "true" : "false") + ",");
    }

    protected void printClassField(String fieldName, String fieldType)
    {
        println("/**");
        println(" * @var " + fieldType);
        println(" */");
        println("public $" + fieldName + ";");
    }

    public void printClass(String className, ImmutableMap<String,String> build)
    {
        println();
        println("class " + className);
        printlnAndIndent("{");
        {
            for (String fieldName : build.keySet())
            {
                printClassField(fieldName, build.get(fieldName));
            }
            println("");
            println("public function __construct($array)");
            printlnAndIndent("{");
            {
                for (String fieldName : build.keySet())
                {
                    println("$this->" + fieldName + " = $array['" + fieldName + "'];");
                }
            }
            undoIndentAndPrintln("}");
        }
        undoIndentAndPrintln("}");
    }


    static public void export(Collection<ItemStruct> items, Collection<RecipeStruct> recipes, Collection<RecipeTypeStruct> recipeHandlers, Collection<String> itemCategories, File target)
    {
        try
        {
            PhpExporter writer = new PhpExporter(target, "UTF-8");

            writer.println("<?php");

            writer.printClass("RecipeDocData", (new ImmutableMap.Builder<String, String>())
                    .put("items", "Item[]")
                    .put("recipes", "Recipe[]")
                    .put("recipeTypes", "RecipeHandler[]")
                    .put("itemCategories", "string[]")
                    .build());
            writer.printClass("Item", (new ImmutableMap.Builder<String, String>())
                    .put("id", "int")
                    .put("damage", "int")
                    .put("icon", "string")
                    .put("name", "string")
                    .put("rawName", "string")
                    .put("mod", "string")
                    .put("type", "string")
                    .put("category", "string")
                    .put("description", "string")
                    .put("attributes", "string[]")
                    .put("showOnList", "bool")
                    .put("isBaseItem", "bool")
                    .put("rawCost", "ItemRawCost[]")
                    .build());
            writer.printClass("ItemRawCost", (new ImmutableMap.Builder<String, String>())
                    .put("items", "ItemRawCostEntry[]")
                    .build());
            writer.printClass("ItemRawCostEntry", (new ImmutableMap.Builder<String, String>())
                    .put("id", "int")
                    .put("damage", "int")
                    .put("amount", "float")
                    .build());
            writer.printClass("ItemId", (new ImmutableMap.Builder<String, String>())
                    .put("id", "int")
                    .put("damage", "int")
                    .build());
            writer.printClass("RecipeIngredient", (new ImmutableMap.Builder<String, String>())
                    .put("type", "string")
                    .put("x", "int")
                    .put("y", "int")
                    .put("amount", "int")
                    .put("items", "ItemId[]")
                    .build());
            writer.printClass("Recipe", (new ImmutableMap.Builder<String, String>())
                    .put("recipeHandler", "string")
                    .put("ingredients", "RecipeIngredient[]")
                    .put("visible", "Boolean")
                    .build());
            writer.printClass("RecipeHandler", (new ImmutableMap.Builder<String, String>())
                    .put("id", "string")
                    .put("name", "string")
                    .put("image", "string")
                    .put("machines", "ItemId[]")
                    .build());

            writer.printlnAndIndent("return new RecipeDocData(array(");
            {
                writer.printlnAndIndent("'items' => array(");
                for (ItemStruct item : items)
                {
                    writer.printlnAndIndent("new Item(array(");
                    {
                        writer.printArrayAssign("id", item.itemId);
                        writer.printArrayAssign("damage", item.damageId);
                        writer.printArrayAssign("icon", item.getIconName());
                        writer.printArrayAssign("name", item.name);
                        writer.printArrayAssign("rawName", item.rawName);
                        writer.printArrayAssign("mod", item.mod);
                        writer.printArrayAssign("type", item.type);
                        writer.printArrayAssign("category", item.category);
                        writer.printArrayAssign("description", item.description);
                        writer.printArrayAssign("showOnList", item.showOnList);
                        writer.printArrayAssign("isBaseItem", item.isBaseItem);
                        writer.printlnAndIndent("'attributes' => array(");
                        for (String key : item.attributes.keySet())
                        {
                            writer.printlnAndIndent("array(");
                            {
                                writer.printArrayAssign("id", key);
                                writer.printArrayAssign("value", item.attributes.get(key));
                            }
                            writer.undoIndentAndPrintln("),");
                        }
                        writer.undoIndentAndPrintln("),");
                        writer.printlnAndIndent("'rawCost' => array(");
                        for (HashMap<IdDamagePair, Float> rawCost : item.rawCosts)
                        {
                            writer.printlnAndIndent("new ItemRawCost(array('items' => array(");
                            {
                                for (IdDamagePair idDamagePair : rawCost.keySet())
                                {
                                    float amount = (float)rawCost.get(idDamagePair);
                                    writer.printlnAndIndent("new ItemRawCostEntry(array(");
                                    {
                                        writer.printArrayAssign("id", idDamagePair.itemId);
                                        writer.printArrayAssign("damage", idDamagePair.damageId);
                                        writer.printArrayAssign("amount", amount);
                                    }
                                    writer.undoIndentAndPrintln(")),");
                                }
                            }
                            writer.undoIndentAndPrintln("))),");
                        }
                        writer.undoIndentAndPrintln("),");
                    }
                    writer.undoIndentAndPrintln(")),");
                }
                writer.undoIndentAndPrintln("),");

                writer.printlnAndIndent("'recipes' => array(");
                for (RecipeStruct recipe : recipes)
                {
                    writer.printlnAndIndent("new Recipe(array(");
                    {
                        writer.printArrayAssign("recipeHandler", recipe.recipeHandlerName);
                        writer.printArrayAssign("visible", recipe.visible);
                        writer.printlnAndIndent("'ingredients' => array(");
                        {
                            for (RecipeItemStruct item : recipe.items)
                            {
                                writer.printlnAndIndent("new RecipeIngredient(array(");
                                {
                                    writer.printArrayAssign("type", item.elementType.toString());
                                    writer.printArrayAssign("x", item.relativeX);
                                    writer.printArrayAssign("y", item.relativeY);
                                    writer.printArrayAssign("amount", item.amount);
                                    writer.printlnAndIndent("'items' => array(");
                                    for (IdDamagePair itemIdStruct : item.itemIds)
                                    {
                                        writer.printlnAndIndent("new ItemId(array(");
                                        {
                                            writer.printArrayAssign("id", itemIdStruct.itemId);
                                            writer.printArrayAssign("damage", itemIdStruct.damageId);
                                        }
                                        writer.undoIndentAndPrintln(")),");
                                    }
                                    writer.undoIndentAndPrintln("),");
                                }
                                writer.undoIndentAndPrintln(")),");
                            }
                        }
                        writer.undoIndentAndPrintln("),");
                    }
                    writer.undoIndentAndPrintln(")),");
                }
                writer.undoIndentAndPrintln("),");

                writer.printlnAndIndent("'recipeTypes' => array(");
                for (RecipeTypeStruct recipeType : recipeHandlers)
                {
                    writer.printlnAndIndent("new RecipeHandler(array(");
                    {
                        writer.printArrayAssign("id", recipeType.typeId);
                        writer.printArrayAssign("name", recipeType.name);
                        writer.printArrayAssign("image", recipeType.image);
                        writer.printlnAndIndent("'machines' => array(");
                        for (IdDamagePair machine : recipeType.machines)
                        {
                            writer.printlnAndIndent("new ItemId(array(");
                            {
                                writer.printArrayAssign("id", machine.itemId);
                                writer.printArrayAssign("damage", machine.damageId);
                            }
                            writer.undoIndentAndPrintln(")),");
                        }
                        writer.undoIndentAndPrintln("),");
                    }
                    writer.undoIndentAndPrintln(")),");
                }
                writer.undoIndentAndPrintln("),");

                writer.printlnAndIndent("'itemCategories' => array(");
                for (String category : itemCategories)
                {
                    writer.println("'" + category + "',");
                }
                writer.undoIndentAndPrintln("),");
            }
            writer.undoIndentAndPrintln("));");
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
