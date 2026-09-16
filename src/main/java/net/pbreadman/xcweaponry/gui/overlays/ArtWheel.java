package net.pbreadman.xcweaponry.gui.overlays;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pbreadman.xcweaponry.items.MonadoArt;
import net.pbreadman.xcweaponry.network.ArtPayload;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

@OnlyIn(Dist.CLIENT)
public class ArtWheel extends Screen {
    private static final double OUTER_RADIUS = 120.0D;
    private static final double INNER_RADIUS = 44.0D;
    private static final int SEGMENTS_PER_SLICE = 18;
    private static final double START_ANGLE = -Math.PI / 2.0D;
    private static final int[] SLICE_COLORS = {
            0xFFE74C3C, 0xFFE67E22, 0xFFF1C40F, 0xFF2ECC71,
            0xFF1ABC9C, 0xFF3498DB, 0xFF9B59B6, 0xFFE84393
    };
    private static final int CENTER_COLOR = 0xFF1F2430;
    private static final int CENTER_HOVER_COLOR = 0xFF3A4152;

    private final List<MonadoArt> unlocked;
    private int hovered = -1;
    private double lastMouseX = Integer.MIN_VALUE;
    private double lastMouseY = Integer.MIN_VALUE;

    public ArtWheel() {
        super(Component.translatable("gui.xcweaponry.artwheel"));
        this.unlocked = getUnlockedArts();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;

        int cx = this.width / 2;
        int cy = this.height / 2;
        this.hovered = -1;

        if (this.unlocked.isEmpty()) {
            graphics.drawCenteredString(this.font,
                    Component.translatable("gui.xcweaponry.artwheel.no_arts"),
                    cx, cy - 8, 0xAAAAAA);
            return;
        }

        double dx = mouseX - cx;
        double dy = mouseY - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist <= OUTER_RADIUS && dist > INNER_RADIUS) {
            this.hovered = angleToIndex(Math.atan2(dy, dx));
        }

        drawSlices(graphics, cx, cy);
        drawLabels(graphics, cx, cy);
        drawCenter(graphics, cx, cy, dist <= INNER_RADIUS);
        graphics.drawCenteredString(this.font, this.title, cx, (int) (cy - OUTER_RADIUS - 24), 0xFFFFFF);
    }

    /**
     * Called when the art wheel key is released. Selects the art (or reset) under
     * the cursor, or closes without changing if the cursor is outside the wheel.
     */
    public void commit() {
        commitAt(this.lastMouseX, this.lastMouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            commitAt(mouseX, mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void commitAt(double x, double y) {
        if (x == Integer.MIN_VALUE || this.unlocked.isEmpty()) {
            this.onClose();
            return;
        }
        int cx = this.width / 2;
        int cy = this.height / 2;
        double dx = x - cx;
        double dy = y - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist <= OUTER_RADIUS) {
            if (dist <= INNER_RADIUS) {
                select("");
            } else {
                int index = angleToIndex(Math.atan2(dy, dx));
                if (index >= 0) {
                    select(this.unlocked.get(index).getName());
                } else {
                    this.onClose();
                }
            }
        } else {
            this.onClose();
        }
    }

    private void select(String artName) {
        PacketDistributor.sendToServer(new ArtPayload(artName));
        this.onClose();
    }

    private void drawSlices(GuiGraphics graphics, int cx, int cy) {
        int count = this.unlocked.size();
        double sliceAngle = Math.PI * 2.0D / count;

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < count; i++) {
            double a0 = START_ANGLE + i * sliceAngle;
            int color = i == this.hovered ? brighten(this.unlocked.get(i)) : SLICE_COLORS[i % SLICE_COLORS.length];
            for (int s = 0; s < SEGMENTS_PER_SLICE; s++) {
                double t0 = a0 + sliceAngle * s / SEGMENTS_PER_SLICE;
                double t1 = a0 + sliceAngle * (s + 1) / SEGMENTS_PER_SLICE;
                vertex(builder, cx, cy, INNER_RADIUS, t0, color);
                vertex(builder, cx, cy, OUTER_RADIUS, t0, color);
                vertex(builder, cx, cy, OUTER_RADIUS, t1, color);
                vertex(builder, cx, cy, INNER_RADIUS, t1, color);
            }
        }
        MeshData mesh = builder.build();
        BufferUploader.drawWithShader(mesh);
        mesh.close();

        RenderSystem.disableBlend();
    }

    private void drawLabels(GuiGraphics graphics, int cx, int cy) {
        int count = this.unlocked.size();
        double sliceAngle = Math.PI * 2.0D / count;
        double midRadius = (OUTER_RADIUS + INNER_RADIUS) * 0.5D;
        for (int i = 0; i < count; i++) {
            double mid = START_ANGLE + (i + 0.5D) * sliceAngle;
            float x = (float) (cx + midRadius * Math.cos(mid));
            float y = (float) (cy + midRadius * Math.sin(mid));
            int color = i == this.hovered ? 0xFFF4D03F : 0xFFFFFFFF;
            graphics.drawCenteredString(this.font, capitalize(this.unlocked.get(i).getName()),
                    (int) x, (int) (y - this.font.lineHeight / 2.0F), color);
        }
    }

    private void drawCenter(GuiGraphics graphics, int cx, int cy, boolean hovered) {
        int color = hovered ? CENTER_HOVER_COLOR : CENTER_COLOR;

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        int a = color >>> 24;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        builder.addVertex(cx, cy, 0.0F).setColor(r, g, b, a);
        for (int s = 0; s <= 32; s++) {
            double t = Math.PI * 2.0D * s / 32;
            float x = (float) (cx + INNER_RADIUS * Math.cos(t));
            float y = (float) (cy + INNER_RADIUS * Math.sin(t));
            builder.addVertex(x, y, 0.0F).setColor(r, g, b, a);
        }
        MeshData mesh = builder.build();
        BufferUploader.drawWithShader(mesh);
        mesh.close();
        RenderSystem.disableBlend();

        Component reset = Component.translatable("gui.xcweaponry.artwheel.reset");
        graphics.drawCenteredString(this.font, reset, cx, (int) (cy - this.font.lineHeight / 2.0F), 0xFFAAAAAA);
    }

    private int angleToIndex(double angle) {
        int count = this.unlocked.size();
        if (count <= 0) {
            return -1;
        }
        double relative = angle - START_ANGLE;
        while (relative < 0.0D) {
            relative += Math.PI * 2.0D;
        }
        while (relative >= Math.PI * 2.0D) {
            relative -= Math.PI * 2.0D;
        }
        int index = (int) (relative / (Math.PI * 2.0D / count));
        return index >= count ? count - 1 : index;
    }

    private static int brighten(MonadoArt art) {
        int base = SLICE_COLORS[art.ordinal() % SLICE_COLORS.length];
        int r = Math.min(255, ((base >> 16) & 0xFF) + 60);
        int g = Math.min(255, ((base >> 8) & 0xFF) + 60);
        int b = Math.min(255, (base & 0xFF) + 60);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static void vertex(BufferBuilder builder, int cx, int cy, double radius, double angle, int color) {
        float x = (float) (cx + radius * Math.cos(angle));
        float y = (float) (cy + radius * Math.sin(angle));
        int a = color >>> 24;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        builder.addVertex(x, y, 0.0F).setColor(r, g, b, a);
    }

    private static List<MonadoArt> getUnlockedArts() {
        List<MonadoArt> unlocked = new ArrayList<>();
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return unlocked;
        }
        ItemStack held = player.getMainHandItem();
        for (MonadoArt art : MonadoArt.values()) {
            if (held.has(art.getUnlockHolder().get())) {
                unlocked.add(art);
            }
        }
        return unlocked;
    }

    private static String capitalize(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}