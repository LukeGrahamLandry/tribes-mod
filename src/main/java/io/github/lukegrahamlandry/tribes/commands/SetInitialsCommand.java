package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class SetInitialsCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("initials")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(SetInitialsCommand::handleCreate)
                ).executes(ctx -> {
                            ctx.getSource().sendFeedback(new StringTextComponent("pick initials for your tribe"), false);
                            return 0;
                        }
                );

    }

    public static int handleCreate(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();
        String str = StringArgumentType.getString(source, "name");

        Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());
        TribeErrorType response = tribe.trySetInitials(str, player.getUniqueID());
        if (response == TribeErrorType.SUCCESS){
            source.getSource().sendFeedback(new StringTextComponent("Your tribe's initials are now: " + str), true);
        } else {
            source.getSource().sendFeedback(response.getText(), true);
        }


        return Command.SINGLE_SUCCESS;
    }
}
