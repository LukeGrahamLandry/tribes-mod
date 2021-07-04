package io.github.lukegrahamlandry.tribes.commands.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;

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
                ((CommandSource)context.getSource()).sendFeedback(TribeErrorType.INVALID_TRIBE.getText(), true);
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
