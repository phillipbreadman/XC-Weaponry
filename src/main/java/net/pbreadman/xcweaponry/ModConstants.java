package net.pbreadman.xcweaponry;

import java.util.EnumMap;
import java.util.Map;
import net.pbreadman.xcweaponry.items.MonadoArt;

/**
 Central home for all tunable gameplay variables. Every magic number in the
 codebase should live here. Sections are grouped by feature and each header
 states which file(s) pull from it.
 */
public final class ModConstants {
    private ModConstants() {
    }

    // MONADO TOOL TIER (ModToolTiers.java)

    // MonadoTier durability.
    public static final int MONADO_TIER_DURABILITY = 0;
    // MonadoTier attack speed.
    public static final float MONADO_TIER_ATTACK_SPEED = 8.0f;
    // MonadoTier attack damage bonus.
    public static final float MONADO_TIER_ATTACK_DAMAGE_BONUS = 4.0f;
    // MonadoTier enchantability.
    public static final int MONADO_TIER_ENCHANTMENT_VALUE = 0;

    // ART ACTIVATION WINDOW (MonadoArt.java, MonadoBase.java)
    // How many ticks an on-hit art stays armed after right-clicking.
    public static final int ART_ACTIVE_WINDOW_TICKS = 200;

    // ============================================================================
    // SHIELD ART (MonadoArt.java)
    // ============================================================================
    public static final int SHIELD_ABSORPTION_DURATION_TICKS = 2400;
    public static final int SHIELD_ABSORPTION_AMPLIFIER = 4;

    // ============================================================================
    // ARMOUR ART (MonadoArt.java)
    // ============================================================================
    public static final int ARMOUR_RESISTANCE_DURATION_TICKS = 2400;
    public static final int ARMOUR_RESISTANCE_AMPLIFIER = 2;
    public static final int ARMOUR_FIRE_RESISTANCE_DURATION_TICKS = 2400;
    public static final int ARMOUR_FIRE_RESISTANCE_AMPLIFIER = 1;

    // ============================================================================
    // SPEED ART (MonadoArt.java)
    // ============================================================================
    public static final int SPEED_EFFECT_DURATION_TICKS = 2400;
    public static final int SPEED_EFFECT_AMPLIFIER = 1;

    // ============================================================================
    // ENCHANT ART (MonadoArt.java, MonadoAttackHandler.java)
    // ============================================================================
    // Strength buff duration granted on arming (also ART_ACTIVE_WINDOW_TICKS).
    public static final int ENCHANT_STRENGTH_DURATION_TICKS = ART_ACTIVE_WINDOW_TICKS;
    // Strength buff amplifier.
    public static final int ENCHANT_STRENGTH_AMPLIFIER = 0;

    // ============================================================================
    // BUSTER ART (MonadoAttackHandler.java)
    // ============================================================================
    // Multiplies damage.
    public static final float BUSTER_DAMAGE_MULTIPLIER = 2.5f;

    // ============================================================================
    // EATER ART (MonadoArt.java)
    // ============================================================================
    public static final int EATER_WITHER_DURATION_TICKS = 100;
    public static final int EATER_WITHER_AMPLIFIER = 1;
    public static final int EATER_REGENERATION_DURATION_TICKS = 100;
    public static final int EATER_REGENERATION_AMPLIFIER = 0;

    // ============================================================================
    // CYCLONE ART (MonadoArt.java)
    // ============================================================================
    // Radius (blocks) around the player that Cyclone hits entities from.
    public static final double CYCLONE_RADIUS = 6.0D;
    // Minimum horizontal knockback strength (at max distance).
    public static final double CYCLONE_KNOCKBACK_BASE = 0.9D;
    // Knockback strength gained per unit rad= distance from the player.
    public static final double CYCLONE_KNOCKBACK_SCALE = 2.25D;
    // Upward knockback on flung entities.
    public static final double CYCLONE_KNOCKBACK_UPWARD = 0.8D;

    // ============================================================================
    // CYCLONE TOPPLE (MonadoArt.java)
    // ============================================================================
    public static final int TOPPLE_DURATION_TICKS = 180;

    // ============================================================================
    // ART WHEEL GUI (ArtWheel.java)
    // ============================================================================
    // Outer radius of the wheel ring in pixels.
    public static final double ART_WHEEL_OUTER_RADIUS = 120.0D;
    // Inner radius (center disc) of the wheel ring in pixels.
    public static final double ART_WHEEL_INNER_RADIUS = 44.0D;
    // Draw segments per slice (smoothness of the ring).
    public static final int ART_WHEEL_SEGMENTS_PER_SLICE = 18;
    // Angle where the first slice starts (top of the wheel).
    public static final double ART_WHEEL_START_ANGLE = -Math.PI / 2.0D;
    // Segments used to draw the solid center disc.
    public static final int ART_WHEEL_CENTER_DISC_SEGMENTS = 32;
    // Pixels above the wheel where the title is drawn.
    public static final int ART_WHEEL_TITLE_Y_OFFSET = 24;
    // How much a slice's colour is brightened when hovered.
    public static final int ART_WHEEL_BRIGHTEN_AMOUNT = 60;
    // Slice background colour (ARGB).
    public static final int ART_WHEEL_SLICE_BACKGROUND_COLOR = 0x1AAAAAAA;
    // Radius of the light-gray background disc behind the wheel (pixels).
    public static final double ART_WHEEL_BACKGROUND_RADIUS = 132.0D;
    // Segments used to draw the background disc.
    public static final int ART_WHEEL_BACKGROUND_DISC_SEGMENTS = 64;
    // Background disc colour (ARGB, translucent light gray).
    public static final int ART_WHEEL_BACKGROUND_COLOR = 0x33AAAAAA;
    // Separator line colour between art slices (ARGB, translucent).
    public static final int ART_WHEEL_SEPARATOR_COLOR = 0xAA444444;
    // Half-width of separator lines (pixels).
    public static final double ART_WHEEL_SEPARATOR_WIDTH = 1.0D;
    // Centre disc colour (ARGB).
    public static final int ART_WHEEL_CENTER_COLOR = 0x1A1F2430;
    // Centre disc colour when hovered (ARGB).
    public static final int ART_WHEEL_CENTER_HOVER_COLOR = 0x1A3A4152;
    // Slice label name colours keyed by art (ARGB).
    public static final Map<MonadoArt, Integer> ART_WHEEL_LABEL_COLORS = new EnumMap<>(MonadoArt.class);

    static {
        ART_WHEEL_LABEL_COLORS.put(MonadoArt.SHIELD, 0xFFFFEF00);
        ART_WHEEL_LABEL_COLORS.put(MonadoArt.ARMOUR, 0xFFFF9A00);
        ART_WHEEL_LABEL_COLORS.put(MonadoArt.SPEED, 0xFF0FBED3);
        ART_WHEEL_LABEL_COLORS.put(MonadoArt.ENCHANT, 0xFF9F00FF);
        ART_WHEEL_LABEL_COLORS.put(MonadoArt.BUSTER, 0xFF000BFF);
        ART_WHEEL_LABEL_COLORS.put(MonadoArt.CYCLONE, 0xFFFFFFFF);
        ART_WHEEL_LABEL_COLORS.put(MonadoArt.EATER, 0xFF000000);
        ART_WHEEL_LABEL_COLORS.put(MonadoArt.PURGE, 0xFF1BFF00);
    }

    // Fallback label colour for any art not keyed above (ARGB).
    public static final int ART_WHEEL_LABEL_DEFAULT_COLOR = 0xFF00CBBE;
    // "Reset selection" text colour (ARGB).
    public static final int ART_WHEEL_RESET_COLOR = 0xFFAAAAAA;
}