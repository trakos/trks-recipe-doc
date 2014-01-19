package trks.recipedoc.client;

import codechicken.nei.ItemList;
import codechicken.nei.forge.IContainerTooltipHandler;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ScreenShotHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.glReadPixels;

public class DataLoader
{
    static public ArrayList<ItemStruct> items = new ArrayList<ItemStruct>();

    static public void load()
    {
        ItemList.ThreadLoadItems thread = new ItemList.ThreadLoadItems();
        thread.run();

        //initFBO();
        GL11.glScaled(4d, 4d, 1d);

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
        } catch (Exception e)
        {
            System.err.println("Couldn't render " + itemStack.getDisplayName());
        }

        return item;
    }

    static protected int framebufferID;

    static protected int iconWidth = 32;
    static protected int iconHeight = 32;

    static protected void initFBO()
    {
        iconWidth = Minecraft.getMinecraft().displayWidth;
        iconHeight = Minecraft.getMinecraft().displayHeight;
        if (!GLContext.getCapabilities().GL_EXT_framebuffer_object)
        {
            System.err.println("FBO not supported!");
        } else
        {
            System.out.println("FBO supported.");
        }
        framebufferID = glGenFramebuffersEXT();
        int colorTextureID = GL11.glGenTextures();
        int depthRenderBufferID = glGenRenderbuffersEXT();

        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTextureID);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, iconWidth, iconHeight, 0, GL11.GL_RGBA, GL11.GL_INT, (java.nio.ByteBuffer) null);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D, colorTextureID, 0);

        glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthRenderBufferID);
        glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL11.GL_DEPTH_COMPONENT, iconWidth, iconHeight);
        glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, depthRenderBufferID);

        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    protected static RenderItem renderItem = new RenderItem();



    static protected void renderItem(ItemStack itemStack)
    {
        /*glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);

        GL11.glViewport(0, 0, iconWidth, iconHeight);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearDepth (1.0f);
        GL11.glClearColor(1, 1, 1, 1f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearDepth(1.0D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);


        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), itemStack, 10, 10, false);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glFlush();

        drawBox();

        GL11.glReadBuffer(GL11.GL_FRONT);

        int width = iconWidth;
        int height = iconHeight;
        int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        saveImage(itemStack.getDisplayName(), width, height, bpp, buffer);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);*/
        GL11.glClearColor(1, 1, 1, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), itemStack, 0, 0, false);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        ScreenShotHelper.saveScreenshot(new File("C:/test/"), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    }

    static protected void saveImage(String name, int width, int height, int bpp, ByteBuffer buffer)
    {
        File file = new File("C:/test/" + name + ".png"); // The file to save to.
        String format = "PNG";
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width * height * bpp; x++)
        {
            byte value = buffer.get(x);
            x = x;
        }
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int i = (x + (width * y)) * bpp;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }

        try
        {
            ImageIO.write(image, format, file);
        } catch (IOException e)
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
    }

    protected static List<String> getNameList(ItemStack itemstack)
    {
        List<String> nameList = null;
        try
        {
            //noinspection unchecked
            nameList = itemstack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        } catch (Throwable ignored)
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

    static protected void drawBox()
    {
        GL11.glColor3f(1, 0, 0);
        // this func just draws a perfectly normal box with some texture coordinates
        GL11.glBegin(GL11.GL_QUADS);
        // Front Face
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(-1.0f, -1.0f, 1.0f);  // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(1.0f, -1.0f, 1.0f);  // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, 1.0f);  // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(-1.0f, 1.0f, 1.0f);  // Top Left Of The Texture and Quad
        // Back Face
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(-1.0f, -1.0f, -1.0f);  // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(-1.0f, 1.0f, -1.0f);  // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, -1.0f);  // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(1.0f, -1.0f, -1.0f);  // Bottom Left Of The Texture and Quad
        // Top Face
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(-1.0f, 1.0f, -1.0f);  // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(-1.0f, 1.0f, 1.0f);  // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(1.0f, 1.0f, 1.0f);  // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, -1.0f);  // Top Right Of The Texture and Quad
        // Bottom Face
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(-1.0f, -1.0f, -1.0f);  // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(1.0f, -1.0f, -1.0f);  // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(1.0f, -1.0f, 1.0f);  // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(-1.0f, -1.0f, 1.0f);  // Bottom Right Of The Texture and Quad
        // Right face
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(1.0f, -1.0f, -1.0f);  // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, -1.0f);  // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, 1.0f);  // Top Left Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(1.0f, -1.0f, 1.0f);  // Bottom Left Of The Texture and Quad
        // Left Face
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(-1.0f, -1.0f, -1.0f);  // Bottom Left Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(-1.0f, -1.0f, 1.0f);  // Bottom Right Of The Texture and Quad
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(-1.0f, 1.0f, 1.0f);  // Top Right Of The Texture and Quad
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(-1.0f, 1.0f, -1.0f);  // Top Left Of The Texture and Quad
        GL11.glEnd();
    }
}
