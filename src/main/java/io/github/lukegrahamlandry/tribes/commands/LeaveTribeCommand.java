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
import net.minecraft.world.entity.player.Player;

public class LeaveTribeCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("leave")
                .requires(cs->cs.hasPermission(0)) //permission
                .executes(LeaveTribeCommand::handleLeave);

    }

    public static int handleLeave(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Player player = source.getSource().getPlayerOrException();

        Tribe tribe = TribesManager.getTribeOf(player.getUUID());
        if (tribe != null){
            ConfirmCommand.add(player, () -> {
                TribesManager.leaveTribe(player);
                source.getSource().sendSuccess(TribeSuccessType.YOU_LEFT.getText(), true);
            });
            return Command.SINGLE_SUCCESS;
        } else {
            source.getSource().sendSuccess(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
