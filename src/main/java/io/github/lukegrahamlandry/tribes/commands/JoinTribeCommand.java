package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.commands.arguments.TribeArgumentType;
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

import static io.github.lukegrahamlandry.tribes.tribe_data.TribesManager.playerHasTribe;

public class JoinTribeCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("join")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .then(Commands.argument("tribe", TribeArgumentType.tribe())
                        .executes(JoinTribeCommand::handleJoin)
                ).executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().asPlayer();
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketOpenJoinGUI(player));
                    return Command.SINGLE_SUCCESS;
                });
    }

    public static int handleJoin(CommandContext<CommandSource> source) throws CommandSyntaxException {
        PlayerEntity player = source.getSource().asPlayer();
        Tribe tribe = TribeArgumentType.getTribe(source, "tribe");
        if (tribe == null) return 1;

        TribeActionResult response;
        if (playerHasTribe(player.getUniqueID())) response = TribeActionResult.IN_TRIBE;
        else response = tribe.addMember(player.getUniqueID(), Tribe.Rank.MEMBER);

        if (response == TribeActionResult.SUCCESS){
            source.getSource().sendFeedback(new StringTextComponent("Tribe successfully joined: " + tribe.getName()), true);
        } else {
            source.getSource().sendFeedback(new StringTextComponent(response.toString()), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
