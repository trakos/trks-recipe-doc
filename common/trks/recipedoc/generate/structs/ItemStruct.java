package trks.recipedoc.generate.structs;

import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStruct
{
    public int id;
    public int damage;
    public String name;
    public List<String> tooltipDescription = new ArrayList<String>();
    public String description;
    public Map<String, String> attributes = new HashMap<String, String>();
    public String icon;
    public String mod;
    public String type;
}
