package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class CountTribeCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("count")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(CountTribeCommand::handleCount)
                ).executes(ctx -> {
                            ctx.getSource().sendFeedback(new StringTextComponent("pick a tribe to count"), false);
                            return 0;
                        }
                );

    }

    public static int handleCount(CommandContext<CommandSource> source) throws CommandSyntaxException {
        String name = StringArgumentType.getString(source, "name");

        if (TribesManager.isNameAvailable(name)){
            source.getSource().sendFeedback(new StringTextComponent(name + " does not exist"), true);
        } else {
            source.getSource().sendFeedback(new StringTextComponent(name + " has " + TribesManager.getTribe(name).getCount() + " members (tier " + TribesManager.getTribe(name).getTribeTier() + ")"), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
