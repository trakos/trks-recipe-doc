package trks.recipedoc.generate.renderers.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import trks.recipedoc.generate.Generate;
import trks.recipedoc.generate.structs.ItemStruct;

import java.awt.*;
import java.io.File;
import java.nio.FloatBuffer;
import java.util.Collection;

public class IconRenderer
{
    protected static RenderItem renderItem = new RenderItem();

    static public void renderItems(Collection<ItemStruct> items)
    {
        IconRenderer.prepareIconRendering();

        for (ItemStruct item : items)
        {
            try
            {
                IconRenderer.renderItem(item);
            }
            catch (Exception e)
            {
                System.err.println("Couldn't render " + item.name);
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

    static final float COLOR_R = 1f / 255;
    static final float COLOR_G = 1f;
    static final float COLOR_B = 1f / 255;

    static protected void renderItem(ItemStruct itemStruct)
    {
        if (Tessellator.instance.isDrawing)
        {
            Tessellator.instance.draw();
        }
        GL11.glClearColor(COLOR_R, COLOR_G, COLOR_B, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawItemStack(itemStruct.getSourceItemStack(), 0, 0, "overlay");

        ScreenshotRenderer.saveTrimmedScreenshot(new File(Generate.iconDirectory + "/" + itemStruct.getIconName()), 160, 160, new Color(COLOR_R, COLOR_G, COLOR_B));
    }

    protected static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);
    protected static FloatBuffer setColorBuffer(float par0, float par1, float par2, float par3)
    {
        colorBuffer.clear();
        colorBuffer.put(par0).put(par1).put(par2).put(par3);
        colorBuffer.flip();
        return colorBuffer;
    }

    static protected void drawItemStack(ItemStack par1ItemStack, int par2, int par3, String par4Str)
    {
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, setColorBuffer(.1f, .1f, .1f, 1.0F));
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, setColorBuffer(.5f, .5f, .5f, 1.0F));
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) 240 / 1.0F, (float) 240 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        renderItem.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), par1ItemStack, par2, par3);
        //renderItem.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, this.mc.getTextureManager(), par1ItemStack, par2, par3 - (this.draggedStack == null ? 0 : 8), par4Str);
        RenderHelper.disableStandardItemLighting();
    }
}
