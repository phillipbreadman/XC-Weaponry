package net.pbreadman.xcweaponry.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.pbreadman.xcweaponry.ModConstants;
import net.pbreadman.xcweaponry.XCWeaponry;
import net.pbreadman.xcweaponry.items.custom.ModDataComponents;

public class MonadoBase extends SwordItem {
    private static final ResourceLocation ART_BONUS_ID = ResourceLocation.fromNamespaceAndPath(XCWeaponry.MOD_ID, "art_damage_bonus");

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

    public static int getUnlockedArtCount(ItemStack stack) {
        int count = 0;
        for (MonadoArt art : MonadoArt.values()) {
            if (stack.has(art.getUnlockHolder().get())) {
                count++;
            }
        }
        return count;
    }

    public static ItemAttributeModifiers baseWithArtBonus(int artCount) {
        ItemAttributeModifiers modifiers = SwordItem.createAttributes(ModToolTiers.MONADO, 3, -2.4f);
        if (artCount > 0) {
            modifiers = modifiers.withModifierAdded(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(ART_BONUS_ID, artCount, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND);
        }
        return modifiers;
    }

    public static void syncArtDamageModifier(ItemStack stack) {
        ItemAttributeModifiers computed = baseWithArtBonus(getUnlockedArtCount(stack));
        ItemAttributeModifiers current = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (!computed.equals(current)) {
            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, computed);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        MonadoArt art = getSelectedArt(itemStack);
        if (art == null) {
            return InteractionResultHolder.pass(itemStack);
        }
        if (art.hasUseAction()) {
            if (!level.isClientSide) {
                art.applyOnUse(player);
            }
            return InteractionResultHolder.success(itemStack);
        }
        if (art.hasOnHitEffect()) {
            Long until = itemStack.get(ModDataComponents.ART_READY_UNTIL.get());
            if (level.isClientSide) {
                if (until != null && until > level.getGameTime()) {
                    return InteractionResultHolder.pass(itemStack);
                }
                return InteractionResultHolder.sidedSuccess(itemStack, false);
            }
            if (until == null || until <= level.getGameTime()) {
                itemStack.set(ModDataComponents.ART_READY_UNTIL.get(),
                        level.getGameTime() + ModConstants.ART_ACTIVE_WINDOW_TICKS);
                art.applyOnArm(player);
                player.displayClientMessage(
                        Component.translatable("message.xcweaponry.art_armed", art.getName()), true);
            }
            return InteractionResultHolder.sidedSuccess(itemStack, false);
        }
        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
        MonadoArt art = getSelectedArt(stack);
        if (art == null || attacker.level().isClientSide || !art.hasOnHitEffect()) {
            return;
        }
        Long until = stack.get(ModDataComponents.ART_READY_UNTIL.get());
        if (until != null && until > attacker.level().getGameTime()) {
            art.applyOnHit(attacker, target);
        }
    }
}