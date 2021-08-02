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
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;

public class WhichTribeCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("who")
                .requires(cs->cs.hasPermission(0)) //permission
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(WhichTribeCommand::handleCheck)
                ).executes(ctx -> {
                    ctx.getSource().sendSuccess(TribeErrorType.ARG_PLAYER.getText(), false);
                            return 0;
                        }
                );

    }

    public static int handleCheck(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Player playerToCheck = EntityArgument.getPlayer(source, "player");

        Tribe tribe = TribesManager.getTribeOf(playerToCheck.getUUID());

        if (tribe == null){
            source.getSource().sendSuccess(TribeSuccessType.WHICH_NO_TRIBE.getBlueText(playerToCheck), true);
        } else {
            source.getSource().sendSuccess(TribeSuccessType.WHICH_TRIBE.getBlueText(playerToCheck, tribe), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
