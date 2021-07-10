package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.commands.util.TribeArgumentType;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class CountTribeCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("count")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("tribe", TribeArgumentType.tribe())
                        .executes(CountTribeCommand::handleCount)
                ).executes(ctx -> {
                    ctx.getSource().sendFeedback(TribeErrorType.ARG_TRIBE.getText(), false);
                            return 0;
                        }
                );

    }

    public static int handleCount(CommandContext<CommandSource> source) throws CommandSyntaxException {
        Tribe tribe = TribeArgumentType.getTribe(source, "tribe");

        if (tribe != null) {
            source.getSource().sendFeedback(TribeSuccessType.COUNT_TRIBE.getBlueText(tribe, tribe.getCount(), tribe.getTribeTier()), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
