package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class DeleteTribeCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("delete")
                .requires(cs->cs.hasPermission(0)) //permission
                .executes(DeleteTribeCommand::handleDelete);

    }

    public static int handleDelete(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Player player = source.getSource().getPlayerOrException();

        TribeErrorType response = TribesManager.deleteTribe(TribesManager.getTribeOf(player.getUUID()).getName(), player.getUUID());
        if (response != TribeErrorType.SUCCESS){
            source.getSource().sendSuccess(response.getText(), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
