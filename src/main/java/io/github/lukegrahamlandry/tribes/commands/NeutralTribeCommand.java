package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class NeutralTribeCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("neutral")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(NeutralTribeCommand::handleJoin)
                ).executes(ctx -> {
                            ctx.getSource().sendFeedback(new StringTextComponent("pick a tribe to be neutral with"), false);
                            return 0;
                        }
                );

    }

    public static int handleJoin(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();
        String name = StringArgumentType.getString(source, "name");

        Tribe yourTribe = TribesManager.getTribeOf(player.getUniqueID());
        Tribe otherTribe = TribesManager.getTribe(name);

        TribeActionResult response = yourTribe.setRelation(player.getUniqueID(), otherTribe, Tribe.Relation.NONE);
        if (response == TribeActionResult.SUCCESS){
            source.getSource().sendFeedback(new StringTextComponent("Your tribe is now neutral with: " + name), true);
            yourTribe.broadcastMessage(name + " (" + otherTribe.getInitials() + ") is now neutral", player);
        } else {
            source.getSource().sendFeedback(new StringTextComponent(response.toString()), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
