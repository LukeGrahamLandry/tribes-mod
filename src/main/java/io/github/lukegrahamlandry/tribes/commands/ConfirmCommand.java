package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfirmCommand {
    private static Map<UUID, IConfirmAction> CONFIRM_ACTIONS = new HashMap<>();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("confirm").executes(ConfirmCommand::handleConfirm);
    }

    public static void add(PlayerEntity player, IConfirmAction action){
        player.sendStatusMessage(TribeSuccessType.MUST_CONFIRM.getBlueText(), false);
        CONFIRM_ACTIONS.put(player.getUniqueID(), action);
    }

    public static int handleConfirm(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();


        IConfirmAction action = CONFIRM_ACTIONS.get(player.getUniqueID());
        if (action == null){
            source.getSource().sendFeedback(TribeErrorType.NO_CONFIRM.getText(), true);
        } else {
            action.call();
        }

        return Command.SINGLE_SUCCESS;
    }

    public interface IConfirmAction {
        void call();
    }
}
