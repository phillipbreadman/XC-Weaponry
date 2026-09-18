package net.pbreadman.xcweaponry.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.pbreadman.xcweaponry.ModConstants;

public class ModToolTiers {
    public static final Tier MONADO = new SimpleTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            ModConstants.MONADO_TIER_DURABILITY,
            ModConstants.MONADO_TIER_ATTACK_SPEED,
            ModConstants.MONADO_TIER_ATTACK_DAMAGE_BONUS,
            ModConstants.MONADO_TIER_ENCHANTMENT_VALUE,
            () -> Ingredient.of(Items.AMETHYST_SHARD)
    );
}
