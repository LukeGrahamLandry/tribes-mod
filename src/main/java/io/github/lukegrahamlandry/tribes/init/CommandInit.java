package io.github.lukegrahamlandry.tribes.init;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.commands.*;
import io.github.lukegrahamlandry.tribes.network.PacketOpenMyTribeGUI;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import static io.github.lukegrahamlandry.tribes.TribesMain.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandInit {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("tribe")
                .requires((context) -> context.hasPermission(0))
                .then(CreateTribeCommand.register())
                .then(JoinTribeCommand.register())
                .then(CountTribeCommand.register())
                .then(DeleteTribeCommand.register())
                .then(LeaveTribeCommand.register())
                .then(BanPlayerCommand.register())
                .then(UnbanPlayerCommand.register())
                .then(ListBansCommand.register())
                .then(PromotePlayerCommand.register())
                .then(DemotePlayerCommand.register())
                .then(AllyTribeCommand.register())
                .then(EnemyTribeCommand.register())
                .then(NeutralTribeCommand.register())
                .then(SetInitialsCommand.register())
                .then(WhichTribeCommand.register())
                .then(ChunkClaimCommand.register())
                .then(HemiAccessCommand.register())
                .then(EffectsTribeCommand.register())
                .then(AdminCommands.register())
                .then(ConfirmCommand.register())
                .then(DeityCommands.register())
                .then(AutobanCommands.register())
                .then(HelpCommand.register())
                .then(InviteCommands.register())
                .executes(CommandInit::openMyTribeGUI));

        event.getDispatcher().register(Commands.literal("tribes").executes(CommandInit::openMyTribeGUI));

        TribesMain.LOGGER.debug("Tribe commands registered");
    }

    public static int openMyTribeGUI(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        ServerPlayer player = source.getSource().getPlayerOrException();
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketOpenMyTribeGUI(player));
        return Command.SINGLE_SUCCESS;
    }
}
