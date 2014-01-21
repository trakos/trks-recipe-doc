package trks.recipedoc.generate.structs;

/**
 * Created by trakos on 20.01.14.
 */
public class RecipeTypeStruct
{
    public String typeId;
    public String name;
    public String image;

    public RecipeTypeStruct(String typeId, String name, String image)
    {
        this.typeId = typeId;
        this.name = name;
        this.image = image;
    }
}
