package net.pbreadman.xcweaponry.gui.overlays;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pbreadman.xcweaponry.network.ArtPayload;

@OnlyIn(Dist.CLIENT)
public class ArtWheel extends Screen {
    public ArtWheel() {
        super(Component.literal("Select Art"));
    }
    @Override
    protected void init() {
        int buttonWidth = 120;
        int buttonHeight = 20;
        int startX = this.width / 2 - buttonWidth / 2;
        int startY = this.height / 2 - (4 * buttonHeight);

        String[] arts = {"Buster", "Enchant", "Shield", "Speed", "Purge", "Cyclone", "Eater", "Armour"};

        for (int index = 0; index < 8; index++) {
            int currentArtSlot = index;
            this.addRenderableWidget(Button.builder(Component.literal(arts[index]), button -> {
                PacketDistributor.sendToServer(new ArtPayload(currentArtSlot));
                this.onClose();
            }).bounds(startX, startY + (index * 22), buttonWidth, buttonHeight).build());
        }
    }
    @Override
public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 100, 0xFFFFFF);
    }
    @Override
    public boolean isPauseScreen() {return false;}
}
