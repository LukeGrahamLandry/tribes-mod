package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketOpenJoinGUI;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

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

                source.getSource().sendFeedback(new StringTextComponent("your tribe will autoban people who die " + numDeaths + " times within " + numDays + " RL days"), true);
            } else {
                source.getSource().sendFeedback(new StringTextComponent("you are not the leader of your tribe"), true);
            }

        } else {
            source.getSource().sendFeedback(new StringTextComponent("you have no tribe"), true);
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
                    source.getSource().sendFeedback(new StringTextComponent("your tribe will " + not + " autoban " + rankName + "s if they die too often"), true);

                } else {
                    source.getSource().sendFeedback(new StringTextComponent("that is not a valid tribe rank"), true);
                }
            } else {
                source.getSource().sendFeedback(new StringTextComponent("you are not the leader of your tribe"), true);
            }

        } else {
            source.getSource().sendFeedback(new StringTextComponent("you have no tribe"), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
