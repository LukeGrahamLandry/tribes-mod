package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketOpenHelpLink;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.network.PacketDistributor;

public class HelpCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("help")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketOpenHelpLink());
                    return Command.SINGLE_SUCCESS;
                });
    }
}
