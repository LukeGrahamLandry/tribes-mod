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

public class PromotePlayerCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("promote")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(PromotePlayerCommand::handle)
                ).executes(ctx -> {
                    ctx.getSource().sendFeedback(TribeErrorType.ARG_PLAYER.getText(), false);
                            return 0;
                        }
                );

    }

    public static int handle(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity playerRunning = source.getSource().asPlayer();
        PlayerEntity playerTarget = EntityArgument.getPlayer(source, "player");

        Tribe tribe = TribesManager.getTribeOf(playerRunning.getUniqueID());

        if (tribe == null){
            source.getSource().sendFeedback(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
            return Command.SINGLE_SUCCESS;
        }

        // require confirm to demote yourself
        if (tribe.isViceLeader(playerTarget.getUniqueID()) && tribe.isLeader(playerRunning.getUniqueID())){
            source.getSource().sendFeedback(new StringTextComponent("make " + playerTarget.getName().getString() + " the leader of your tribe?"), true);

            ConfirmCommand.add(playerRunning, () -> {
                TribeErrorType response = tribe.promotePlayer(playerRunning.getUniqueID(), playerTarget.getUniqueID());

                if (response == TribeErrorType.SUCCESS){
                    String rank = tribe.getRankOf(playerTarget.getUniqueID().toString()).asString();
                    tribe.broadcastMessage(TribeSuccessType.PROMOTE, playerRunning, playerTarget, rank);
                } else {
                    source.getSource().sendFeedback(response.getText(), true);
                }
            });
            return Command.SINGLE_SUCCESS;
        }

        TribeErrorType response = tribe.promotePlayer(playerRunning.getUniqueID(), playerTarget.getUniqueID());

        if (response == TribeErrorType.SUCCESS){
            String rank = tribe.getRankOf(playerTarget.getUniqueID().toString()).asString();
            tribe.broadcastMessage(TribeSuccessType.PROMOTE, playerRunning, playerTarget, rank);
        } else {
            source.getSource().sendFeedback(response.getText(), true);
        }


        return Command.SINGLE_SUCCESS;
    }
}
