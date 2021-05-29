package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class HemiAccessCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("hemisphere")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("side", StringArgumentType.word())
                        .executes(HemiAccessCommand::handleSelect)
                ).executes(ctx -> {
                        String types = TribesConfig.getUseNorthSouthHemisphereDirection() ? "north / south" : "east / west";
                        ctx.getSource().sendFeedback(new StringTextComponent("pick which hemi to access (" + types + ")"), false);
                        return 0;
                    }
                );

    }

    public static int handleSelect(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();
        String side = StringArgumentType.getString(source, "side");

        Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());
        TribeActionResult response = tribe.validateSelectHemi(player, side);

        if (response == TribeActionResult.SUCCESS){
            source.getSource().sendFeedback(new StringTextComponent("are you sure you want to choose " + side + "? your tribe's hemisphere can never be changed"), true);

            ConfirmCommand.add(player, () -> {
                tribe.selectHemi(player, side);
                source.getSource().sendFeedback(new StringTextComponent("your tribe has claimed: " + side), true);
            });
        } else {
            source.getSource().sendFeedback(new StringTextComponent(response.toString()), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
