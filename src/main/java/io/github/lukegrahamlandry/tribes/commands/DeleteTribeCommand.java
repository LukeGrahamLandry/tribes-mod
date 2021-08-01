package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class DeleteTribeCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("delete")
                .requires(cs->cs.hasPermission(0)) //permission
                .executes(DeleteTribeCommand::handleDelete);

    }

    public static int handleDelete(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().getPlayerOrException();

        TribeErrorType response = TribesManager.deleteTribe(TribesManager.getTribeOf(player.getUUID()).getName(), player.getUUID());
        if (response != TribeErrorType.SUCCESS){
            source.getSource().sendSuccess(response.getText(), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
