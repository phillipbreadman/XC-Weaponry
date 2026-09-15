package net.pbreadman.xcweaponry;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = XCWeaponry.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = XCWeaponry.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class XCWeaponryClient {
    public XCWeaponryClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        XCWeaponry.LOGGER.info("XCWeaponry is loaded on the client");
    }
}
