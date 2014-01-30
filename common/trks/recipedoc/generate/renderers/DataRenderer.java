package trks.recipedoc.generate.renderers;

import trks.recipedoc.generate.loaders.DataNEIFetcher;
import trks.recipedoc.generate.renderers.utils.RecipeBackgroundRenderer;
import trks.recipedoc.generate.structs.ItemStruct;

import java.util.Collection;

public class DataRenderer
{

    static public void render(Collection<ItemStruct> items)
    {
        //IconRenderer.renderItems(items);
        RecipeBackgroundRenderer.renderAll(DataNEIFetcher.getCraftingHandlers());
    }
}
