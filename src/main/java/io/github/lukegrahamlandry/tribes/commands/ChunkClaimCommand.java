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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;

public class ChunkClaimCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("chunk")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(claim())
                .then(unclaim())
                .then(who());
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
            source.getSource().sendFeedback(new StringTextComponent("You're not in a tribe'"), true);
            return Command.SINGLE_SUCCESS;
        }

        TribeActionResult response = tribe.claimChunk(getChunk(player), player.getUniqueID());
        if (response == TribeActionResult.SUCCESS){
            source.getSource().sendFeedback(new StringTextComponent("You claimed this chunk"), true);
            int x = (int) getChunk(player);
            int z = (int) (getChunk(player) >> 32);
            tribe.broadcastMessage("chunk (" + x + ", " + z + ") has been claimed", player);
        } else {
            source.getSource().sendFeedback(new StringTextComponent(response.toString()), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int handleUnclaim(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();
        Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());

        if (tribe == null){
            source.getSource().sendFeedback(new StringTextComponent("You're not in a tribe'"), true);
            return Command.SINGLE_SUCCESS;
        }

        TribeActionResult response = tribe.unclaimChunk(getChunk(player), player.getUniqueID());
        if (response == TribeActionResult.SUCCESS){
            source.getSource().sendFeedback(new StringTextComponent("You unclaimed this chunk"), true);
            int x = (int) getChunk(player);
            int z = (int) (getChunk(player) >> 32);
            tribe.broadcastMessage("chunk (" + x + ", " + z + ") has been unclaimed", player);
        } else {
            source.getSource().sendFeedback(new StringTextComponent(response.toString()), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int handleWho(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();

        Tribe owner = TribesManager.getChunkOwner(getChunk(player));
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
