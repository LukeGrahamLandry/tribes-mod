package io.github.lukegrahamlandry.tribes.tribe_data;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

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
    IS_PRIVATE,
    CANT_CLAIM_IN_HEMI, MUST_CLICK_BANNER, BANNER_CLAIM_DISABLED, COMMAND_CLAIM_DISABLED, OVERLAPPING_CLAIM, BANNER_NEEDS_AREA;

    public TranslationTextComponent getText(){
        String langEntry = "error.tribes." + this.name().toLowerCase();
        TranslationTextComponent text = new TranslationTextComponent(langEntry);
        Style style = text.getStyle().withColor(Color.fromRgb(0xFF0000));
        text.setStyle(style);
        return text;
    }

    public static TranslationTextComponent getWaitText(long time){
        String langEntry = "error.tribes.wait";
        TranslationTextComponent text = new TranslationTextComponent(langEntry, time);
        Style style = text.getStyle().withColor(Color.fromRgb(0xFF0000));
        text.setStyle(style);
        return text;
    }

    @Override
    public String toString() {
        return "use TranslationTextComponent instead of TribeActionResult#toString";
    }
}
