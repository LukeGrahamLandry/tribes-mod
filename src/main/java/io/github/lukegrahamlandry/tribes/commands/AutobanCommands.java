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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class AutobanCommands {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("autoban")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
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

    public static int handleSet(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();
        Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());
        if (tribe != null){
            if (tribe.getRankOf(player.getUniqueID().toString()) == Tribe.Rank.LEADER){
                int numDeaths = IntegerArgumentType.getInteger(source, "numDeaths");
                int numDays = IntegerArgumentType.getInteger(source, "numDays");

                tribe.autobanDeathThreshold = numDeaths;
                tribe.autobanDaysThreshold = numDeaths;

                source.getSource().sendFeedback(TribeSuccessType.AUTOBAN_NUMBERS.getText(numDeaths, numDays), true);
            } else {
                source.getSource().sendFeedback(TribeErrorType.LOW_RANK.getText(), true);
            }

        } else {
            source.getSource().sendFeedback(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int handleRankSettings(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();
        Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());
        if (tribe != null){
            if (tribe.getRankOf(player.getUniqueID().toString()) == Tribe.Rank.LEADER){
                String rankName = StringArgumentType.getString(source, "rank");
                Tribe.Rank rank = Tribe.Rank.fromString(rankName);
                if (rank != null){
                    boolean value = BoolArgumentType.getBool(source, "value");

                    tribe.autobanRank.put(rank, value);

                    String not = value ? "" : "not";
                    if (value){
                        source.getSource().sendFeedback(TribeSuccessType.YES_AUTOBAN_RANK.getText(rankName), true);
                    } else {
                        source.getSource().sendFeedback(TribeSuccessType.NO_AUTOBAN_RANK.getText(rankName), true);
                    }
                } else {
                    source.getSource().sendFeedback(TribeErrorType.INVALID_RANK.getText(), true);
                }
            } else {
                source.getSource().sendFeedback(TribeErrorType.LOW_RANK.getText(), true);
            }

        } else {
            source.getSource().sendFeedback(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
