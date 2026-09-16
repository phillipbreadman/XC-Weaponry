package net.pbreadman.xcweaponry.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.util.Unit;
import net.minecraft.world.item.component.Unbreakable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pbreadman.xcweaponry.XCWeaponry;
import net.pbreadman.xcweaponry.items.custom.ModDataComponents;
import net.pbreadman.xcweaponry.items.custom.MonadoTemplate;
import net.pbreadman.xcweaponry.items.custom.UseableItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(XCWeaponry.MOD_ID);

    //enchant and buster should start on it
    public static final DeferredItem<SwordItem> MONADO = ITEMS.register("monado",
            () -> new MonadoBase(ModToolTiers.MONADO, new Item
                    .Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.MONADO, 3, -2.4f))
                    .fireResistant()
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
                    .component(ModDataComponents.UNLOCKED_ENCHANT.get(), Unit.INSTANCE)
                    .component(ModDataComponents.UNLOCKED_BUSTER.get(), Unit.INSTANCE)
                   ));

    public static final DeferredItem<Item> MONADO_ART_TEMPLATE = ITEMS.register("monado_art_template",
            MonadoTemplate::createMonadoTemplate);

    public static final DeferredItem<Item> ART_SHIELD = ITEMS.register("art_shield",
            () -> new Item(new Item.Properties()
                    .component(ModDataComponents.UNLOCKS_COMPONENT.get(), ResourceLocation.fromNamespaceAndPath(XCWeaponry.MOD_ID, "unlocked_shield"))));

    public static final DeferredItem<Item> ART_ARMOUR = ITEMS.register("art_armour",
            () -> new Item(new Item.Properties()
                    .component(ModDataComponents.UNLOCKS_COMPONENT.get(), ResourceLocation.fromNamespaceAndPath(XCWeaponry.MOD_ID, "unlocked_armour"))));

    public static final DeferredItem<Item> ART_SPEED = ITEMS.register("art_speed",
            () -> new Item(new Item.Properties()
                    .component(ModDataComponents.UNLOCKS_COMPONENT.get(), ResourceLocation.fromNamespaceAndPath(XCWeaponry.MOD_ID, "unlocked_speed"))));

    public static final DeferredItem<Item> ART_ENCHANT = ITEMS.register("art_enchant",
            () -> new Item(new Item.Properties()
                    .component(ModDataComponents.UNLOCKS_COMPONENT.get(), ResourceLocation.fromNamespaceAndPath(XCWeaponry.MOD_ID, "unlocked_enchant"))));

    public static final DeferredItem<Item> ART_BUSTER = ITEMS.register("art_buster",
            () -> new Item(new Item.Properties()
                    .component(ModDataComponents.UNLOCKS_COMPONENT.get(), ResourceLocation.fromNamespaceAndPath(XCWeaponry.MOD_ID, "unlocked_buster"))));

    public static final DeferredItem<Item> ART_CYCLONE = ITEMS.register("art_cyclone",
            () -> new Item(new Item.Properties()
                    .component(ModDataComponents.UNLOCKS_COMPONENT.get(), ResourceLocation.fromNamespaceAndPath(XCWeaponry.MOD_ID, "unlocked_cyclone"))));

    public static final DeferredItem<Item> ART_EATER = ITEMS.register("art_eater",
            () -> new Item(new Item.Properties()
                    .component(ModDataComponents.UNLOCKS_COMPONENT.get(), ResourceLocation.fromNamespaceAndPath(XCWeaponry.MOD_ID, "unlocked_eater"))));

    public static final DeferredItem<Item> ART_PURGE = ITEMS.register("art_purge",
            () -> new Item(new Item.Properties()
                    .component(ModDataComponents.UNLOCKS_COMPONENT.get(), ResourceLocation.fromNamespaceAndPath(XCWeaponry.MOD_ID, "unlocked_purge"))));

    public static final DeferredItem<Item> ETHER = ITEMS.register("ether",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ONTOS = ITEMS.register("ontos",
            () -> new UseableItem(new Item.Properties()));

    public static final DeferredItem<Item> CORE_CRYSTAL = ITEMS.register("core_crystal",
            () -> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}