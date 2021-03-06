package trks.recipedoc.minecraft;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;


public class RendererHelper
{
    static public void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6, double zLevel)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), (double)zLevel, (double)((float)(par3 + 0) * f), (double)((float)(par4 + par6) * f1));
        tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double)zLevel, (double)((float)(par3 + par5) * f), (double)((float)(par4 + par6) * f1));
        tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), (double)zLevel, (double)((float)(par3 + par5) * f), (double)((float)(par4 + 0) * f1));
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)zLevel, (double)((float)(par3 + 0) * f), (double)((float)(par4 + 0) * f1));
        tessellator.draw();
    }

    static public void resizeWindow(int width, int height, boolean force)
    {
        if (force || Minecraft.getMinecraft().displayWidth != width || Minecraft.getMinecraft().displayWidth != height)
        {
            try
            {
                Minecraft.getMinecraft().toggleFullscreen();
                ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), width, "tempDisplayWidth", "field_71436_X");
                ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), height, "tempDisplayHeight", "field_71435_Y");
                Minecraft.getMinecraft().toggleFullscreen();
            }
            catch (Exception e)
            {
                System.err.println(e);
            }
        }
    }
}
