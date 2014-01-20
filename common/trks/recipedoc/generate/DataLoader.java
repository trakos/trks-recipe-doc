package trks.recipedoc.generate;

import codechicken.nei.ItemList;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import trks.recipedoc.minecraft.RendererHelper;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DataLoader
{
    public static boolean ready;

    static public ArrayList<ItemStruct> items = new ArrayList<ItemStruct>();

    static public void load()
    {

        ItemList.ThreadLoadItems thread = new ItemList.ThreadLoadItems();
        thread.run();

        /*IconSaver.prepareIconRendering();

        for (ItemStack item : ItemList.items)
        {
            items.add(getItemStruct(item));
        }

        IconSaver.endIconRendering();*/

        Minecraft.getMinecraft().displayGuiScreen(new GuiInventory(Minecraft.getMinecraft().thePlayer));


        for (ICraftingHandler craftingHandler : getActiveRecipeHandlers())
        {
            renderRecipeHandlerBackground(craftingHandler);
        }


        /*for (ItemStack item : ItemList.items)
        {
            loadRecipesByTarget(item);
        }*/

        System.out.print("test");
    }

    static protected ItemStruct getItemStruct(ItemStack itemStack)
    {
        ItemStruct item = new ItemStruct();

        item.id = itemStack.itemID;
        item.name = itemStack.getDisplayName();

        item.tooltipDescription = getNameList(itemStack);
        //noinspection unchecked
        item.attributes = (Multimap<String, String>) itemStack.getAttributeModifiers();

        try
        {
            IconSaver.renderItem(itemStack);
        }
        catch (Exception e)
        {
            System.err.println("Couldn't render " + itemStack.getDisplayName());
            e.printStackTrace();
        }

        return item;
    }

    static protected ArrayList<ICraftingHandler> getActiveRecipeHandlers()
    {
        ArrayList<ICraftingHandler> foundHandlers = new ArrayList<ICraftingHandler>();
        ArrayList<ICraftingHandler> remainingHandlers = new ArrayList<ICraftingHandler>(GuiCraftingRecipe.craftinghandlers);
        for (ItemStack item : ItemList.items)
        {
            Iterator<ICraftingHandler> craftingHandlerIterator = remainingHandlers.iterator();
            while (craftingHandlerIterator.hasNext())
            {
                ICraftingHandler craftingHandler = craftingHandlerIterator.next();
                ICraftingHandler handler = craftingHandler.getRecipeHandler("item", item.copy());
                if (handler.numRecipes() > 0)
                {
                    foundHandlers.add(handler);
                    craftingHandlerIterator.remove();
                }
            }
            if (remainingHandlers.isEmpty())
            {
                break;
            }
        }
        return foundHandlers;
    }

    static protected void renderRecipeHandlerBackground(ICraftingHandler craftingHandler)
    {
        if (Tessellator.instance.isDrawing)
        {
            Tessellator.instance.draw();
        }
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1, 1, 1, 1);

        Constructor<?> constructor = GuiCraftingRecipe.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        try
        {
            ICraftingHandler[] craftingHandlers = {craftingHandler};
            GuiCraftingRecipe recipe = (GuiCraftingRecipe) constructor.newInstance(Minecraft.getMinecraft().currentScreen, new ArrayList<ICraftingHandler>(Arrays.asList(craftingHandlers)));
            Minecraft.getMinecraft().displayGuiScreen(recipe);
            GL11.glClearColor(1, 1, 1, 1);

            GL11.glColor4f(1, 1, 1, 1);
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("nei:textures/gui/recipebg.png"));
            GL11.glPushMatrix();
            {
                int xSize = 176;
                int ySize = 166;

                ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft().gameSettings, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
                int sWidth = scaledresolution.getScaledWidth();
                int sHeight = scaledresolution.getScaledHeight();

                float scale = Math.min((float)sWidth / xSize, (float)sHeight / ySize);
                GL11.glScalef(scale, scale, 1);
            }
            RendererHelper.drawTexturedModalRect(0, 0, 0, 0, 176, 166, 0);
            GL11.glTranslatef(5, 16, 0);
            craftingHandler.drawBackground(0);
            GL11.glPopMatrix();
            /*Method protectedMethod = GuiRecipe.class.getDeclaredMethod("drawGuiContainerBackgroundLayer", float.class, int.class, int.class);
            protectedMethod.setAccessible(true);
            protectedMethod.invoke(recipe, 0f, 0, 0);*/
            /*protectedMethod = GuiRecipe.class.getDeclaredMethod("drawGuiContainerForegroundLayer", int.class, int.class);
            protectedMethod.setAccessible(true);
            protectedMethod.invoke(recipe, 0, 0);*/
            //recipe.drawScreen(0, 0, 0);


            ImageRenderer.saveScreenshot(new File(DataSaver.guiHandlersDirectory + "/" + craftingHandler.getRecipeName() + ".png"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    static protected void loadRecipesByTarget(ItemStack itemStack)
    {
        ArrayList<ICraftingHandler> handlers = new ArrayList<ICraftingHandler>();
        for (ICraftingHandler craftingHandler : GuiCraftingRecipe.craftinghandlers)
        {
            ICraftingHandler handler = craftingHandler.getRecipeHandler("item", itemStack.copy());
            if (handler.numRecipes() > 0)
            {
                handlers.add(handler);
            }
        }

        if (handlers.isEmpty()) return;

        if (Tessellator.instance.isDrawing)
        {
            Tessellator.instance.draw();
        }
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        int x, y;
        {
            ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft().gameSettings, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            x = Mouse.getX() * i / Minecraft.getMinecraft().displayWidth;
            y = j - Mouse.getY() * j / Minecraft.getMinecraft().displayHeight - 1;
        }

        Constructor<?> constructor = GuiCraftingRecipe.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        try
        {
            GuiCraftingRecipe recipe = (GuiCraftingRecipe) constructor.newInstance(Minecraft.getMinecraft().currentScreen, handlers);
            Minecraft.getMinecraft().displayGuiScreen(recipe);
            GL11.glClearColor(1, 1, 1, 1);

            Method protectedMethod = GuiRecipe.class.getDeclaredMethod("drawGuiContainerBackgroundLayer", float.class, int.class, int.class);
            protectedMethod.setAccessible(true);
            protectedMethod.invoke(recipe, 0f, (int)- (Minecraft.getMinecraft().displayWidth / 2), (int)- (Minecraft.getMinecraft().displayHeight / 2));
            protectedMethod = GuiRecipe.class.getDeclaredMethod("drawGuiContainerForegroundLayer", int.class, int.class);
            protectedMethod.setAccessible(true);
            protectedMethod.invoke(recipe, 0, 0);

            protectedMethod = GuiContainer.class.getDeclaredMethod("drawSlotInventory", Slot.class);
            protectedMethod.setAccessible(true);
            for (int j1 = 0; j1 < recipe.inventorySlots.inventorySlots.size(); ++j1)
            {
                Slot slot = (Slot)recipe.inventorySlots.inventorySlots.get(j1);
                protectedMethod.invoke(recipe, slot);
            }
                //recipe.drawScreen(0, 0, 0);


            ImageRenderer.saveScreenshot(new File(DataSaver.recipeDirectory + "/" + itemStack.itemID + "_" + itemStack.getItemDamage() + ".png"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        /*try
        {

            if (Tessellator.instance.isDrawing)
            {
                Tessellator.instance.draw();
            }
            GL11.glClearColor(1, 1, 1, 0);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);

            GuiCraftingRecipe.openRecipeGui("item", itemStack.copy());
            Minecraft.getMinecraft().ingameGUI.renderGameOverlay(0, Minecraft.getMinecraft().currentScreen != null, 0, 0);

            ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft().gameSettings, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            int k = Mouse.getX() * i / Minecraft.getMinecraft().displayWidth;
            int l = j - Mouse.getY() * j / Minecraft.getMinecraft().displayHeight - 1;
            ImageRenderer.saveScreenshot(new File(DataSaver.recipeDirectory + "/" + itemStack.itemID + "_" + itemStack.getItemDamage() + ".png"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/


    }

    protected static List<String> getNameList(ItemStack itemstack)
    {
        List<String> nameList = null;
        try
        {
            //noinspection unchecked
            nameList = itemstack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        }
        catch (Throwable ignored)
        {

        }

        if (nameList == null)
        {
            nameList = new ArrayList<String>();
        }

        if (nameList.size() == 0)
        {
            nameList.add("Unnamed");
        }

        if (nameList.get(0) == null || nameList.get(0).equals(""))
        {
            nameList.set(0, "Unnamed");
        }

        nameList.set(0, "\247" + Integer.toHexString(itemstack.getRarity().rarityColor) + nameList.get(0));
        for (int i = 1; i < nameList.size(); i++)
        {
            nameList.set(i, "\u00a77" + nameList.get(i));
        }

        return nameList;
    }
}
