package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.commands.util.DeityArgumentType;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.init.BannarInit;
import io.github.lukegrahamlandry.tribes.tribe_data.DeitiesManager;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;

public class DeityCommands {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("deity")
                .then(Commands.literal("book").executes(DeityCommands::createBook))
                .then(Commands.literal("list").executes(DeityCommands::handleList))
                .then(Commands.literal("banner").executes(DeityCommands::createBanner))
                .then(Commands.literal("choose")
                        .then(Commands.argument("deity", DeityArgumentType.tribe())
                            .executes(DeityCommands::handleChoose))
                        .executes(ctx -> {
                                ctx.getSource().sendFeedback(new StringTextComponent("choose a deity to follow"), false);
                                return 0;
                            }))
                .then(Commands.literal("describe")
                        .then(Commands.argument("deity", DeityArgumentType.tribe())
                                .executes(DeityCommands::handleDescribe))
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(new StringTextComponent("choose a deity to describe"), false);
                            return 0;
                        }))
                ;
    }

    private static int handleChoose(CommandContext<CommandSource> source) throws CommandSyntaxException {
        DeitiesManager.DeityData deity = DeityArgumentType.getDeity(source, "deity");
        ServerPlayerEntity player = source.getSource().asPlayer();

        if (!TribesManager.playerHasTribe(player.getUniqueID())){
            source.getSource().sendFeedback(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        } else if (deity != null){
            Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());

            if (tribe.isLeader(player.getUniqueID())){
                long timeSinceLastChange = System.currentTimeMillis() - tribe.lastDeityChangeTime;
                if (timeSinceLastChange < TribesConfig.betweenDeityChangeMillis()){
                    long hoursToWait = (TribesConfig.betweenDeityChangeMillis() - timeSinceLastChange) / 1000 / 60 / 60;
                    source.getSource().sendFeedback(TribeErrorType.getWaitText(hoursToWait), true);
                } else {
                    ConfirmCommand.add(player, () -> {
                        tribe.deity = deity.key;
                        tribe.lastDeityChangeTime = System.currentTimeMillis();
                        source.getSource().sendFeedback(new StringTextComponent("your tribe now follows " + deity.displayName), true);
                    });
                }
            } else {
                source.getSource().sendFeedback(TribeErrorType.LOW_RANK.getText(), true);
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int handleList(CommandContext<CommandSource> source) {
        DeitiesManager.deities.forEach((key, data) -> {
            StringBuilder domains = new StringBuilder();
            data.domains.forEach((s) -> domains.append(s).append(", "));
            source.getSource().sendFeedback(new StringTextComponent( data.displayName + " is the " + data.label + " of " + domains), true);
        });
        return Command.SINGLE_SUCCESS;
    }

    private static int handleDescribe(CommandContext<CommandSource> source) {
        DeitiesManager.DeityData data = DeityArgumentType.getDeity(source, "deity");
        if (data != null){
            StringBuilder domains = new StringBuilder();
            data.domains.forEach((s) -> domains.append(s).append(", "));
            source.getSource().sendFeedback(new StringTextComponent(data.displayName + " is the " + data.label + " of " + domains), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int createBanner(CommandContext<CommandSource> source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getSource().asPlayer();

        if (!TribesManager.playerHasTribe(player.getUniqueID())){
            source.getSource().sendFeedback(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        } else {
            String deityName = TribesManager.getTribeOf(player.getUniqueID()).deity;
            if (deityName == null){
                source.getSource().sendFeedback(TribeErrorType.NO_DEITY.getText(), true);
            } else {
                ItemStack banner = player.getHeldItem(Hand.MAIN_HAND);

                if (banner.getItem() instanceof BannerItem){
                    DeitiesManager.DeityData data = DeitiesManager.deities.get(deityName);

                    // dont actually need the BannerPattern here, just hashname
                    BannerPattern bannerpattern = BannarInit.get(data.bannerKey);
                    DyeColor dyecolor = DyeColor.WHITE;
                    CompoundNBT compoundnbt = banner.getOrCreateChildTag("BlockEntityTag");
                    ListNBT listnbt;
                    if (compoundnbt.contains("Patterns", 9)) {
                        listnbt = compoundnbt.getList("Patterns", 10);
                    } else {
                        listnbt = new ListNBT();
                        compoundnbt.put("Patterns", listnbt);
                    }

                    CompoundNBT compoundnbt1 = new CompoundNBT();
                    compoundnbt1.putString("Pattern", bannerpattern.getHashname());
                    compoundnbt1.putInt("Color", dyecolor.getId());
                    listnbt.add(compoundnbt1);

                    player.setHeldItem(Hand.MAIN_HAND, banner);

                    source.getSource().sendFeedback(new StringTextComponent("holy banner created"), true);
                } else {
                    source.getSource().sendFeedback(TribeErrorType.HOLD_BANNER.getText(), true);
                }
            }
        }

        return Command.SINGLE_SUCCESS;
    }


    private static int createBook(CommandContext<CommandSource> source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getSource().asPlayer();

        if (!TribesManager.playerHasTribe(player.getUniqueID())){
            source.getSource().sendFeedback(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        } else {
            String deityName = TribesManager.getTribeOf(player.getUniqueID()).deity;
            if (deityName == null){
                source.getSource().sendFeedback(TribeErrorType.NO_DEITY.getText(), true);
            } else {
                Item currentlyHeld = player.getHeldItem(Hand.MAIN_HAND).getItem();

                if (currentlyHeld == Items.BOOK || currentlyHeld == Items.WRITABLE_BOOK || currentlyHeld == Items.BOOKSHELF){
                    DeitiesManager.DeityData data = DeitiesManager.deities.get(deityName);
                    ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

                    CompoundNBT tag = new CompoundNBT();

                    tag.putString("author", data.bookAuthor);
                    tag.putString("title", data.bookTitle);
                    tag.putBoolean("resolved", true);

                    TribesMain.LOGGER.debug(data.bookPages);

                    ListNBT pages = new ListNBT();
                    for (String content : data.bookPages){
                        INBT page = StringNBT.valueOf("{\"text\": \"" + content + "\"}");
                        pages.add(page);
                    }
                    TribesMain.LOGGER.debug(pages);
                    tag.put("pages", pages);

                    book.setTag(tag);

                    player.getHeldItem(Hand.MAIN_HAND).shrink(1);
                    player.dropItem(book, true);

                    if (currentlyHeld == Items.BOOKSHELF){
                        player.dropItem(book, true);
                        player.dropItem(book, true);
                    }

                    source.getSource().sendFeedback(new StringTextComponent("holy book created"), true);
                } else {
                    source.getSource().sendFeedback(TribeErrorType.HOLD_BOOK.getText(), true);
                }
            }
        }

        return Command.SINGLE_SUCCESS;
    }

}
