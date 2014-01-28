package trks.recipedoc.generate.structs;

public class IdDamagePair implements Comparable<IdDamagePair>, Cloneable
{
    public int itemId;
    public int damageId;

    public IdDamagePair(int itemId, int damageId)
    {
        this.itemId = itemId;
        this.damageId = damageId;
    }

    @Override
    public IdDamagePair clone()
    {
        return new IdDamagePair(this.itemId, this.damageId);
    }

    @Override
    public boolean equals(Object o)
    {
        return this == o
               || o instanceof IdDamagePair && damageId == ((IdDamagePair) o).damageId && itemId == ((IdDamagePair) o).itemId;
    }

    @Override
    public int hashCode()
    {
        int result = itemId;
        result = 31 * result + damageId;
        return result;
    }

    @Override
    public int compareTo(IdDamagePair o)
    {
        int compared = Integer.compare(itemId, o.itemId);
        if (compared == 0)
        {
            compared = Integer.compare(damageId, o.damageId);
        }
        return compared;
    }
}
