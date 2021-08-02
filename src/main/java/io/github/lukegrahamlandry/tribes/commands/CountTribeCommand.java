package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.commands.util.TribeArgumentType;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CountTribeCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("count")
                .requires(cs->cs.hasPermission(0)) //permission
                .then(Commands.argument("tribe", TribeArgumentType.tribe())
                        .executes(CountTribeCommand::handleCount)
                ).executes(ctx -> {
                    ctx.getSource().sendSuccess(TribeErrorType.ARG_TRIBE.getText(), false);
                            return 0;
                        }
                );

    }

    public static int handleCount(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Tribe tribe = TribeArgumentType.getTribe(source, "tribe");

        if (tribe != null) {
            source.getSource().sendSuccess(TribeSuccessType.COUNT_TRIBE.getBlueText(tribe, tribe.getCount(), tribe.getTribeTier()), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
