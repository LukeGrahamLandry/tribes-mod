package io.github.lukegrahamlandry.tribes.tribe_data;

import io.github.lukegrahamlandry.tribes.events.TribeServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.*;

import java.util.UUID;

public enum TribeSuccessType {
    SUCCESS,
    MADE_TRIBE,
    ALLY_TRIBE,
    ENEMY_TRIBE,
    NEUTRAL_TRIBE,
    CLAIM_CHUNK,
    UNCLAIM_CHUNK,
    BAN_PLAYER,
    UNBAN_PLAYER,
    COUNT_TRIBE,
    DELETE_TRIBE,
    CHOOSE_DEITY,
    DESCRIBE_DEITY,
    MAKE_HOLY_BANNER,
    MAKE_HOLY_BOOK,
    PROMOTE,
    DEMOTE,
    YOU_LEFT,
    SOMEONE_LEFT,
    YOU_JOINED,
    SOMEONE_JOINED,
    SET_INITIALS,
    WHICH_TRIBE,
    WHICH_NO_TRIBE,
    MUST_CONFIRM,
    AUTOBAN_NUMBERS,
    YES_AUTOBAN_RANK,
    NO_AUTOBAN_RANK,
    CHOOSE_HEMI,
    BAN_FOR_DEATHS,
    LIST_BANS,
    NO_BANS,
    INVITE_SENT,
    INVITE_REMOVED,
    NOW_PRIVATE,
    NO_LONGER_PRIVATE,

    ALERT_EFFECTS,
    ALERT_DEITY,
    ALERT_VICE_LEADER,
    ALERT_JOIN;

    public TranslatableComponent getText(){
        String langEntry = "success.tribes." + this.name().toLowerCase();
        TranslatableComponent text = new TranslatableComponent(langEntry);
        Style style = text.getStyle().withColor(TextColor.fromRgb(0x00FF00));
        text.setStyle(style);
        return text;
    }

    public TranslatableComponent getText(Object... args){
        // convert a passed in tribe to its name string so it can be formated in the lang
        for (int i=0;i<args.length;i++){
            if (args[i] instanceof Tribe){
                args[i] = (Object) ((Tribe) args[i]).getName();
            }
            if (args[i] instanceof Player){
                args[i] = ((Player) args[i]).getName().getContents();
            }
        }

        String langEntry = "success.tribes." + this.name().toLowerCase();
        TranslatableComponent text = new TranslatableComponent(langEntry, args);
        Style style = text.getStyle().withColor(TextColor.fromRgb(0x00FF00));
        text.setStyle(style);
        return text;
    }

    // blue
    public Component getTextPrefixPlayer(UUID causingPlayer, Object... args){
        // convert a passed in tribe to its name string so it can be formated in the lang
        for (int i=0;i<args.length;i++){
            if (args[i] instanceof Tribe){
                args[i] = (Object) ((Tribe) args[i]).getName();
            }
        }

        Player player = TribeServer.getPlayerByUuid(causingPlayer);
        if (player == null) return getText(args);

        String langEntry = "success.tribes." + this.name().toLowerCase();
        TranslatableComponent text = new TranslatableComponent(langEntry, args);
        Style style = text.getStyle().withColor(TextColor.fromRgb(0x34e5eb));
        text.setStyle(style);
        TextComponent name = new TextComponent(player.getName().getContents() + " ");
        Style namestyle = name.getStyle().withBold(true).withColor(TextColor.fromRgb(0xffbb00));
        name.setStyle(namestyle);
        return name.append(text);
    }

    public Component getBlueText() {
        TranslatableComponent text = getText();
        Style style = text.getStyle().withColor(TextColor.fromRgb(0x34e5eb));
        text.setStyle(style);
        return text;
    }

    public Component getBlueText(Object... args) {
        TranslatableComponent text = getText(args);
        Style style = text.getStyle().withColor(TextColor.fromRgb(0x34e5eb));
        text.setStyle(style);
        return text;
    }
}
