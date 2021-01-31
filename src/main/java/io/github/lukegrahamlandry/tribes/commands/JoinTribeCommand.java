package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class JoinTribeCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("join")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(JoinTribeCommand::handleJoin)
                ).executes(ctx -> {
                            ctx.getSource().sendFeedback(new StringTextComponent("pick a tribe to join"), false);
                            return 0;
                        }
                );

    }

    public static int handleJoin(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();
        String name = StringArgumentType.getString(source, "name");

        TribeActionResult response = TribesManager.joinTribe(name, player);
        if (response == TribeActionResult.SUCCESS){
            source.getSource().sendFeedback(new StringTextComponent("Tribe successfully joined: " + name), true);
        } else {
            source.getSource().sendFeedback(new StringTextComponent(response.toString()), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}