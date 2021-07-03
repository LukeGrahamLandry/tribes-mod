package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketOpenEffectGUI;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class EffectsTribeCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("effects")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .executes(EffectsTribeCommand::handleeffects);

    }

    public static int handleeffects(CommandContext<CommandSource> source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getSource().asPlayer();

        Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());
        if (tribe != null){
            long timePassed = System.currentTimeMillis() - tribe.lastEffectsChangeTime;
            long timeToWait = TribesConfig.betweenEffectsChangeMillis() - timePassed;
            if (timeToWait > 0){
                source.getSource().sendFeedback(new StringTextComponent("error: you must wait " + (timeToWait / 1000 / 60 / 60) + " hours before changing your effects"), true);
                return Command.SINGLE_SUCCESS;
            }

            if (!tribe.isLeader(player.getUniqueID())){
                source.getSource().sendFeedback(TribeActionResult.LOW_RANK.getErrorComponent(), true);
                return Command.SINGLE_SUCCESS;
            }

            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketOpenEffectGUI(player));
        } else {
            source.getSource().sendFeedback(TribeActionResult.YOU_NOT_IN_TRIBE.getErrorComponent(), true);
        }


        return Command.SINGLE_SUCCESS;

    }
}
