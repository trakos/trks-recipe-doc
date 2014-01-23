package trks.recipedoc.generate.structs;

public class RecipeTypeMachineIdStruct implements Comparable<RecipeTypeMachineIdStruct>
{

    public int itemId;
    public int damageId;

    public RecipeTypeMachineIdStruct(int itemId, int damageId)
    {
        this.itemId = itemId;
        this.damageId = damageId;
    }

    @Override
    public int compareTo(RecipeTypeMachineIdStruct o)
    {
        int compared = Integer.compare(itemId, o.itemId);
        if (compared == 0)
        {
            compared = Integer.compare(damageId, o.damageId);
        }
        return compared;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        else if (!(o instanceof RecipeTypeMachineIdStruct))
        {
            return false;
        }
        RecipeTypeMachineIdStruct that = (RecipeTypeMachineIdStruct) o;
        return damageId == that.damageId && itemId == that.itemId;

    }

    @Override
    public int hashCode()
    {
        int result = itemId;
        result = 31 * result + damageId;
        return result;
    }
}
