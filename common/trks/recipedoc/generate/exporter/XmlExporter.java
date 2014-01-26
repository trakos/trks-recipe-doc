package trks.recipedoc.generate.exporter;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import trks.recipedoc.generate.structs.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Collection;

public class XmlExporter
{
    static public void export(Collection<ItemStruct> items, Collection<RecipeStruct> recipes, Collection<RecipeTypeStruct> recipeHandlers, Collection<String> itemCategories, File target)
    {
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("root");
            doc.appendChild(rootElement);

            Element itemsElement = doc.createElement("items");
            rootElement.appendChild(itemsElement);

            for (ItemStruct item : items)
            {
                Element itemElement = doc.createElement("item");
                itemsElement.appendChild(itemElement);
                itemElement.setAttribute("id", Integer.toString(item.id));
                itemElement.setAttribute("damage", Integer.toString(item.damage));
                itemElement.setAttribute("icon", item.getIconName());
                itemElement.setAttribute("description", item.description);
                itemElement.setAttribute("name", item.name);
                itemElement.setAttribute("mod", item.mod);
                itemElement.setAttribute("type", item.type);
                itemElement.setAttribute("category", item.category);
                itemElement.setAttribute("showOnList", item.showOnList ? "1" : "0");
                itemElement.setAttribute("isBaseItem", item.isBaseItem ? "1" : "0");
                itemElement.setAttribute("tooltip", StringUtils.join(item.tooltipDescription, "; "));

                for (String key : item.attributes.keySet())
                {
                    Element itemAttribute = doc.createElement("attribute");
                    itemElement.appendChild(itemAttribute);

                    itemAttribute.setAttribute("key", key);
                    itemAttribute.setAttribute("value", item.attributes.get(key));
                }
            }

            Element recipesElement = doc.createElement("recipes");
            rootElement.appendChild(recipesElement);

            for (RecipeStruct recipe : recipes)
            {
                Element recipeElement = doc.createElement("recipe");
                recipesElement.appendChild(recipeElement);

                recipeElement.setAttribute("handlerName", recipe.recipeHandlerName);
                recipeElement.setAttribute("visible", recipe.visible ? "1" : "0");

                for (RecipeItemStruct item : recipe.items)
                {
                    Element recipeItemElement  = doc.createElement("ingredient");
                    recipeElement.appendChild(recipeItemElement);

                    recipeItemElement.setAttribute("type", item.elementType.toString());
                    recipeItemElement.setAttribute("x", Integer.toString(item.relativeX));
                    recipeItemElement.setAttribute("y", Integer.toString(item.relativeY));
                    recipeItemElement.setAttribute("amount", Integer.toString(item.amount));

                    for (RecipeItemStruct.RecipeItemIdStruct recipeItemId : item.itemIds)
                    {
                        Element optionElement = doc.createElement("option");
                        recipeItemElement.appendChild(optionElement);

                        optionElement.setAttribute("id", Integer.toString(recipeItemId.itemId));
                        optionElement.setAttribute("damage", Integer.toString(recipeItemId.damageId));
                    }
                }
                recipesElement.appendChild(recipeElement);
            }

            Element recipeTypesElement = doc.createElement("recipeTypes");
            rootElement.appendChild(recipeTypesElement);

            for (RecipeTypeStruct recipeType : recipeHandlers)
            {
                Element itemElement = doc.createElement("recipeType");
                recipeTypesElement.appendChild(itemElement);

                itemElement.setAttribute("id", recipeType.typeId);
                itemElement.setAttribute("name", recipeType.name);
                itemElement.setAttribute("image", recipeType.image);

                for (RecipeTypeMachineIdStruct recipeMachineId : recipeType.machines)
                {
                    Element machineElement = doc.createElement("machine");
                    itemElement.appendChild(machineElement);

                    machineElement.setAttribute("id", Integer.toString(recipeMachineId.itemId));
                    machineElement.setAttribute("damage", Integer.toString(recipeMachineId.damageId));
                }
            }

            Element itemCategoriesElement = doc.createElement("itemCategories");
            rootElement.appendChild(itemCategoriesElement);

            for (String itemCategory : itemCategories)
            {
                Element categoryElement = doc.createElement("category");
                itemCategoriesElement.appendChild(categoryElement);

                categoryElement.setAttribute("name", itemCategory);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(target);

            transformer.transform(source, result);

            System.out.println("File saved!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
