package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.DeitiesManager;
import io.github.lukegrahamlandry.tribes.tribe_data.SaveHandler;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;

public class DeityCommands {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("deity")
                .then(Commands.literal("book").executes(DeityCommands::createBook))
                .then(Commands.literal("list").executes(DeityCommands::handleList))
                .then(Commands.literal("choose")
                        .then(Commands.argument("name", StringArgumentType.word())
                            .executes(DeityCommands::handleChoose))
                        .executes(ctx -> {
                                ctx.getSource().sendFeedback(new StringTextComponent("choose a deity to follow"), false);
                                return 0;
                            }))
                .then(Commands.literal("describe")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(DeityCommands::handleDescribe))
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(new StringTextComponent("choose a deity to describe"), false);
                            return 0;
                        }))
                ;
    }

    private static int handleChoose(CommandContext<CommandSource> source) throws CommandSyntaxException {
        String name = StringArgumentType.getString(source, "name");
        ServerPlayerEntity player = source.getSource().asPlayer();

        if (!TribesManager.playerHasTribe(player.getUniqueID())){
            source.getSource().sendFeedback(new StringTextComponent("error: you do not have a tribe"), true);
        } else if (DeitiesManager.deities.containsKey(name)){
            Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());

            if (tribe.isLeader(player.getUniqueID())){
                long timeSinceLastChange = System.currentTimeMillis() - tribe.lastDeityChangeTime;
                if (timeSinceLastChange < TribesConfig.betweenDeityChangeMillis()){
                    long hoursToWait = (TribesConfig.betweenDeityChangeMillis() - timeSinceLastChange) / 1000 / 60 / 60;
                    source.getSource().sendFeedback(new StringTextComponent("error: you must wait " + hoursToWait + " hours before changing your deity"), true);
                } else {
                    ConfirmCommand.add(player, () -> {
                        tribe.deity = name;
                        tribe.lastDeityChangeTime = System.currentTimeMillis();
                        source.getSource().sendFeedback(new StringTextComponent("your tribe now follows " + DeitiesManager.deities.get(name).displayName), true);
                    });
                }
            } else {
                source.getSource().sendFeedback(new StringTextComponent("error: you are too low a rank to choose your tribe's deity"), true);
            }
        } else {
            source.getSource().sendFeedback(new StringTextComponent("The deity <" + name + "> does not exist. make sure you are using thier key not display name"), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int handleList(CommandContext<CommandSource> source) {
        DeitiesManager.deities.forEach((key, data) -> {
            StringBuilder domains = new StringBuilder();
            data.domains.forEach((s) -> domains.append(s).append(", "));
            source.getSource().sendFeedback(new StringTextComponent(key + ": " + data.displayName + " is the " + data.label + " of " + domains), true);
        });
        return Command.SINGLE_SUCCESS;
    }

    private static int handleDescribe(CommandContext<CommandSource> source) {
        String name = StringArgumentType.getString(source, "name");
        if (DeitiesManager.deities.containsKey(name)){
            DeitiesManager.DeityData data = DeitiesManager.deities.get(name);
            StringBuilder domains = new StringBuilder();
            data.domains.forEach((s) -> domains.append(s).append(", "));
            source.getSource().sendFeedback(new StringTextComponent(data.displayName + " is the " + data.label + " of " + domains), true);

        }

        return Command.SINGLE_SUCCESS;
    }

    private static int createBook(CommandContext<CommandSource> source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getSource().asPlayer();

        if (!TribesManager.playerHasTribe(player.getUniqueID())){
            source.getSource().sendFeedback(new StringTextComponent("error: you do not have a tribe"), true);
        } else {
            String deityName = TribesManager.getTribeOf(player.getUniqueID()).deity;
            if (deityName == null){
                source.getSource().sendFeedback(new StringTextComponent("error: your tribe has not chosen a deity"), true);
            } else {
                Item currentlyHeld = player.getHeldItem(Hand.MAIN_HAND).getItem();

                if (currentlyHeld == Items.BOOK || currentlyHeld == Items.WRITABLE_BOOK || currentlyHeld == Items.BOOKSHELF){
                    DeitiesManager.DeityData data = DeitiesManager.deities.get(deityName);
                    ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

                    CompoundNBT tag = new CompoundNBT();

                    tag.putString("author", data.bookAuthor);
                    tag.putString("title", data.bookAuthor);
                    tag.putBoolean("resolved", true);

                    ListNBT pages = new ListNBT();
                    for (String content : data.bookPages){
                        INBT page = StringNBT.valueOf("{\"text\": \"" + content + "\"}");
                        pages.add(page);
                    }
                    tag.put("pages", pages);

                    book.setTag(tag);

                    player.setHeldItem(Hand.MAIN_HAND, book);

                    if (currentlyHeld == Items.BOOKSHELF){
                        player.dropItem(book, true);
                        player.dropItem(book, true);
                    }

                    source.getSource().sendFeedback(new StringTextComponent("holy book created"), true);
                } else {
                    source.getSource().sendFeedback(new StringTextComponent("you are not holding a book"), true);
                }
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
