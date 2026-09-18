package net.pbreadman.xcweaponry.gui.overlays;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pbreadman.xcweaponry.ModConstants;
import net.pbreadman.xcweaponry.items.MonadoArt;
import net.pbreadman.xcweaponry.network.ArtPayload;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class ArtWheel extends Screen {
    private static final double TAU = Math.PI * 2.0D;

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

        double dx = mouseX - cx;
        double dy = mouseY - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist <= ModConstants.ART_WHEEL_OUTER_RADIUS && dist > ModConstants.ART_WHEEL_INNER_RADIUS) {
            this.hovered = angleToIndex(Math.atan2(dy, dx));
        }

        drawBackground(graphics, cx, cy);
        drawSlices(graphics, cx, cy);
        drawSeparators(graphics, cx, cy);
        drawLabels(graphics, cx, cy);
        drawCenter(graphics, cx, cy, dist <= ModConstants.ART_WHEEL_INNER_RADIUS);
        graphics.drawCenteredString(this.font, this.title, cx,
                (int) (cy - ModConstants.ART_WHEEL_OUTER_RADIUS - ModConstants.ART_WHEEL_TITLE_Y_OFFSET), 0xFFFFFF);
    }

    /**
       Called when the art wheel key is released. Selects the art (or reset) under
       the cursor, or closes without changing if the cursor is outside the wheel.
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
        if (dist <= ModConstants.ART_WHEEL_OUTER_RADIUS) {
            if (dist <= ModConstants.ART_WHEEL_INNER_RADIUS) {
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
        if (count <= 0) {
            return;
        }
        double sliceAngle = TAU / count;
        Matrix4f matrix = graphics.pose().last().pose();
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.debugQuads());
        for (int i = 0; i < count; i++) {
            double a0 = ModConstants.ART_WHEEL_START_ANGLE + i * sliceAngle;
            int color = i == this.hovered ? brighten(ModConstants.ART_WHEEL_SLICE_BACKGROUND_COLOR) : ModConstants.ART_WHEEL_SLICE_BACKGROUND_COLOR;
            for (int s = 0; s < ModConstants.ART_WHEEL_SEGMENTS_PER_SLICE; s++) {
                double t0 = a0 + sliceAngle * s / ModConstants.ART_WHEEL_SEGMENTS_PER_SLICE;
                double t1 = a0 + sliceAngle * (s + 1) / ModConstants.ART_WHEEL_SEGMENTS_PER_SLICE;
                quad(consumer, matrix, cx, cy, color, ModConstants.ART_WHEEL_INNER_RADIUS,
                        ModConstants.ART_WHEEL_OUTER_RADIUS, t0, t1);
            }
        }
        graphics.flush();
    }

    private void drawLabels(GuiGraphics graphics, int cx, int cy) {
        int count = this.unlocked.size();
        double sliceAngle = TAU / count;
        double midRadius = (ModConstants.ART_WHEEL_OUTER_RADIUS + ModConstants.ART_WHEEL_INNER_RADIUS) * 0.5D;
        for (int i = 0; i < count; i++) {
            double mid = ModConstants.ART_WHEEL_START_ANGLE + (i + 0.5D) * sliceAngle;
            float x = (float) (cx + midRadius * Math.cos(mid));
            float y = (float) (cy + midRadius * Math.sin(mid));
            int color = ModConstants.ART_WHEEL_LABEL_COLORS.getOrDefault(this.unlocked.get(i),
                    ModConstants.ART_WHEEL_LABEL_DEFAULT_COLOR);
            if (i == this.hovered) {
                color = brighten(color);
            }
            graphics.drawCenteredString(this.font, capitalize(this.unlocked.get(i).getName()),
                    (int) x, (int) (y - this.font.lineHeight / 2.0F), color);
        }
    }

    private void drawCenter(GuiGraphics graphics, int cx, int cy, boolean hovered) {
        int color = hovered ? ModConstants.ART_WHEEL_CENTER_HOVER_COLOR : ModConstants.ART_WHEEL_CENTER_COLOR;
        drawDisc(graphics, cx, cy, ModConstants.ART_WHEEL_INNER_RADIUS, color, ModConstants.ART_WHEEL_CENTER_DISC_SEGMENTS);

        Component reset = Component.translatable("gui.xcweaponry.artwheel.reset");
        graphics.drawCenteredString(this.font, reset, cx,
                (int) (cy - this.font.lineHeight / 2.0F), ModConstants.ART_WHEEL_RESET_COLOR);
    }

    private void drawBackground(GuiGraphics graphics, int cx, int cy) {
        drawDisc(graphics, cx, cy, ModConstants.ART_WHEEL_BACKGROUND_RADIUS,
                ModConstants.ART_WHEEL_BACKGROUND_COLOR, ModConstants.ART_WHEEL_BACKGROUND_DISC_SEGMENTS);
    }

    private static void drawDisc(GuiGraphics graphics, int cx, int cy, double radius, int color, int segments) {
        Matrix4f matrix = graphics.pose().last().pose();
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.debugQuads());
        addDisc(consumer, matrix, cx, cy, radius, color, segments);
        graphics.flush();
    }

    private static void addDisc(VertexConsumer consumer, Matrix4f matrix, int cx, int cy, double radius, int color, int segments) {
        for (int s = 0; s < segments; s++) {
            double t0 = TAU * s / segments;
            double t1 = TAU * (s + 1) / segments;
            float x0 = (float) (cx + radius * Math.cos(t0));
            float y0 = (float) (cy + radius * Math.sin(t0));
            float x1 = (float) (cx + radius * Math.cos(t1));
            float y1 = (float) (cy + radius * Math.sin(t1));
            consumer.addVertex(matrix, cx, cy, 0.0F).setColor(color);
            consumer.addVertex(matrix, x0, y0, 0.0F).setColor(color);
            consumer.addVertex(matrix, x1, y1, 0.0F).setColor(color);
            consumer.addVertex(matrix, cx, cy, 0.0F).setColor(color);
        }
    }

    private void drawSeparators(GuiGraphics graphics, int cx, int cy) {
        int count = this.unlocked.size();
        if (count <= 0) {
            return;
        }
        double sliceAngle = TAU / count;
        Matrix4f matrix = graphics.pose().last().pose();
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.debugQuads());
        for (int i = 0; i < count; i++) {
            double angle = ModConstants.ART_WHEEL_START_ANGLE + i * sliceAngle;
            line(consumer, matrix, cx, cy, angle, ModConstants.ART_WHEEL_INNER_RADIUS,
                    ModConstants.ART_WHEEL_OUTER_RADIUS, ModConstants.ART_WHEEL_SEPARATOR_WIDTH,
                    ModConstants.ART_WHEEL_SEPARATOR_COLOR);
        }
        graphics.flush();
    }

    private static void quad(VertexConsumer consumer, Matrix4f matrix, int cx, int cy, int color,
                             double radiusStart, double radiusEnd, double angleStart, double angleEnd) {
        float x00 = (float) (cx + radiusStart * Math.cos(angleStart));
        float y00 = (float) (cy + radiusStart * Math.sin(angleStart));
        float x10 = (float) (cx + radiusEnd * Math.cos(angleStart));
        float y10 = (float) (cy + radiusEnd * Math.sin(angleStart));
        float x11 = (float) (cx + radiusEnd * Math.cos(angleEnd));
        float y11 = (float) (cy + radiusEnd * Math.sin(angleEnd));
        float x01 = (float) (cx + radiusStart * Math.cos(angleEnd));
        float y01 = (float) (cy + radiusStart * Math.sin(angleEnd));
        consumer.addVertex(matrix, x00, y00, 0.0F).setColor(color);
        consumer.addVertex(matrix, x10, y10, 0.0F).setColor(color);
        consumer.addVertex(matrix, x11, y11, 0.0F).setColor(color);
        consumer.addVertex(matrix, x01, y01, 0.0F).setColor(color);
    }

    private static void line(VertexConsumer consumer, Matrix4f matrix, int cx, int cy, double angle,
                             double radiusStart, double radiusEnd, double halfWidth, int color) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float x0 = (float) (cx + radiusStart * cos);
        float y0 = (float) (cy + radiusStart * sin);
        float x1 = (float) (cx + radiusEnd * cos);
        float y1 = (float) (cy + radiusEnd * sin);
        float hw = (float) halfWidth;
        consumer.addVertex(matrix, x0 - sin * hw, y0 + cos * hw, 0.0F).setColor(color);
        consumer.addVertex(matrix, x1 - sin * hw, y1 + cos * hw, 0.0F).setColor(color);
        consumer.addVertex(matrix, x1 + sin * hw, y1 - cos * hw, 0.0F).setColor(color);
        consumer.addVertex(matrix, x0 + sin * hw, y0 - cos * hw, 0.0F).setColor(color);
    }

    private int angleToIndex(double angle) {
        int count = this.unlocked.size();
        if (count <= 0) {
            return -1;
        }
        double relative = angle - ModConstants.ART_WHEEL_START_ANGLE;
        while (relative < 0.0D) {
            relative += TAU;
        }
        while (relative >= TAU) {
            relative -= TAU;
        }
        int index = (int) (relative / (TAU / count));
        return index >= count ? count - 1 : index;
    }

    private static int brighten(int base) {
        int r = Math.min(255, ((base >> 16) & 0xFF) + ModConstants.ART_WHEEL_BRIGHTEN_AMOUNT);
        int g = Math.min(255, ((base >> 8) & 0xFF) + ModConstants.ART_WHEEL_BRIGHTEN_AMOUNT);
        int b = Math.min(255, (base & 0xFF) + ModConstants.ART_WHEEL_BRIGHTEN_AMOUNT);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
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