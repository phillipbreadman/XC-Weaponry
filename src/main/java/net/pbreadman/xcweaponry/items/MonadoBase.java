package net.pbreadman.xcweaponry.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class MonadoBase extends SwordItem {
    public MonadoBase(Tier tier, Properties properties) {
        super(tier, properties);
    }
    @Override
    public boolean isDamageable(ItemStack stack){
        return false;

    }
}
