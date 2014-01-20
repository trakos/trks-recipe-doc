package trks.recipedoc.generate;

import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStruct
{
    public int id;
    public String name;
    public List<String> tooltipDescription = new ArrayList<String>();
    public String description;
    public Multimap<String, String> attributes;
}
