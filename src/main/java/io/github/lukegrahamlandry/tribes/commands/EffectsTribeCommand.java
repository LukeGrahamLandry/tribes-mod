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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.network.PacketDistributor;

public class EffectsTribeCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("effects")
                .requires(cs->cs.hasPermission(0)) //permission
                .executes(EffectsTribeCommand::handleeffects);

    }

    public static int handleeffects(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        ServerPlayer player = source.getSource().getPlayerOrException();

        Tribe tribe = TribesManager.getTribeOf(player.getUUID());
        if (tribe != null){
            long timePassed = System.currentTimeMillis() - tribe.lastEffectsChangeTime;
            long timeToWait = TribesConfig.betweenEffectsChangeMillis() - timePassed;
            if (timeToWait > 0){
                long hours = timeToWait / 1000 / 60 / 60;
                source.getSource().sendSuccess(TribeErrorType.getWaitText(hours), true);
                return Command.SINGLE_SUCCESS;
            }

            if (!tribe.isLeader(player.getUUID())){
                source.getSource().sendSuccess(TribeErrorType.LOW_RANK.getText(), true);
                return Command.SINGLE_SUCCESS;
            }

            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketOpenEffectGUI(player));
        } else {
            source.getSource().sendSuccess(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        }


        return Command.SINGLE_SUCCESS;

    }
}
