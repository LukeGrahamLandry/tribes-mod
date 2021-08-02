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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class EnemyTribeCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("enemy")
                .requires(cs->cs.hasPermission(0)) //permission
                .then(Commands.argument("tribe", TribeArgumentType.tribe())
                        .executes(EnemyTribeCommand::handleJoin)
                ).executes(ctx -> {
                    ctx.getSource().sendSuccess(TribeErrorType.ARG_TRIBE.getText(), false);
                            return 0;
                        }
                );

    }

    public static int handleJoin(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Player player = source.getSource().getPlayerOrException();
        Tribe otherTribe = TribeArgumentType.getTribe(source, "tribe");
        if (otherTribe == null) return 1;

        Tribe yourTribe = TribesManager.getTribeOf(player.getUUID());

        TribeErrorType response = yourTribe.setRelation(player.getUUID(), otherTribe, Tribe.Relation.ENEMY);
        if (response == TribeErrorType.SUCCESS){
            yourTribe.broadcastMessage(TribeSuccessType.ENEMY_TRIBE, player, otherTribe);
        } else {
            source.getSource().sendSuccess(response.getText(), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
