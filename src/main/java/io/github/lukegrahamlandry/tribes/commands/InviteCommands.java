package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.*;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;

public class InviteCommands {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("invite")
                .then(Commands.literal("send").then(Commands.argument("player", EntityArgument.player()).executes(InviteCommands::invitePlayer))).executes(ctx -> {
                    ctx.getSource().sendFeedback(TribeErrorType.ARG_PLAYER.getText(), false);
                    return 0;
                })
                .then(Commands.literal("revoke").then(Commands.argument("player", EntityArgument.player()).executes(InviteCommands::uninvitePlayer))).executes(ctx -> {
                    ctx.getSource().sendFeedback(TribeErrorType.ARG_PLAYER.getText(), false);
                    return 0;
                })
                .then(Commands.literal("toggle")
                        .then(Commands.argument("flag", BoolArgumentType.bool()).executes(InviteCommands::setPrivate))
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(TribeErrorType.ARG_MISSING.getText(), false);
                                return 0;
                            }));
    }

    private static int invitePlayer(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity leader = source.getSource().asPlayer();
        PlayerEntity toInvite = EntityArgument.getPlayer(source, "player");

        Tribe tribe = TribesManager.getTribeOf(leader.getUniqueID());

        if (tribe == null) {
            source.getSource().sendFeedback(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), false);
            return 0;
        }
        if (!tribe.isPrivate){
            source.getSource().sendFeedback(TribeErrorType.NOT_PRIVATE.getText(), false);
            return 0;
        }
        if (!tribe.isOfficer(leader.getUniqueID())){
            source.getSource().sendFeedback(TribeErrorType.LOW_RANK.getText(), false);
            return 0;
        }

        tribe.pendingInvites.add(toInvite.getUniqueID().toString());
        tribe.broadcastMessage(TribeSuccessType.INVITE_SENT, leader, toInvite);

        return 0;
    }

    private static int uninvitePlayer(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity leader = source.getSource().asPlayer();
        PlayerEntity toInvite = EntityArgument.getPlayer(source, "player");

        Tribe tribe = TribesManager.getTribeOf(leader.getUniqueID());

        if (tribe == null) {
            source.getSource().sendFeedback(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), false);
            return 0;
        }
        if (!tribe.isPrivate){
            source.getSource().sendFeedback(TribeErrorType.NOT_PRIVATE.getText(), false);
            return 0;
        }
        if (!tribe.isOfficer(leader.getUniqueID())){
            source.getSource().sendFeedback(TribeErrorType.LOW_RANK.getText(), false);
            return 0;
        }

        tribe.pendingInvites.remove(toInvite.getUniqueID().toString());
        tribe.broadcastMessage(TribeSuccessType.INVITE_REMOVED, leader, toInvite);

        return 0;
    }

    private static int setPrivate(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity leader = source.getSource().asPlayer();
        boolean flag = BoolArgumentType.getBool(source, "flag");

        Tribe tribe = TribesManager.getTribeOf(leader.getUniqueID());

        if (tribe == null) {
            source.getSource().sendFeedback(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), false);
            return 0;
        }
        if (!tribe.isViceLeader(leader.getUniqueID())){
            source.getSource().sendFeedback(TribeErrorType.LOW_RANK.getText(), false);
            return 0;
        }

        tribe.isPrivate = flag;
        TribeSuccessType msg = flag ? TribeSuccessType.NOW_PRIVATE : TribeSuccessType.NO_LONGER_PRIVATE;
        tribe.broadcastMessage(msg, leader);

        return 0;
    }





}
