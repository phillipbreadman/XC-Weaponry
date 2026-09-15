package net.pbreadman.xcweaponry.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.pbreadman.xcweaponry.items.ModItems;


public class ModEvents {
    @SubscribeEvent
    public static void OntosUse(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack heldItem = event.getItemStack();
        InteractionHand hand = event.getHand();

        if (heldItem.is(ModItems.ONTOS.get())) {
        ItemStack replaceItem = new ItemStack(ModItems.MONADO.get());
        ItemStack resultsStack = ItemUtils.createFilledResult(heldItem, player, replaceItem);

        player.setItemInHand(hand,resultsStack);
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
        }
    }


}
