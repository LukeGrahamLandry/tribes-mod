package io.github.lukegrahamlandry.tribes.tribe_data;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

public enum TribeActionResult {
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
    INVALID_DEITY;


    public TranslationTextComponent getComponent(){
        String langEntry = "error.tribes." + this.name().toLowerCase();
        return new TranslationTextComponent(langEntry);
    }

    public TranslationTextComponent getErrorComponent(){
        TranslationTextComponent text = this.getComponent();
        Style style = text.getStyle().setColor(Color.fromInt(0xFF0000));
        text.setStyle(style);
        return text;
    }

    @Override
    public String toString() {
        return "use TranslationTextComponent instead of TribeActionResult#toString";
    }
}
