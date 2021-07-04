package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.*;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class ChunkClaimCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("chunk")
                .requires(cs->cs.hasPermissionLevel(0))
                .then(claim())
                .then(unclaim());
                // .then(who());  // done in ShowLandOwnerUI
    }

    private static ArgumentBuilder<CommandSource, ?> claim() {
        return Commands.literal("claim")
                .executes(ChunkClaimCommand::handleClaim);
    }

    private static ArgumentBuilder<CommandSource, ?> unclaim() {
        return Commands.literal("unclaim")
                .executes(ChunkClaimCommand::handleUnclaim);
    }

    private static ArgumentBuilder<CommandSource, ?> who() {
        return Commands.literal("who")
                .executes(ChunkClaimCommand::handleWho);
    }

    public static int handleClaim(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();
        Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());

        if (tribe == null){
            source.getSource().sendFeedback(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
            return Command.SINGLE_SUCCESS;
        }

        TribeErrorType response = tribe.claimChunk(getChunk(player), player.getUniqueID());
        if (response == TribeErrorType.SUCCESS){
            int x = (int) getChunk(player);
            int z = (int) (getChunk(player) >> 32);
            tribe.broadcastMessage(TribeSuccessType.CLAIM_CHUNK, player, x, z);
        } else {
            source.getSource().sendFeedback(response.getText(), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int handleUnclaim(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();
        Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());

        if (tribe == null){
            source.getSource().sendFeedback(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
            return Command.SINGLE_SUCCESS;
        }

        TribeErrorType response = tribe.unclaimChunk(getChunk(player), player.getUniqueID());
        if (response == TribeErrorType.SUCCESS){
            int x = (int) getChunk(player);
            int z = (int) (getChunk(player) >> 32);

            tribe.broadcastMessage(TribeSuccessType.UNCLAIM_CHUNK, player, x, z);
        } else {
            source.getSource().sendFeedback(response.getText(), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int handleWho(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();

        Tribe owner = LandClaimHelper.getChunkOwner(getChunk(player));
        int x = (int) getChunk(player);
        int z = (int) (getChunk(player) >> 32);

        if (owner == null){
            source.getSource().sendFeedback(new StringTextComponent("chunk (" + x + ", " + z + ") is unclaimed"), true);
        } else {
            source.getSource().sendFeedback(new StringTextComponent("chunk (" + x + ", " + z + ") is claimed by " + owner.getName() + " (" + owner.getInitials() + ")"), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static long getChunk(PlayerEntity player){
        return player.getEntityWorld().getChunkAt(player.getPosition()).getPos().asLong();
    }
}
