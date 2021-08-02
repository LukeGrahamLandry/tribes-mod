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
import net.minecraft.commands.synchronization.EntityArgument;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class ListBansCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("bans")
                .requires(cs->cs.hasPermission(0)) //permission
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ListBansCommand::handleListBans)
                ).executes(ctx -> {
                    ctx.getSource().sendSuccess(TribeErrorType.ARG_PLAYER.getText(), false);
                            return 0;
                        }
                );

    }

    public static int handleListBans(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Player playerToCheck = EntityArgument.getPlayer(source, "player");

        List<Tribe> bannedIn = TribesManager.getBans(playerToCheck);
        String output = "";
        for (Tribe tribe: bannedIn) {
            output += tribe.getName() + ", ";
        }

        if (bannedIn.size() == 0){
            source.getSource().sendSuccess(TribeSuccessType.NO_BANS.getBlueText(playerToCheck), true);
        } else {
            source.getSource().sendSuccess(TribeSuccessType.LIST_BANS.getBlueText(playerToCheck, output), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
