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
import net.pbreadman.xcweaponry.items.MonadoBase;
import net.pbreadman.xcweaponry.items.custom.ModDataComponents;

public record ArtPayload(int artIndex) implements CustomPacketPayload {
    public static final Type<ArtPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(XCWeaponry.MOD_ID, select_art));
    public static final StreamCodec<FriendlyByteBuf, ArtPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ArtPayload:: artIndex,
            ArtPayload::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(ArtPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof MonadoBase) {
                stack.set(ModDataComponents.ModDataComponentTypes.SELECTED_ART.get(), payload.artIndex());
            }
        });
    }
}
