package net.pbreadman.xcweaponry;


import net.neoforged.neoforge.common.NeoForge;
import net.pbreadman.xcweaponry.command.ModCommands;
import net.pbreadman.xcweaponry.event.ModEvents;
import net.pbreadman.xcweaponry.event.MonadoAttackHandler;
import net.pbreadman.xcweaponry.items.ModItems;
import net.pbreadman.xcweaponry.items.custom.ModDataComponents;
import net.pbreadman.xcweaponry.recipe.ModRecipeSerializers;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.pbreadman.xcweaponry.network.ArtPayload;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(XCWeaponry.MOD_ID)
public class XCWeaponry {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "xcweaponry";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public XCWeaponry(IEventBus modEventBus, ModContainer modContainer) {
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModItems.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);

        NeoForge.EVENT_BUS.register(ModEvents.class);
        NeoForge.EVENT_BUS.register(ModCommands.class);
        NeoForge.EVENT_BUS.register(MonadoAttackHandler.class);

        modEventBus.addListener(this::registerPayloads);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MOD_ID);
        registrar.playToServer(ArtPayload.TYPE, ArtPayload.STREAM_CODEC, ArtPayload::handle);
    }
}
