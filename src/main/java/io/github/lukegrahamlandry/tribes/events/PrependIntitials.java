package io.github.lukegrahamlandry.tribes.events;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.util.text.*;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PrependIntitials {
    /*
    @SubscribeEvent
    public static void addInitialsToChat(ServerChatEvent event){
        Tribe tribe = TribesManager.getTribeOf(event.getPlayer().getUniqueID());
        if (tribe != null){
            ITextComponent initials = new StringTextComponent("(")
            ITextComponent text = event.getComponent();


        }
    }
     */

    @SubscribeEvent
    public static void addToDisplayName(PlayerEvent.NameFormat event){
        if (event.getPlayer().getCommandSenderWorld().isClientSide()) {
            return;
        }

        Tribe tribe = TribesManager.getTribeOf(event.getPlayer().getUUID());
        if (tribe != null){
            TextComponent initials = new StringTextComponent(tribe.getInitials() + " ");
            Style style = initials.getStyle().withBold(true).withColor(Color.fromRgb(0xffbb00));
            initials.setStyle(style);
            TextComponent text = (TextComponent) event.getDisplayname();
            text.setStyle(Style.EMPTY.withBold(false).withColor(Color.fromRgb(0xFFFFFF)));
            ITextComponent both = initials.append(text);
            event.setDisplayname(both);
        }
    }
}
