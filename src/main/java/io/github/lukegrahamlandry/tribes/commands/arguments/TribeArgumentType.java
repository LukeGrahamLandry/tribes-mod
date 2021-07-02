package io.github.lukegrahamlandry.tribes.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.command.arguments.ItemParser;
import net.minecraft.tags.ItemTags;
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

public class TribeArgumentType implements ArgumentType<Tribe> {
    public static TribeArgumentType tribe() {
        return new TribeArgumentType();
    }

    public Tribe parse(StringReader reader) {
        String tribeName = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        return TribesManager.getTribe(tribeName);
    }

    public static <S> Tribe getTribe(CommandContext<S> context, String name) {
        try {
            return context.getArgument(name, Tribe.class);
        } catch (Exception e){
            if (context.getSource() instanceof CommandSource){
                TextComponent error = new StringTextComponent("Invalid Tribe");
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

        for (Tribe tribe : TribesManager.getTribes()){
            if (tribe.getName().startsWith(s)) builder.suggest(tribe.getName());
        }

        return builder.buildFuture();
    }

    public Collection<String> getExamples() {
        return new ArrayList<>();
    }
}
