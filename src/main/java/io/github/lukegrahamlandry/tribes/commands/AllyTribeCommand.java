package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.commands.util.TribeArgumentType;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class AllyTribeCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("ally")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("tribe", TribeArgumentType.tribe())
                        .executes(AllyTribeCommand::handleJoin)
                ).executes(ctx -> {
                            ctx.getSource().sendFeedback(new StringTextComponent("pick a tribe to ally"), false);
                            return 0;
                        }
                );

    }

    public static int handleJoin(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();
        Tribe otherTribe = TribeArgumentType.getTribe(source, "tribe");
        if (otherTribe == null) return 1;

        Tribe yourTribe = TribesManager.getTribeOf(player.getUniqueID());

        TribeActionResult response = yourTribe.setRelation(player.getUniqueID(), otherTribe, Tribe.Relation.ALLY);
        if (response == TribeActionResult.SUCCESS){
            source.getSource().sendFeedback(new StringTextComponent("Your tribe is now allies with " + otherTribe.getName()), true);
            yourTribe.broadcastMessage(otherTribe.getName() + " (" + otherTribe.getInitials() + ") is now your ally", player);
        } else {
            source.getSource().sendFeedback(response.getErrorComponent(), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
