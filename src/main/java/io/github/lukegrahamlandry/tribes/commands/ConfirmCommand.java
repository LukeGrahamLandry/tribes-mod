package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfirmCommand {
    private static Map<UUID, IConfirmAction> CONFIRM_ACTIONS = new HashMap<>();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("confirm").executes(ConfirmCommand::handleConfirm);
    }

    public static void add(Player player, IConfirmAction action){
        player.displayClientMessage(TribeSuccessType.MUST_CONFIRM.getBlueText(), false);
        CONFIRM_ACTIONS.put(player.getUUID(), action);
    }

    public static int handleConfirm(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Player player = source.getSource().getPlayerOrException();


        IConfirmAction action = CONFIRM_ACTIONS.get(player.getUUID());
        if (action == null){
            source.getSource().sendSuccess(TribeErrorType.NO_CONFIRM.getText(), true);
        } else {
            action.call();
        }

        return Command.SINGLE_SUCCESS;
    }

    public interface IConfirmAction {
        void call();
    }
}
