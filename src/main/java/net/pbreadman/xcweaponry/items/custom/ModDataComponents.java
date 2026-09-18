package net.pbreadman.xcweaponry.items.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pbreadman.xcweaponry.XCWeaponry;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(XCWeaponry.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> UNLOCKED_ENCHANT =
            DATA_COMPONENTS.registerComponentType("unlocked_enchant", b -> b.persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> UNLOCKED_BUSTER =
            DATA_COMPONENTS.registerComponentType("unlocked_buster", b -> b.persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> UNLOCKED_SHIELD =
            DATA_COMPONENTS.registerComponentType("unlocked_shield", b -> b.persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> UNLOCKED_ARMOUR =
            DATA_COMPONENTS.registerComponentType("unlocked_armour", b -> b.persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> UNLOCKED_SPEED =
            DATA_COMPONENTS.registerComponentType("unlocked_speed", b -> b.persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> UNLOCKED_CYCLONE =
            DATA_COMPONENTS.registerComponentType("unlocked_cyclone", b -> b.persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> UNLOCKED_EATER =
            DATA_COMPONENTS.registerComponentType("unlocked_eater", b -> b.persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> UNLOCKED_PURGE =
            DATA_COMPONENTS.registerComponentType("unlocked_purge", b -> b.persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> UNLOCKS_COMPONENT =
            DATA_COMPONENTS.registerComponentType("unlocks_component", b -> b.persistent(ResourceLocation.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> SELECTED_ART =
            DATA_COMPONENTS.registerComponentType("selected_art", b -> b.persistent(ResourceLocation.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> ART_READY_UNTIL =
            DATA_COMPONENTS.registerComponentType("art_ready_until", b -> b.persistent(Codec.LONG));
}