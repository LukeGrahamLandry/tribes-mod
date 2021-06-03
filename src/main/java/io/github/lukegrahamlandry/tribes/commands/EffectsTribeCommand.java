package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketOpenEffectGUI;
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
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketOpenEffectGUI(player));
        return Command.SINGLE_SUCCESS;
    }
}
