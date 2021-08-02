package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.EntityArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.TextComponent;

public class PromotePlayerCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("promote")
                .requires(cs->cs.hasPermission(0)) //permission
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(PromotePlayerCommand::handle)
                ).executes(ctx -> {
                    ctx.getSource().sendSuccess(TribeErrorType.ARG_PLAYER.getText(), false);
                            return 0;
                        }
                );

    }

    public static int handle(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Player playerRunning = source.getSource().getPlayerOrException();
        Player playerTarget = EntityArgument.getPlayer(source, "player");

        Tribe tribe = TribesManager.getTribeOf(playerRunning.getUUID());

        if (tribe == null){
            source.getSource().sendSuccess(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
            return Command.SINGLE_SUCCESS;
        }

        // require confirm to demote yourself
        if (tribe.isViceLeader(playerTarget.getUUID()) && tribe.isLeader(playerRunning.getUUID())){
            source.getSource().sendSuccess(new TextComponent("make " + playerTarget.getName().getString() + " the leader of your tribe?"), true);

            ConfirmCommand.add(playerRunning, () -> {
                TribeErrorType response = tribe.promotePlayer(playerRunning.getUUID(), playerTarget.getUUID());

                if (response == TribeErrorType.SUCCESS){
                    String rank = tribe.getRankOf(playerTarget.getUUID().toString()).asString();
                    tribe.broadcastMessage(TribeSuccessType.PROMOTE, playerRunning, playerTarget, rank);
                } else {
                    source.getSource().sendSuccess(response.getText(), true);
                }
            });
            return Command.SINGLE_SUCCESS;
        }

        TribeErrorType response = tribe.promotePlayer(playerRunning.getUUID(), playerTarget.getUUID());

        if (response == TribeErrorType.SUCCESS){
            String rank = tribe.getRankOf(playerTarget.getUUID().toString()).asString();
            tribe.broadcastMessage(TribeSuccessType.PROMOTE, playerRunning, playerTarget, rank);
        } else {
            source.getSource().sendSuccess(response.getText(), true);
        }


        return Command.SINGLE_SUCCESS;
    }
}
