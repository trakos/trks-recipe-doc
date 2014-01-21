package trks.recipedoc.generate.renderers.utils;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import trks.recipedoc.generate.Generate;
import trks.recipedoc.generate.renderers.DataRenderer;
import trks.recipedoc.minecraft.RendererHelper;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;

public class RecipeBackgroundRenderer
{
    static public String getRecipeHandlerImageName(IRecipeHandler recipeHandler)
    {
        return recipeHandler.getRecipeName() + ".png";
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
    }

    static protected void endRecipesRendering()
    {
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

            ScreenshotRenderer.saveScreenshot(new File(Generate.craftingHandlersBackgroundsDirectory + "/" + getRecipeHandlerImageName(craftingHandler)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
