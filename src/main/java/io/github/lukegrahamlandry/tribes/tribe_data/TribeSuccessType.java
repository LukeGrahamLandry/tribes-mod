package io.github.lukegrahamlandry.tribes.tribe_data;

import io.github.lukegrahamlandry.tribes.events.TribeServer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.*;

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
    CHOOSE_HEMI;

    public TranslationTextComponent getText(){
        String langEntry = "success.tribes." + this.name().toLowerCase();
        TranslationTextComponent text = new TranslationTextComponent(langEntry);
        Style style = text.getStyle().setColor(Color.fromInt(0x00FF00));
        text.setStyle(style);
        return text;
    }

    public TranslationTextComponent getText(Object... args){
        // convert a passed in tribe to its name string so it can be formated in the lang
        for (int i=0;i<args.length;i++){
            if (args[i] instanceof Tribe){
                args[i] = (Object) ((Tribe) args[i]).getName();
            }
        }

        String langEntry = "success.tribes." + this.name().toLowerCase();
        TranslationTextComponent text = new TranslationTextComponent(langEntry, args);
        Style style = text.getStyle().setColor(Color.fromInt(0x00FF00));
        text.setStyle(style);
        return text;
    }

    // blue
    public ITextComponent getTextPrefixPlayer(UUID causingPlayer, Object... args){
        // convert a passed in tribe to its name string so it can be formated in the lang
        for (int i=0;i<args.length;i++){
            if (args[i] instanceof Tribe){
                args[i] = (Object) ((Tribe) args[i]).getName();
            }
        }

        PlayerEntity player = TribeServer.getPlayerByUuid(causingPlayer);
        if (player == null) return getText(args);

        String langEntry = "success.tribes." + this.name().toLowerCase();
        TranslationTextComponent text = new TranslationTextComponent(langEntry, args);
        Style style = text.getStyle().setColor(Color.fromInt(0x0000FF));
        text.setStyle(style);
        TextComponent name = new StringTextComponent(player.getName().getUnformattedComponentText() + " ");
        Style namestyle = name.getStyle().setBold(true).setColor(Color.fromInt(0xffbb00));
        name.setStyle(namestyle);
        return name.append(text);
    }

    public ITextComponent getBlueText() {
        TextComponent text = getText();
        Style style = text.getStyle().setColor(Color.fromInt(0x0000FF));
        text.setStyle(style);
        return text;
    }

    public ITextComponent getBlueText(Object... args) {
        TextComponent text = getText(args);
        Style style = text.getStyle().setColor(Color.fromInt(0x0000FF));
        text.setStyle(style);
        return text;
    }
}
