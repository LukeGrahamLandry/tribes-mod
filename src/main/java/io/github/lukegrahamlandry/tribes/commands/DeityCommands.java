package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.DeitiesManager;
import io.github.lukegrahamlandry.tribes.tribe_data.SaveHandler;
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
                .then(Commands.literal("load").executes(DeityCommands::loadData))
                .then(Commands.literal("delete")
                        .then(Commands.argument("name", StringArgumentType.word())
                            .executes(DeityCommands::handleDelete))
                        .executes(ctx -> {
                                ctx.getSource().sendFeedback(new StringTextComponent("choose a tribe to delete"), false);
                                return 0;
                            }))
                .then(Commands.literal("rename")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("newname", StringArgumentType.word())
                                        .executes(DeityCommands::handleRename))
                                .executes(ctx -> {
                                    ctx.getSource().sendFeedback(new StringTextComponent("choose a new name for " + StringArgumentType.getString(ctx, "name")), false);
                                    return 0;
                                }))
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(new StringTextComponent("choose a tribe to rename"), false);
                            return 0;
                        }))
                ;
    }

    private static int handleRename(CommandContext<CommandSource> source) {
        String name = StringArgumentType.getString(source, "name");
        String newname = StringArgumentType.getString(source, "newname");

        if (TribesManager.isNameAvailable(name)){
            source.getSource().sendFeedback(new StringTextComponent("Tribe <" + name + "> does not exist"), true);
        } else if (!TribesManager.isNameAvailable(newname)){
            source.getSource().sendFeedback(new StringTextComponent("The name <" + newname + "> is already taken"), true);
        }else {
            TribesManager.renameTribe(name, newname);
            source.getSource().sendFeedback(new StringTextComponent("The tribe <" + name + "> is now called <" + newname + ">"), true);
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

    public static int saveData(CommandContext<CommandSource> source) {
        SaveHandler.save(SaveHandler.tribeDataLocation);
        source.getSource().sendFeedback(new StringTextComponent("tribe data has been saved in " + SaveHandler.tribeDataLocation), true);
        return Command.SINGLE_SUCCESS;
    }

    public static int loadData(CommandContext<CommandSource> source) {
        SaveHandler.load(SaveHandler.tribeDataLocation);
        source.getSource().sendFeedback(new StringTextComponent("tribe data has been loaded from " + SaveHandler.tribeDataLocation), true);
        return Command.SINGLE_SUCCESS;
    }

}
