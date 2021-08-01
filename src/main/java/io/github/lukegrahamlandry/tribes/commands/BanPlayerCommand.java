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

public class BanPlayerCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("ban")
                .requires(cs->cs.hasPermission(0)) //permission
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(BanPlayerCommand::handleBan)
                ).executes(ctx -> {
                    ctx.getSource().sendSuccess(TribeErrorType.ARG_PLAYER.getText(), false);
                            return 0;
                        }
                );

    }

    public static int handleBan(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity playerBanning = source.getSource().getPlayerOrException();
        PlayerEntity playerToBan = EntityArgument.getPlayer(source, "player");

        Tribe tribe = TribesManager.getTribeOf(playerBanning.getUUID());
        if (tribe == null){
            source.getSource().sendSuccess(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        } else {
            TribeErrorType response = tribe.banPlayer(playerBanning.getUUID(), playerToBan.getUUID());

            if (response == TribeErrorType.SUCCESS){
                tribe.broadcastMessage(TribeSuccessType.BAN_PLAYER, playerBanning, playerToBan);
            } else {
                source.getSource().sendSuccess(response.getText(), true);
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
