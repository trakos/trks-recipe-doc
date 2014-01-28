package trks.recipedoc.generate.structs;

import net.minecraft.item.ItemStack;

public class IdDamagePairWithStack extends IdDamagePair
{
    public IdDamagePairWithStack(int itemId, int damageId, ItemStack itemStack)
    {
        super(itemId, damageId);
        this.itemStack = itemStack;
    }
    public ItemStack getItemStack()
    {
        return itemStack;
    }
    protected final ItemStack itemStack;
}
