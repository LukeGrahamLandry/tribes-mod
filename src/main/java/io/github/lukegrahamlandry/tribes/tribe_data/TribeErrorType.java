package io.github.lukegrahamlandry.tribes.tribe_data;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Style;

public enum TribeErrorType {
    SUCCESS,
    NAME_TAKEN,
    IN_TRIBE,
    LONG_NAME,
    CLIENT,
    INVALID_TRIBE,
    LOW_RANK,
    YOU_NOT_IN_TRIBE,
    BANNED,
    RANK_DOESNT_EXIST,
    THEY_NOT_IN_TRIBE,
    SAME_TRIBE,
    CONFIG,
    ALREADY_CLAIMED,
    HAVE_HEMI,
    INVALID_HEMI,
    WEAK_TRIBE,
    NO_CONFIRM,
    INVALID_DEITY,
    WAIT,
    NO_DEITY,
    HOLD_BOOK,
    HOLD_BANNER,
    ARG_TRIBE,
    ARG_PLAYER,
    ARG_DEITY,
    ARG_MISSING,
    INVALID_RANK,
    NOT_PRIVATE,
    IS_PRIVATE;

    public TranslatableComponent getText(){
        String langEntry = "error.tribes." + this.name().toLowerCase();
        TranslatableComponent text = new TranslatableComponent(langEntry);
        Style style = text.getStyle().withColor(TextColor.fromRgb(0xFF0000));
        text.setStyle(style);
        return text;
    }

    public static TranslatableComponent getWaitText(long time){
        String langEntry = "error.tribes.wait";
        TranslatableComponent text = new TranslatableComponent(langEntry, time);
        Style style = text.getStyle().withColor(TextColor.fromRgb(0xFF0000));
        text.setStyle(style);
        return text;
    }

    @Override
    public String toString() {
        return "use TranslationTextComponent instead of TribeActionResult#toString";
    }
}
