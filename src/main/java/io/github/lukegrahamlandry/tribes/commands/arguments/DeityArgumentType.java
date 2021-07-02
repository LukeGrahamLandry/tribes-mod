package io.github.lukegrahamlandry.tribes.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.tribe_data.DeitiesManager;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

// big problem:
// since this uses a greedy string you can only have it as the last argument
// further thinking required on how to fix this

public class DeityArgumentType implements ArgumentType<DeitiesManager.DeityData> {
    public static DeityArgumentType tribe() {
        return new DeityArgumentType();
    }

    public DeitiesManager.DeityData parse(StringReader reader) {
        String key = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        return DeitiesManager.deities.get(key);
    }

    public static <S> DeitiesManager.DeityData getDeity(CommandContext<S> context, String name) {
        try {
            return context.getArgument(name, DeitiesManager.DeityData.class);
        } catch (Exception e){
            if (context.getSource() instanceof CommandSource){
                TextComponent error = new StringTextComponent("Invalid Deity Key");
                Style style = error.getStyle().setColor(Color.fromInt(0xFF0000));
                error.setStyle(style);
                ((CommandSource)context.getSource()).sendFeedback(error, true);
            }
            return null;
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader stringreader = new StringReader(builder.getInput());
        stringreader.setCursor(builder.getStart());
        String s = stringreader.getRemaining();
        stringreader.setCursor(stringreader.getTotalLength());

        stringreader.skipWhitespace();

        TribesMain.LOGGER.debug(s);

        for (String key : DeitiesManager.deities.keySet()){
            if (key.startsWith(s)) builder.suggest(key);
            else if (DeitiesManager.deities.get(key).displayName.startsWith(s)) builder.suggest(key);
        }

        return builder.buildFuture();
    }

    public Collection<String> getExamples() {
        return new ArrayList<>();
    }
}
