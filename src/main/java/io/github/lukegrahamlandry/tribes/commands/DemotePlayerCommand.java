package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class DemotePlayerCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("demote")
                .requires(cs->cs.hasPermission(0)) //permission
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(DemotePlayerCommand::handle)
                ).executes(ctx -> {
                    ctx.getSource().sendSuccess(TribeErrorType.ARG_PLAYER.getText(), false);
                            return 0;
                        }
                );

    }

    public static int handle(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity playerRunning = source.getSource().getPlayerOrException();
        PlayerEntity playerTarget = EntityArgument.getPlayer(source, "player");

        Tribe tribe = TribesManager.getTribeOf(playerRunning.getUUID());
        if (tribe == null){
            source.getSource().sendSuccess(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        } else {
            TribeErrorType response = tribe.demotePlayer(playerRunning.getUUID(), playerTarget.getUUID());

            if (response == TribeErrorType.SUCCESS){
                String rank = tribe.getRankOf(playerTarget.getUUID().toString()).asString();
                tribe.broadcastMessage(TribeSuccessType.DEMOTE, playerRunning, playerTarget, rank);
            } else {
                source.getSource().sendSuccess(response.getText(), true);
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
