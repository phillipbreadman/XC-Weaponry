package net.pbreadman.xcweaponry.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.pbreadman.xcweaponry.XCWeaponry;
import net.pbreadman.xcweaponry.items.MonadoArt;
import net.pbreadman.xcweaponry.items.MonadoBase;

public record ArtPayload(String artName) implements CustomPacketPayload {
    public static final Type<ArtPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(XCWeaponry.MOD_ID, "select_art"));
    public static final StreamCodec<FriendlyByteBuf, ArtPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ArtPayload::artName,
            ArtPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ArtPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null) {
                return;
            }
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof MonadoBase) {
                MonadoBase.selectArt(stack, MonadoArt.fromName(payload.artName()));
            }
        });
    }
}