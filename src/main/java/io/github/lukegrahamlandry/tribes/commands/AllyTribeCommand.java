package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.commands.util.TribeArgumentType;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class AllyTribeCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("ally")
                .requires(cs->cs.hasPermission(0)) //permission
                .then(Commands.argument("tribe", TribeArgumentType.tribe())
                        .executes(AllyTribeCommand::handleAlly)
                ).executes(ctx -> {
                            ctx.getSource().sendSuccess(TribeErrorType.ARG_TRIBE.getText(), false);
                            return 0;
                        }
                );

    }

    public static int handleAlly(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().getPlayerOrException();
        Tribe otherTribe = TribeArgumentType.getTribe(source, "tribe");
        if (otherTribe == null) return 1;

        Tribe yourTribe = TribesManager.getTribeOf(player.getUUID());

        TribeErrorType response = yourTribe.setRelation(player.getUUID(), otherTribe, Tribe.Relation.ALLY);
        if (response == TribeErrorType.SUCCESS){
            yourTribe.broadcastMessage(TribeSuccessType.ALLY_TRIBE, player, otherTribe);
        } else {
            source.getSource().sendSuccess(response.getText(), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
