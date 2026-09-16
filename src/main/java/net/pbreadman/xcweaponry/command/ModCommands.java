package net.pbreadman.xcweaponry.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.pbreadman.xcweaponry.items.MonadoArt;
import net.pbreadman.xcweaponry.items.MonadoBase;
import net.pbreadman.xcweaponry.items.ModItems;

public class ModCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("xcweaponry")
                .then(Commands.literal("art")
                        .then(Commands.literal("none")
                                .executes(ctx -> setArt(ctx.getSource(), null)))
                        .then(Commands.argument("art", StringArgumentType.word())
                                .suggests(ModCommands::suggestArts)
                                .executes(ctx -> setArt(ctx.getSource(), StringArgumentType.getString(ctx, "art"))))));
    }

    private static CompletableFuture<Suggestions> suggestArts(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        builder.suggest("none");
        for (MonadoArt art : MonadoArt.values()) {
            builder.suggest(art.getName());
        }
        return builder.buildFuture();
    }

    private static int setArt(CommandSourceStack source, String name) throws CommandSyntaxException {
        Player player = source.getPlayerOrException();
        ItemStack held = player.getMainHandItem();
        if (!held.is(ModItems.MONADO.get())) {
            source.sendFailure(Component.literal("You must be holding a Monado."));
            return 0;
        }
        if (name == null) {
            MonadoBase.selectArt(held, null);
            source.sendSuccess(() -> Component.literal("Monado resets to base.").withStyle(ChatFormatting.GRAY), false);
            return 1;
        }
        MonadoArt art = MonadoArt.fromName(name);
        if (art == null) {
            source.sendFailure(Component.literal("Unknown art '" + name + "'."));
            return 0;
        }
        if (!held.has(art.getUnlockHolder().get())) {
            source.sendFailure(Component.literal("Art '" + art.getName() + "' is not unlocked on this Monado."));
            return 0;
        }
        MonadoBase.selectArt(held, art);
        source.sendSuccess(() -> Component.literal("Monado art set to " + art.getName() + ".").withStyle(ChatFormatting.AQUA), false);
        return 1;
    }
}