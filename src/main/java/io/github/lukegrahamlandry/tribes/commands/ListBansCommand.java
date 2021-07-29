package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class ListBansCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("bans")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ListBansCommand::handleListBans)
                ).executes(ctx -> {
                    ctx.getSource().sendFeedback(TribeErrorType.ARG_PLAYER.getText(), false);
                            return 0;
                        }
                );

    }

    public static int handleListBans(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity playerToCheck = EntityArgument.getPlayer(source, "player");

        List<Tribe> bannedIn = TribesManager.getBans(playerToCheck);
        String output = "";
        for (Tribe tribe: bannedIn) {
            output += tribe.getName() + ", ";
        }

        if (bannedIn.size() == 0){
            source.getSource().sendFeedback(TribeSuccessType.NO_BANS.getBlueText(playerToCheck), true);
        } else {
            source.getSource().sendFeedback(TribeSuccessType.LIST_BANS.getBlueText(playerToCheck, output), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
