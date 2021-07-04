package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketOpenEffectGUI;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
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
                long hours = timeToWait / 1000 / 60 / 60;
                source.getSource().sendFeedback(TribeErrorType.getWaitText(hours), true);
                return Command.SINGLE_SUCCESS;
            }

            if (!tribe.isLeader(player.getUniqueID())){
                source.getSource().sendFeedback(TribeErrorType.LOW_RANK.getText(), true);
                return Command.SINGLE_SUCCESS;
            }

            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketOpenEffectGUI(player));
        } else {
            source.getSource().sendFeedback(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        }


        return Command.SINGLE_SUCCESS;

    }
}
