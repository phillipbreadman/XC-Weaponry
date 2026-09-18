package net.pbreadman.xcweaponry.event;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.pbreadman.xcweaponry.ModConstants;
import net.pbreadman.xcweaponry.items.MonadoArt;
import net.pbreadman.xcweaponry.items.MonadoBase;
import net.pbreadman.xcweaponry.items.custom.ModDataComponents;

public class MonadoAttackHandler {
    @SubscribeEvent
    public static void applyArmedOnHitArts(LivingIncomingDamageEvent event) {
        if (event.isCanceled()) {
            return;
        }
        DamageSource source = event.getSource();
        if (!source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
            return;
        }
        Entity direct = source.getDirectEntity();
        if (!(direct instanceof Player attacker)) {
            return;
        }
        ItemStack stack = attacker.getMainHandItem();
        if (!(stack.getItem() instanceof MonadoBase)) {
            return;
        }
        Long until = stack.get(ModDataComponents.ART_READY_UNTIL.get());
        if (until == null || until <= attacker.level().getGameTime()) {
            return;
        }
        MonadoArt art = MonadoBase.getSelectedArt(stack);
        if (art == MonadoArt.BUSTER) {
            event.setAmount(event.getAmount() * ModConstants.BUSTER_DAMAGE_MULTIPLIER);
        } else if (art == MonadoArt.ENCHANT) {
            Registry<DamageType> registry = attacker.level().registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE);
            Holder.Reference<DamageType> holder = registry.getHolderOrThrow(MonadoArt.ENCHANT_DAMAGE_TYPE);
            event.setCanceled(true);
            event.getEntity().hurt(new DamageSource(holder, attacker, attacker), event.getAmount());
        }
    }
}