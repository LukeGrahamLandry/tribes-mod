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
import net.minecraft.util.text.StringTextComponent;

public class LeaveTribeCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("leave")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .executes(LeaveTribeCommand::handleLeave);

    }

    public static int handleLeave(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();

        // check if it would delete tribe
        Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());
        if (tribe != null && tribe.getCount() == 1){
            ConfirmCommand.add(player, () -> {
                TribesManager.leaveTribe(player);
                source.getSource().sendFeedback(new StringTextComponent("You successfully left your tribe"), true);
            });
            return Command.SINGLE_SUCCESS;
        }

        // normal leave
        TribeActionResult response = TribesManager.leaveTribe(player);
        if (response == TribeActionResult.SUCCESS){
            source.getSource().sendFeedback(new StringTextComponent("You successfully left your tribe"), true);
        } else {
            source.getSource().sendFeedback(new StringTextComponent(response.toString()), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
