package net.pbreadman.xcweaponry.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.pbreadman.xcweaponry.items.custom.ModDataComponents;

public class MonadoBase extends SwordItem {
    public MonadoBase(Tier tier, Properties properties) {
        super(tier, properties);
    }

    public static MonadoArt getSelectedArt(ItemStack stack) {
        ResourceLocation unlockId = stack.get(ModDataComponents.SELECTED_ART.get());
        return unlockId == null ? null : MonadoArt.fromUnlockId(unlockId);
    }

    public static void selectArt(ItemStack stack, MonadoArt art) {
        if (art == null) {
            stack.remove(ModDataComponents.SELECTED_ART.get());
            stack.remove(DataComponents.CUSTOM_MODEL_DATA);
        } else if (stack.has(art.getUnlockHolder().get())) {
            stack.set(ModDataComponents.SELECTED_ART.get(), art.getUnlockId());
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(art.getModelIndex()));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        MonadoArt art = getSelectedArt(itemStack);
        if (art != null && art.hasUseAction()) {
            if (!level.isClientSide) {
                art.applyOnUse(player);
            }
            return InteractionResultHolder.success(itemStack);
        }
        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
        MonadoArt art = getSelectedArt(stack);
        if (art != null && !attacker.level().isClientSide) {
            art.applyOnHit(attacker, target);
        }
    }
}