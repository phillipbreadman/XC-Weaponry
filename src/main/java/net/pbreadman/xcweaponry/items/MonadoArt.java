package net.pbreadman.xcweaponry.items;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.pbreadman.xcweaponry.XCWeaponry;
import net.pbreadman.xcweaponry.ModConstants;
import net.pbreadman.xcweaponry.items.custom.ModDataComponents;

public enum MonadoArt {
    SHIELD("shield", ModDataComponents.UNLOCKED_SHIELD, 1),
    ARMOUR("armour", ModDataComponents.UNLOCKED_ARMOUR, 2),
    SPEED("speed", ModDataComponents.UNLOCKED_SPEED, 3),
    ENCHANT("enchant", ModDataComponents.UNLOCKED_ENCHANT, 4),
    BUSTER("buster", ModDataComponents.UNLOCKED_BUSTER, 5),
    CYCLONE("cyclone", ModDataComponents.UNLOCKED_CYCLONE, 6),
    EATER("eater", ModDataComponents.UNLOCKED_EATER, 7),
    PURGE("purge", ModDataComponents.UNLOCKED_PURGE, 8);

    private static final Map<ResourceLocation, MonadoArt> BY_UNLOCK_ID = Arrays.stream(values())
            .collect(Collectors.toMap(MonadoArt::getUnlockId, Function.identity()));
    private static final Map<String, MonadoArt> BY_NAME = Arrays.stream(values())
            .collect(Collectors.toMap(MonadoArt::getName, Function.identity()));

    public static final ResourceKey<DamageType> ENCHANT_DAMAGE_TYPE = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(XCWeaponry.MOD_ID, "monado_enchant"));
    private static final String PREV_NO_AI_TAG = "xcweaponry_prev_noai";

    private final String name;
    private final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> unlockHolder;
    private final int modelIndex;

    MonadoArt(String name, DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> unlockHolder, int modelIndex) {
        this.name = name;
        this.unlockHolder = unlockHolder;
        this.modelIndex = modelIndex;
    }

    public String getName() {
        return this.name;
    }

    public DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> getUnlockHolder() {
        return this.unlockHolder;
    }

    public ResourceLocation getUnlockId() {
        return this.unlockHolder.getId();
    }

    public int getModelIndex() {
        return this.modelIndex;
    }

    public static MonadoArt fromUnlockId(ResourceLocation unlockId) {
        return BY_UNLOCK_ID.get(unlockId);
    }

    public static MonadoArt fromName(String name) {
        return BY_NAME.get(name.toLowerCase(Locale.ROOT));
    }

    public boolean hasUseAction() {
        return this == SHIELD || this == ARMOUR || this == SPEED || this == CYCLONE;
    }

    public boolean hasOnHitEffect() {
        return this == ENCHANT || this == BUSTER || this == EATER || this == PURGE;
    }

    public void applyOnArm(LivingEntity attacker) {
        if (this == ENCHANT) {
            attacker.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
                    ModConstants.ENCHANT_STRENGTH_DURATION_TICKS, ModConstants.ENCHANT_STRENGTH_AMPLIFIER));
        }
    }

    public void applyOnUse(Player player) {
        switch (this) {
            case SHIELD -> player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,
                    ModConstants.SHIELD_ABSORPTION_DURATION_TICKS, ModConstants.SHIELD_ABSORPTION_AMPLIFIER));
            case ARMOUR -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,
                        ModConstants.ARMOUR_RESISTANCE_DURATION_TICKS, ModConstants.ARMOUR_RESISTANCE_AMPLIFIER));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,
                        ModConstants.ARMOUR_FIRE_RESISTANCE_DURATION_TICKS, ModConstants.ARMOUR_FIRE_RESISTANCE_AMPLIFIER));
            }
            case SPEED -> {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,
                        ModConstants.SPEED_EFFECT_DURATION_TICKS, ModConstants.SPEED_EFFECT_AMPLIFIER));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,
                        ModConstants.SPEED_EFFECT_DURATION_TICKS, ModConstants.SPEED_EFFECT_AMPLIFIER));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP,
                        ModConstants.SPEED_EFFECT_DURATION_TICKS, ModConstants.SPEED_EFFECT_AMPLIFIER));
            }
            case CYCLONE -> knockbackAround(player);
            default -> {
            }
        }
    }

    public void applyOnHit(LivingEntity attacker, LivingEntity target) {
        switch (this) {
            case EATER -> {
                target.addEffect(new MobEffectInstance(MobEffects.WITHER,
                        ModConstants.EATER_WITHER_DURATION_TICKS, ModConstants.EATER_WITHER_AMPLIFIER));
                attacker.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                        ModConstants.EATER_REGENERATION_DURATION_TICKS, ModConstants.EATER_REGENERATION_AMPLIFIER));
            }
            case PURGE -> {
                target.getActiveEffectsMap().keySet().removeIf(effect -> effect.value().isBeneficial());
            }
            default -> {
            }
        }
    }

    private void knockbackAround(Player player) {
        Level level = player.level();
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(ModConstants.CYCLONE_RADIUS),
                entity -> entity.isAlive() && entity != player)) {
            double dx = entity.getX() - player.getX();
            double dz = entity.getZ() - player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist < 1.0E-4D) {
                dx = player.getRandom().nextDouble() - 0.5D;
                dz = player.getRandom().nextDouble() - 0.5D;
                dist = Math.max(1.0E-4D, Math.sqrt(dx * dx + dz * dz));
            }
            double strength = ModConstants.CYCLONE_KNOCKBACK_BASE
                    + ModConstants.CYCLONE_KNOCKBACK_SCALE * (1.0D - dist / ModConstants.CYCLONE_RADIUS);
            entity.setDeltaMovement(entity.getDeltaMovement().add(
                    dx / dist * strength, ModConstants.CYCLONE_KNOCKBACK_UPWARD, dz / dist * strength));
            entity.hurtMarked = true;
            if (entity instanceof Mob mob) {
                topple(mob);
            }
        }
    }

    private void topple(Mob mob) {
        boolean hadAi = !mob.isNoAi();
        mob.setNoAi(true);
        mob.getPersistentData().putBoolean(PREV_NO_AI_TAG, hadAi);
        mob.getNavigation().stop();
        mob.setTarget(null);
        XCWeaponry.LOGGER.debug("Toppling {}", mob.getType());
        MinecraftServer server = mob.level().getServer();
        if (server != null) {
            server.tell(new TickTask(server.getTickCount() + ModConstants.TOPPLE_DURATION_TICKS, () -> {
                if (mob.isAlive() && mob.getPersistentData().contains(PREV_NO_AI_TAG)) {
                    mob.setNoAi(!mob.getPersistentData().getBoolean(PREV_NO_AI_TAG));
                    mob.getPersistentData().remove(PREV_NO_AI_TAG);
                }
            }));
        }
    }
}