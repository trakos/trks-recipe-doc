package trks.recipedoc.generate.renderers.utils;

import codechicken.nei.NEIClientUtils;
import codechicken.nei.NEIController;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import trks.recipedoc.generate.Generate;
import trks.recipedoc.generate.loaders.DataNEIFetcher;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;

public class RecipeBackgroundRenderer
{
    static public String getRecipeHandlerImageName(IRecipeHandler recipeHandler)
    {
        return ScreenshotRenderer.toFileSystemSafeName(DataNEIFetcher.getRecipeHandlerId(recipeHandler), false, 255) + ".png";
    }

    static public void renderAll(ArrayList<ICraftingHandler> craftingHandlers)
    {
        prepareRecipesRendering();

        for (ICraftingHandler craftingHandler : craftingHandlers)
        {
            renderRecipeHandlerBackground(craftingHandler);
        }

        endRecipesRendering();
    }

    static protected void prepareRecipesRendering()
    {
        ScreenshotRenderer.deleteAllScreenshots();
        Minecraft.getMinecraft().displayGuiScreen(new GuiInventory(Minecraft.getMinecraft().thePlayer));
        GL11.glClearColor(32f / 255f, 32f / 255f, 32f / 255f, 1);
    }

    static protected void endRecipesRendering()
    {
    }

    static final float COLOR_R = 1f / 255;
    static final float COLOR_G = 1f;
    static final float COLOR_B = 1f / 255;

    static protected void renderRecipeHandlerBackground(ICraftingHandler craftingHandler)
    {
        GL11.glClearColor(COLOR_R, COLOR_G, COLOR_B, 1);

        if (Tessellator.instance.isDrawing)
        {
            Tessellator.instance.draw();
        }
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(COLOR_R, COLOR_G, COLOR_B, 1);

        Constructor<?> constructor = GuiCraftingRecipe.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        try
        {
            ICraftingHandler[] craftingHandlers = {craftingHandler};
            GuiCraftingRecipe recipe = (GuiCraftingRecipe) constructor.newInstance(Minecraft.getMinecraft().currentScreen, new ArrayList<ICraftingHandler>(Arrays.asList(craftingHandlers)));
            NEIClientUtils.overlayScreen(recipe);
            GL11.glClearColor(COLOR_R, COLOR_G, COLOR_B, 1);

            GL11.glColor4f(1, 1, 1, 1);
            Minecraft.getMinecraft().renderEngine.bindTexture("nei:textures/gui/recipebg.png");
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
            //RendererHelper.drawTexturedModalRect(0, 0, 0, 0, 176, 166, 0);

            GL11.glTranslatef(5, 16, 0);
            craftingHandler.drawBackground(NEIController.manager, 0);
            GL11.glPopMatrix();

            File targetFile = new File(Generate.craftingHandlersBackgroundsDirectory + "/" + getRecipeHandlerImageName(craftingHandler));
            int y = 46;
            ScreenshotRenderer.saveTrimmedScreenshot(targetFile, 0, y, 512, 500/*246*/ - y, new Color(COLOR_R, COLOR_G, COLOR_B));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
