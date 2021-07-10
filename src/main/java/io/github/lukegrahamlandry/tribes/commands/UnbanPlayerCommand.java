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

public class UnbanPlayerCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("unban")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(UnbanPlayerCommand::handleBan)
                ).executes(ctx -> {
                    ctx.getSource().sendFeedback(TribeErrorType.ARG_PLAYER.getText(), false);
                            return 0;
                        }
                );

    }

    public static int handleBan(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity playerBanning = source.getSource().asPlayer();
        PlayerEntity playerToUnban = EntityArgument.getPlayer(source, "player");

        Tribe tribe = TribesManager.getTribeOf(playerBanning.getUniqueID());
        if (tribe == null){
            source.getSource().sendFeedback(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        } else {
            TribeErrorType response = tribe.unbanPlayer(playerBanning.getUniqueID(), playerToUnban.getUniqueID());

            if (response == TribeErrorType.SUCCESS){
                tribe.broadcastMessage(TribeSuccessType.UNBAN_PLAYER, playerBanning, playerToUnban);
            } else {
                source.getSource().sendFeedback(response.getText(), true);
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
