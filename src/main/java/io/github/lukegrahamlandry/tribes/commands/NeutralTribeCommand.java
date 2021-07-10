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

public class NeutralTribeCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("neutral")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("tribe", TribeArgumentType.tribe())
                        .executes(NeutralTribeCommand::handleJoin)
                ).executes(ctx -> {
                    ctx.getSource().sendFeedback(TribeErrorType.ARG_TRIBE.getText(), false);
                            return 0;
                        }
                );

    }

    public static int handleJoin(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();
        Tribe otherTribe = TribeArgumentType.getTribe(source, "tribe");
        if (otherTribe == null) return 1;

        Tribe yourTribe = TribesManager.getTribeOf(player.getUniqueID());

        TribeErrorType response = yourTribe.setRelation(player.getUniqueID(), otherTribe, Tribe.Relation.NONE);
        if (response == TribeErrorType.SUCCESS){
            yourTribe.broadcastMessage(TribeSuccessType.NEUTRAL_TRIBE, player, otherTribe);
        } else {
            source.getSource().sendFeedback(response.getText(), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
