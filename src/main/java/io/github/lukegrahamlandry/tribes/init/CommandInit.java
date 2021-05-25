package io.github.lukegrahamlandry.tribes.init;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.commands.*;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static io.github.lukegrahamlandry.tribes.TribesMain.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandInit {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("tribe")
                .requires((context) -> context.hasPermissionLevel(0))
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
                .then(EffectsTribeCommand.register()));

        TribesMain.LOGGER.debug("Tribe commands registered");
    }
}
