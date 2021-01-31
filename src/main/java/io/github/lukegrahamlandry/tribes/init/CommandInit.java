package io.github.lukegrahamlandry.tribes.init;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.commands.CreateTribeCommand;
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
                .then(CreateTribeCommand.register()));

        TribesMain.LOGGER.debug("registerCommands called");
    }
}
