package trks.recipedoc.generate.renderers.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import trks.recipedoc.generate.Generate;
import trks.recipedoc.generate.renderers.DataRenderer;

import java.io.File;
import java.util.ArrayList;

public class IconRenderer
{
    protected static RenderItem renderItem = new RenderItem();

    static public String getIconName(ItemStack itemStack)
    {
        return itemStack.itemID + "_" + itemStack.getItemDamage() + ".png";
    }

    static public void renderItems(ArrayList<ItemStack> items)
    {
        IconRenderer.prepareIconRendering();

        for (ItemStack item : items)
        {
            try
            {
                IconRenderer.renderItem(item);
            }
            catch (Exception e)
            {
                System.err.println("Couldn't render " + item.getDisplayName());
                e.printStackTrace();
            }
        }

        IconRenderer.endIconRendering();
    }

    static protected void prepareIconRendering()
    {
        ScreenshotRenderer.deleteAllScreenshots();
        GL11.glPushMatrix();
        GL11.glScaled(10, 10, 1);
    }

    static protected void endIconRendering()
    {
        GL11.glPopMatrix();
    }

    static protected void renderItem(ItemStack itemStack)
    {
        if (Tessellator.instance.isDrawing)
        {
            Tessellator.instance.draw();
        }
        GL11.glClearColor(139f / 255f, 139f / 255f, 139f / 255f, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        drawItemStack(itemStack, 0, 0, "overlay");

        ScreenshotRenderer.saveTrimmedScreenshot(new File(Generate.iconDirectory + "/" + getIconName(itemStack)), 160, 160);
    }

    static protected void drawItemStack(ItemStack par1ItemStack, int par2, int par3, String par4Str)
    {
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), par1ItemStack, par2, par3);
        //renderItem.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, this.mc.getTextureManager(), par1ItemStack, par2, par3 - (this.draggedStack == null ? 0 : 8), par4Str);
        RenderHelper.disableStandardItemLighting();
    }
}
