package net.pbreadman.xcweaponry.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.pbreadman.xcweaponry.util.ModTags;

public class ModToolTiers {
    public static final Tier MONADO = new SimpleTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            0,
            8.0f,
            4.0f,
            25,
            () -> Ingredient.of(Items.AMETHYST_SHARD)
    );
}
