package trks.recipedoc.client;

import codechicken.nei.ItemList;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ScreenShotHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class DataLoader
{
    public static boolean ready;

    /*@ForgeSubscribe(priority = EventPriority.NORMAL)
    public void eventHandler(RenderGameOverlayEvent event)
    {
        if (ready)
        {
            load();
            ready = false;
            Minecraft.getMinecraft().shutdown();
        }
    }*/

    static public ArrayList<ItemStruct> items = new ArrayList<ItemStruct>();

    static public void load()
    {
        ItemList.ThreadLoadItems thread = new ItemList.ThreadLoadItems();
        thread.run();

        GL11.glScaled(10, 10, 1);

        for (ItemStack item : ItemList.items)
        {
            items.add(getItemStruct(item));
        }

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
            renderItem(itemStack);
        }
        catch (Exception e)
        {
            System.err.println("Couldn't render " + itemStack.getDisplayName());
            e.printStackTrace();
        }

        return item;
    }

    protected static RenderItem renderItem = new RenderItem();

    static private void drawItemStack(ItemStack par1ItemStack, int par2, int par3, String par4Str)
    {
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), par1ItemStack, par2, par3);
        //renderItem.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, this.mc.getTextureManager(), par1ItemStack, par2, par3 - (this.draggedStack == null ? 0 : 8), par4Str);
        RenderHelper.disableStandardItemLighting();
    }

    static protected void renderItem(ItemStack itemStack)
    {
        if (Tessellator.instance.isDrawing)
        {
            Tessellator.instance.draw();
        }
        GL11.glClearColor(1, 1, 1, 0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        drawItemStack(itemStack, 0, 0, "overlay");

        String directory = "C:/test/";
        ScreenShotHelper.saveScreenshot(new File(directory), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        moveImageTo(new File(directory + itemStack.getDisplayName() + ".png"), directory);
    }

    static protected void moveImageTo(File moveTo, String directory)
    {
        if (moveTo.exists())
        {
            if (!moveTo.delete())
            {
                System.err.println("moveTo returned false");
            }
        }
        File f = new File(directory + "screenshots");
        File[] files = f.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.toLowerCase().endsWith(".png");
            }
        });
        if (files == null)
        {
            System.err.println("no files!");
        }
        else
        {
            if (files.length != 1)
            {
                System.err.println("no files or more than one file!");
            }
            if (!files[0].renameTo(moveTo))
            {
                System.err.println("renameTo returned false");
            }
            files[0].delete();
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
