package trks.recipedoc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.EXTFramebufferObject.*;

public class MyClass
{
    static protected int framebufferID;

    static protected int iconWidth = 32;
    static protected int iconHeight = 32;

    static protected void initFBO()
    {
        if (!GLContext.getCapabilities().GL_EXT_framebuffer_object)
        {
            System.err.println("FBO not supported!");
        }
        else
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
        glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, depthRenderBufferID);

        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    protected static RenderItem renderItem = new RenderItem();

    static protected void renderItem(ItemStack itemStack)
    {
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);

        GL11.glViewport(0, 0, iconWidth, iconHeight);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();
        GL11.glClearColor(1, 1, 1, 1f);


        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), itemStack, 0, 0, false);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glFlush();

        GL11.glReadBuffer(GL11.GL_FRONT);

        int width = iconWidth;
        int height = iconHeight;
        int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        saveImage(itemStack.getDisplayName(), width, height, bpp, buffer);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
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
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
