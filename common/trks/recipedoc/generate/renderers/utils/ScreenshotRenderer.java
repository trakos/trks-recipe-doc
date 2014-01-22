package trks.recipedoc.generate.renderers.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ScreenShotHelper;
import trks.recipedoc.generate.Generate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class ScreenshotRenderer
{

    static public void deleteAllScreenshots()
    {
        File[] files = getScreenshots();
        for(File file : files)
        {
            if (!file.delete())
            {
                System.err.println("Couldn't delete " + file.getPath());
            }
        }
    }

    static public void saveScreenshot(File target)
    {
        ScreenShotHelper.saveScreenshot(new File(Generate.directory), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        moveImageTo(target);
    }

    static public void saveTrimmedScreenshot(File target, int width, int height)
    {
        saveTrimmedScreenshot(target, 0, 0, width, height);
    }

    static public void saveTrimmedScreenshot(File target, int x, int y, int width, int height)
    {
        saveTrimmedScreenshot(target, x, y, width, height, null);
    }

    static public void saveTrimmedScreenshot(File target, int x, int y, int width, int height, Color transparencyColor)
    {
        saveScreenshot(target);

        try
        {
            BufferedImage bufferedImage = ImageIO.read(target).getSubimage(x, y, width, height);
            if (transparencyColor != null)
            {
                bufferedImage = makeColorTransparent(bufferedImage, transparencyColor);
            }
            ImageIO.write(bufferedImage, "png", target);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    static protected BufferedImage makeColorTransparent(BufferedImage im, final Color color)
    {
        ImageFilter filter = new RGBImageFilter() {

            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFF000000;

            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                } else {
                    // nothing to do
                    return rgb;
                }
            }
        };

        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return imageToBufferedImage(Toolkit.getDefaultToolkit().createImage(ip));
    }

    static protected BufferedImage imageToBufferedImage(Image image) {

        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return bufferedImage;

    }

    static protected void moveImageTo(File moveTo)
    {
        if (moveTo.exists())
        {
            if (!moveTo.delete())
            {
                System.err.println("Couldn't delete already existing " + moveTo.getPath());
            }
        }
        File[] files = getScreenshots();
        if (files == null)
        {
            System.err.println("no files!");
        }
        else
        {
            if (files.length != 1)
            {
                System.err.println("more than one file!");
            }
            if (!files[0].renameTo(moveTo))
            {
                System.err.println("renameTo from " + files[0].getPath() + " to " + moveTo.getPath() + " returned false");
            }
        }
    }

    static protected File[] getScreenshots()
    {
        File f = new File(Generate.screenshotsDirectory);
        File[] files = f.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.toLowerCase().endsWith(".png");
            }
        });
        return files == null ? new File[0] : files;
    }

    /**
     * Converts any string into a string that is safe to use as a file name.
     * The result will only include ascii characters and numbers, and the "-","_", and "." characters.
     *
     * @param name name to encode
     * @param dirSeparators whether to leave dir separators untouched
     * @param maxFileLength trim name to given length
     * @return encoded name
     */
    public static String toFileSystemSafeName(String name, boolean dirSeparators, int maxFileLength)
    {
        int size = name.length();
        StringBuilder stringBuilder = new StringBuilder(size * 2);
        for (int i = 0; i < size; i++)
        {
            char c = name.charAt(i);
            boolean valid = c >= 'a' && c <= 'z';
            valid = valid || (c >= 'A' && c <= 'Z');
            valid = valid || (c >= '0' && c <= '9');
            valid = valid || (c == '_') || (c == '-') || (c == '.') || (c == '#')
                    || (dirSeparators && ((c == '/') || (c == '\\')));

            if (valid)
            {
                stringBuilder.append(c);
            }
            else if (c == ' ')
            {
                stringBuilder.append('_');
            }
            else
            {
                // Encode the character using hex notation
                stringBuilder.append('#');
                stringBuilder.append(HexUtils.toHexFromInt(c, true));
            }
        }
        String result = stringBuilder.toString();
        if (result.length() > maxFileLength)
        {
            result = result.substring(result.length() - maxFileLength, result.length());
        }
        return result;
    }
}
