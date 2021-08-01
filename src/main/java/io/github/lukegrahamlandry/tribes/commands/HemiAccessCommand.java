package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class HemiAccessCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("hemisphere")
                .requires(cs->cs.hasPermission(0)) //permission
                .then(Commands.argument("side", StringArgumentType.word())
                        .executes(HemiAccessCommand::handleSelect)
                ).executes(ctx -> {
                        String types = TribesConfig.getUseNorthSouthHemisphereDirection() ? "north / south" : "east / west";
                        ctx.getSource().sendSuccess(new StringTextComponent("pick which hemi to access (" + types + ")"), false);
                        return 0;
                    }
                );

    }

    public static int handleSelect(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().getPlayerOrException();
        String side = StringArgumentType.getString(source, "side");

        Tribe tribe = TribesManager.getTribeOf(player.getUUID());
        TribeErrorType response = tribe.validateSelectHemi(player, side);

        if (response == TribeErrorType.SUCCESS){
            ConfirmCommand.add(player, () -> {
                tribe.selectHemi(player, side);
            });
        } else {
            source.getSource().sendSuccess(response.getText(), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
