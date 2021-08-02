package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class AutobanCommands {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("autoban")
                .requires(cs->cs.hasPermission(0)) //permission
                .then(Commands.literal("set")
                        .then(Commands.argument("numDeaths", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                .then(Commands.argument("numDays", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(AutobanCommands::handleSet))))
                .then(Commands.literal("rank")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                        .then(Commands.argument("rank", StringArgumentType.greedyString())
                                        .executes(AutobanCommands::handleRankSettings)))
        );
    }

    public static int handleSet(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Player player = source.getSource().getPlayerOrException();
        Tribe tribe = TribesManager.getTribeOf(player.getUUID());
        if (tribe != null){
            if (tribe.getRankOf(player.getUUID().toString()) == Tribe.Rank.LEADER){
                int numDeaths = IntegerArgumentType.getInteger(source, "numDeaths");
                int numDays = IntegerArgumentType.getInteger(source, "numDays");

                tribe.autobanDeathThreshold = numDeaths;
                tribe.autobanDaysThreshold = numDeaths;

                source.getSource().sendSuccess(TribeSuccessType.AUTOBAN_NUMBERS.getText(numDeaths, numDays), true);
            } else {
                source.getSource().sendSuccess(TribeErrorType.LOW_RANK.getText(), true);
            }

        } else {
            source.getSource().sendSuccess(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int handleRankSettings(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Player player = source.getSource().getPlayerOrException();
        Tribe tribe = TribesManager.getTribeOf(player.getUUID());
        if (tribe != null){
            if (tribe.getRankOf(player.getUUID().toString()) == Tribe.Rank.LEADER){
                String rankName = StringArgumentType.getString(source, "rank");
                Tribe.Rank rank = Tribe.Rank.fromString(rankName);
                if (rank != null){
                    boolean value = BoolArgumentType.getBool(source, "value");

                    tribe.autobanRank.put(rank, value);

                    String not = value ? "" : "not";
                    if (value){
                        source.getSource().sendSuccess(TribeSuccessType.YES_AUTOBAN_RANK.getText(rankName), true);
                    } else {
                        source.getSource().sendSuccess(TribeSuccessType.NO_AUTOBAN_RANK.getText(rankName), true);
                    }
                } else {
                    source.getSource().sendSuccess(TribeErrorType.INVALID_RANK.getText(), true);
                }
            } else {
                source.getSource().sendSuccess(TribeErrorType.LOW_RANK.getText(), true);
            }

        } else {
            source.getSource().sendSuccess(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
