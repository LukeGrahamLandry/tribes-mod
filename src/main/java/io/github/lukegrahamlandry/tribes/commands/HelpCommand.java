package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketOpenHelpLink;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public class HelpCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("help")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().asPlayer();
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketOpenHelpLink());
                    return Command.SINGLE_SUCCESS;
                });
    }
}
