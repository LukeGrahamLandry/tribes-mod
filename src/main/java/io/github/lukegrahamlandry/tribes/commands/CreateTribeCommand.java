package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
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

public class CreateTribeCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("create")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(CreateTribeCommand::handleCreate)
                ).executes(ctx -> {
                            ctx.getSource().sendFeedback(new StringTextComponent("pick a name"), false);
                            return 0;
                        }
                );

    }

    public static int handleCreate(CommandContext<CommandSource> source) throws CommandSyntaxException {
        TribesMain.LOGGER.debug("handleCommand called");

        PlayerEntity player = source.getSource().asPlayer();
        String name = StringArgumentType.getString(source, "name");

        TribeActionResult response = TribesManager.createNewTribe(name, player);
        if (response == TribeActionResult.SUCCESS){
            source.getSource().sendFeedback(new StringTextComponent("Tribe successfully created: " + name), true);
        } else {
            source.getSource().sendFeedback(new StringTextComponent(response.toString()), true);
        }


        return Command.SINGLE_SUCCESS;
    }
}
