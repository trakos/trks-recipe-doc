package trks.recipedoc.generate.exporter;

import com.google.common.collect.ImmutableMap;
import trks.recipedoc.generate.structs.ItemStruct;
import trks.recipedoc.generate.structs.RecipeItemStruct;
import trks.recipedoc.generate.structs.RecipeStruct;
import trks.recipedoc.generate.structs.RecipeTypeStruct;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

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


    static public void export(Collection<ItemStruct> items, Collection<RecipeStruct> recipes, ArrayList<RecipeTypeStruct> recipeHandlers, File target)
    {
        try
        {
            PhpExporter writer = new PhpExporter(target, "UTF-8");

            writer.println("<?php");

            writer.printClass("RecipeDocData", (new ImmutableMap.Builder<String, String>())
                    .put("items", "Item[]")
                    .put("recipes", "Recipe[]")
                    .put("recipeTypes", "RecipeHandler[]")
                    .build());
            writer.printClass("Item", (new ImmutableMap.Builder<String, String>())
                    .put("id", "int")
                    .put("damage", "int")
                    .put("icon", "string")
                    .put("name", "string")
                    .put("mod", "string")
                    .put("type", "string")
                    .put("description", "string")
                    .put("attributes", "string[]")
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
                    .build());
            writer.printClass("RecipeHandler", (new ImmutableMap.Builder<String, String>())
                    .put("id", "string")
                    .put("name", "string")
                    .put("image", "string")
                    .build());

            writer.printlnAndIndent("return new RecipeDocData(array(");
            {
                writer.printlnAndIndent("'items' => array(");
                for (ItemStruct item : items)
                {
                    writer.printlnAndIndent("new Item(array(");
                    {
                        writer.printArrayAssign("id", item.id);
                        writer.printArrayAssign("damage", item.damage);
                        writer.printArrayAssign("icon", item.icon);
                        writer.printArrayAssign("name", item.name);
                        writer.printArrayAssign("mod", item.mod);
                        writer.printArrayAssign("type", item.type);
                        writer.printArrayAssign("description", item.description);
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
                                    for (RecipeItemStruct.RecipeItemIdStruct itemIdStruct : item.itemIds)
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
                    }
                    writer.undoIndentAndPrintln(")),");
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
