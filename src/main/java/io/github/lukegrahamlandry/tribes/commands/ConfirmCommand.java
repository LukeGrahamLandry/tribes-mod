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
        player.displayClientMessage(TribeSuccessType.MUST_CONFIRM.getBlueText(), false);
        CONFIRM_ACTIONS.put(player.getUUID(), action);
    }

    public static int handleConfirm(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().getPlayerOrException();


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
