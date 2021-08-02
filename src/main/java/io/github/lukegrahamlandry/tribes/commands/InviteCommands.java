package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.EntityArgument;
import net.minecraft.world.entity.player.Player;

public class InviteCommands {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("invite")
                .then(Commands.literal("send").then(Commands.argument("player", EntityArgument.player()).executes(InviteCommands::invitePlayer))).executes(ctx -> {
                    ctx.getSource().sendSuccess(TribeErrorType.ARG_PLAYER.getText(), false);
                    return 0;
                })
                .then(Commands.literal("revoke").then(Commands.argument("player", EntityArgument.player()).executes(InviteCommands::uninvitePlayer))).executes(ctx -> {
                    ctx.getSource().sendSuccess(TribeErrorType.ARG_PLAYER.getText(), false);
                    return 0;
                })
                .then(Commands.literal("toggle")
                        .then(Commands.argument("flag", BoolArgumentType.bool()).executes(InviteCommands::setPrivate))
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(TribeErrorType.ARG_MISSING.getText(), false);
                                return 0;
                            }));
    }

    private static int invitePlayer(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Player leader = source.getSource().getPlayerOrException();
        Player toInvite = EntityArgument.getPlayer(source, "player");

        Tribe tribe = TribesManager.getTribeOf(leader.getUUID());

        if (tribe == null) {
            source.getSource().sendSuccess(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), false);
            return 0;
        }
        if (!tribe.isPrivate){
            source.getSource().sendSuccess(TribeErrorType.NOT_PRIVATE.getText(), false);
            return 0;
        }
        if (!tribe.isOfficer(leader.getUUID())){
            source.getSource().sendSuccess(TribeErrorType.LOW_RANK.getText(), false);
            return 0;
        }

        tribe.pendingInvites.add(toInvite.getUUID().toString());
        tribe.broadcastMessage(TribeSuccessType.INVITE_SENT, leader, toInvite);

        return 0;
    }

    private static int uninvitePlayer(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Player leader = source.getSource().getPlayerOrException();
        Player toInvite = EntityArgument.getPlayer(source, "player");

        Tribe tribe = TribesManager.getTribeOf(leader.getUUID());

        if (tribe == null) {
            source.getSource().sendSuccess(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), false);
            return 0;
        }
        if (!tribe.isPrivate){
            source.getSource().sendSuccess(TribeErrorType.NOT_PRIVATE.getText(), false);
            return 0;
        }
        if (!tribe.isOfficer(leader.getUUID())){
            source.getSource().sendSuccess(TribeErrorType.LOW_RANK.getText(), false);
            return 0;
        }

        tribe.pendingInvites.remove(toInvite.getUUID().toString());
        tribe.broadcastMessage(TribeSuccessType.INVITE_REMOVED, leader, toInvite);

        return 0;
    }

    private static int setPrivate(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Player leader = source.getSource().getPlayerOrException();
        boolean flag = BoolArgumentType.getBool(source, "flag");

        Tribe tribe = TribesManager.getTribeOf(leader.getUUID());

        if (tribe == null) {
            source.getSource().sendSuccess(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), false);
            return 0;
        }
        if (!tribe.isViceLeader(leader.getUUID())){
            source.getSource().sendSuccess(TribeErrorType.LOW_RANK.getText(), false);
            return 0;
        }

        tribe.isPrivate = flag;
        TribeSuccessType msg = flag ? TribeSuccessType.NOW_PRIVATE : TribeSuccessType.NO_LONGER_PRIVATE;
        tribe.broadcastMessage(msg, leader);

        return 0;
    }





}
