package trks.recipedoc.generate;

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

public class IconSaver
{
    protected static RenderItem renderItem = new RenderItem();



    static public void prepareIconRendering()
    {
        ImageRenderer.deleteAllScreenshots();
        GL11.glPushMatrix();
        GL11.glScaled(10, 10, 1);
    }

    static public void endIconRendering()
    {
        GL11.glPopMatrix();
    }

    static public void renderItem(ItemStack itemStack)
    {
        if (Tessellator.instance.isDrawing)
        {
            Tessellator.instance.draw();
        }
        GL11.glClearColor(1, 1, 1, 0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        drawItemStack(itemStack, 0, 0, "overlay");

        ImageRenderer.saveScreenshot(new File(DataSaver.iconDirectory + "/" + itemStack.itemID + "_" + itemStack.getItemDamage() + ".png"));
    }

    static protected void drawItemStack(ItemStack par1ItemStack, int par2, int par3, String par4Str)
    {
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), par1ItemStack, par2, par3);
        //renderItem.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, this.mc.getTextureManager(), par1ItemStack, par2, par3 - (this.draggedStack == null ? 0 : 8), par4Str);
        RenderHelper.disableStandardItemLighting();
    }
}
