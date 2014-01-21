package trks.recipedoc.generate.renderers;

import trks.recipedoc.generate.loaders.DataNEIFetcher;
import trks.recipedoc.generate.renderers.utils.IconRenderer;
import trks.recipedoc.generate.renderers.utils.RecipeBackgroundRenderer;

import java.io.File;

public class DataRenderer
{

    static public void render()
    {
        IconRenderer.renderItems(DataNEIFetcher.getItems());
        RecipeBackgroundRenderer.renderAll(DataNEIFetcher.getCraftingHandlers());
    }
}
