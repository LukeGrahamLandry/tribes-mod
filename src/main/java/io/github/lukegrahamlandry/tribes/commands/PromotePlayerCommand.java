package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
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

public class PromotePlayerCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("promote")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(PromotePlayerCommand::handle)
                ).executes(ctx -> {
                            ctx.getSource().sendFeedback(new StringTextComponent("pick a player to promote"), false);
                            return 0;
                        }
                );

    }

    public static int handle(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity playerRunning = source.getSource().asPlayer();
        PlayerEntity playerTarget = EntityArgument.getPlayer(source, "player");

        Tribe tribe = TribesManager.getTribeOf(playerRunning.getUniqueID());
        if (tribe == null){
            source.getSource().sendFeedback(new StringTextComponent("FAILURE: you are not in a tribe"), true);
        } else {
            TribeActionResult response = tribe.promotePlayer(playerRunning.getUniqueID(), playerTarget.getUniqueID());

            if (response == TribeActionResult.SUCCESS){
                String name = playerTarget.getName().getString();
                String rank = tribe.getRankOf(playerTarget.getUniqueID().toString()).asString();
                source.getSource().sendFeedback(new StringTextComponent("You successfully promoted " + name + " to " + rank), true);
            } else {
                source.getSource().sendFeedback(new StringTextComponent(response.toString()), true);
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
