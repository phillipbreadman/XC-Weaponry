package net.pbreadman.xcweaponry.items;

import cpw.mods.modlauncher.api.IModuleLayerManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.pbreadman.xcweaponry.items.custom.ModDataComponents;


public class MonadoBase extends SwordItem {
    public MonadoBase(Tier tier, Properties properties) {
        super(tier, properties);
    }
    @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        int currentArt = stack.getOrDefault(ModDataComponents.ModDataComponentTypes.SELECTED_ART.get(), 0);

        if (!level.isClientSide()) {
            triggerArt(currentArt, player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
    private void triggerArt(int artIndex, Player player) {
        switch (artIndex) {
            case 0 -> player.sendSystemMessage(Component.literal("Monado Buster"));
            case 1 -> player.sendSystemMessage(Component.literal("Monado Enchantr"));
            case 2 -> player.sendSystemMessage(Component.literal("Monado Shield"));
            case 3 -> player.sendSystemMessage(Component.literal("Monado Speed"));
            case 4 -> player.sendSystemMessage(Component.literal("Monado Purge"));
            case 5 -> player.sendSystemMessage(Component.literal("Monado Cyclone"));
            case 6 -> player.sendSystemMessage(Component.literal("Monado Eater"));
            case 7 -> player.sendSystemMessage(Component.literal("Monado Armour"));
            default -> {}
        }

    }
}
