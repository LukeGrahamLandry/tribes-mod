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
        if (event.getPlayer().getEntityWorld().isRemote()) {
            return;
        }

        Tribe tribe = TribesManager.getTribeOf(event.getPlayer().getUniqueID());
        if (tribe != null){
            TextComponent initials = new StringTextComponent(tribe.getInitials() + " ");
            Style style = initials.getStyle().setBold(true).setColor(Color.fromInt(0xffbb00));
            initials.setStyle(style);
            TextComponent text = (TextComponent) event.getDisplayname();
            text.setStyle(Style.EMPTY.setBold(false).setColor(Color.fromInt(0xFFFFFF)));
            ITextComponent both = initials.append(text);
            event.setDisplayname(both);
        }
    }
}
