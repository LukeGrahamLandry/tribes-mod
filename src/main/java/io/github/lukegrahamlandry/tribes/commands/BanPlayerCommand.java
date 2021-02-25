package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

public class BanPlayerCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("ban")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(BanPlayerCommand::handleBan)
                ).executes(ctx -> {
                            ctx.getSource().sendFeedback(new StringTextComponent("pick a player to ban"), false);
                            return 0;
                        }
                );

    }

    public static int handleBan(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity playerBanning = source.getSource().asPlayer();
        PlayerEntity playerToBan = EntityArgument.getPlayer(source, "player");

        Tribe tribe = TribesManager.getTribeOf(playerBanning.getUniqueID());
        if (tribe == null){
            source.getSource().sendFeedback(new StringTextComponent("FAILURE: you are not in a tribe"), true);
        } else {
            TribeActionResult response = tribe.banPlayer(playerBanning.getUniqueID(), playerToBan.getUniqueID());

            if (response == TribeActionResult.SUCCESS){
                // source.getSource().sendFeedback(new StringTextComponent("You successfully banned: " + playerToBan.getName().getString()), true);
                tribe.broadcastMessage(playerToBan.getName().getString() + " has been banned from your tribe", playerBanning);
            } else {
                source.getSource().sendFeedback(new StringTextComponent(response.toString()), true);
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
