package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.commands.util.DeityArgumentType;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.init.BannarInit;
import io.github.lukegrahamlandry.tribes.tribe_data.*;
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
                            ctx.getSource().sendSuccess(TribeErrorType.ARG_DEITY.getText(), false);
                                return 0;
                            }))
                .then(Commands.literal("describe")
                        .then(Commands.argument("deity", DeityArgumentType.tribe())
                                .executes(DeityCommands::handleDescribe))
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(TribeErrorType.ARG_DEITY.getText(), false);
                            return 0;
                        }))
                ;
    }

    private static int handleChoose(CommandContext<CommandSource> source) throws CommandSyntaxException {
        DeitiesManager.DeityData deity = DeityArgumentType.getDeity(source, "deity");
        ServerPlayerEntity player = source.getSource().getPlayerOrException();

        if (!TribesManager.playerHasTribe(player.getUUID())){
            source.getSource().sendSuccess(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        } else if (deity != null){
            Tribe tribe = TribesManager.getTribeOf(player.getUUID());

            if (tribe.isLeader(player.getUUID())){
                long timeSinceLastChange = System.currentTimeMillis() - tribe.lastDeityChangeTime;
                if (timeSinceLastChange < TribesConfig.betweenDeityChangeMillis()){
                    long hoursToWait = (TribesConfig.betweenDeityChangeMillis() - timeSinceLastChange) / 1000 / 60 / 60;
                    source.getSource().sendSuccess(TribeErrorType.getWaitText(hoursToWait), true);
                } else {
                    ConfirmCommand.add(player, () -> {
                        tribe.deity = deity.key;
                        tribe.lastDeityChangeTime = System.currentTimeMillis();
                        source.getSource().sendSuccess(TribeSuccessType.CHOOSE_DEITY.getText(deity.displayName), true);
                    });
                }
            } else {
                source.getSource().sendSuccess(TribeErrorType.LOW_RANK.getText(), true);
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int handleList(CommandContext<CommandSource> source) {
        DeitiesManager.deities.forEach((key, data) -> {
            StringBuilder domains = new StringBuilder();
            data.domains.forEach((s) -> domains.append(s).append(", "));
            source.getSource().sendSuccess(TribeSuccessType.DESCRIBE_DEITY.getBlueText(data.displayName, data.label, domains), false);
        });
        return Command.SINGLE_SUCCESS;
    }

    private static int handleDescribe(CommandContext<CommandSource> source) {
        DeitiesManager.DeityData data = DeityArgumentType.getDeity(source, "deity");
        if (data != null){
            StringBuilder domains = new StringBuilder();
            data.domains.forEach((s) -> domains.append(s).append(", "));
            source.getSource().sendSuccess(TribeSuccessType.DESCRIBE_DEITY.getBlueText(data.displayName, data.label, domains), false);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int createBanner(CommandContext<CommandSource> source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getSource().getPlayerOrException();

        if (!TribesManager.playerHasTribe(player.getUUID())){
            source.getSource().sendSuccess(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        } else {
            String deityName = TribesManager.getTribeOf(player.getUUID()).deity;
            if (deityName == null){
                source.getSource().sendSuccess(TribeErrorType.NO_DEITY.getText(), true);
            } else {
                ItemStack banner = player.getItemInHand(Hand.MAIN_HAND);

                if (banner.getItem() instanceof BannerItem){
                    DeitiesManager.DeityData data = DeitiesManager.deities.get(deityName);

                    // dont actually need the BannerPattern here, just hashname
                    BannerPattern bannerpattern = BannarInit.get(data.bannerKey);
                    DyeColor dyecolor = DyeColor.WHITE;
                    CompoundNBT compoundnbt = banner.getOrCreateTagElement("BlockEntityTag");
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

                    player.setItemInHand(Hand.MAIN_HAND, banner);

                    source.getSource().sendSuccess(TribeSuccessType.MAKE_HOLY_BANNER.getText(), false);
                } else {
                    source.getSource().sendSuccess(TribeErrorType.HOLD_BANNER.getText(), true);
                }
            }
        }

        return Command.SINGLE_SUCCESS;
    }


    private static int createBook(CommandContext<CommandSource> source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getSource().getPlayerOrException();

        if (!TribesManager.playerHasTribe(player.getUUID())){
            source.getSource().sendSuccess(TribeErrorType.YOU_NOT_IN_TRIBE.getText(), true);
        } else {
            String deityName = TribesManager.getTribeOf(player.getUUID()).deity;
            if (deityName == null){
                source.getSource().sendSuccess(TribeErrorType.NO_DEITY.getText(), true);
            } else {
                Item currentlyHeld = player.getItemInHand(Hand.MAIN_HAND).getItem();

                if (currentlyHeld == Items.BOOK || currentlyHeld == Items.WRITABLE_BOOK || currentlyHeld == Items.BOOKSHELF){
                    DeitiesManager.DeityData data = DeitiesManager.deities.get(deityName);
                    ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

                    CompoundNBT tag = new CompoundNBT();

                    tag.putString("author", data.bookAuthor);
                    tag.putString("title", data.bookTitle);
                    tag.putBoolean("resolved", true);

                    ListNBT pages = new ListNBT();
                    for (String content : data.bookPages){
                        INBT page = StringNBT.valueOf("{\"text\": \"" + content + "\"}");
                        pages.add(page);
                    }
                    tag.put("pages", pages);

                    book.setTag(tag);

                    player.getItemInHand(Hand.MAIN_HAND).shrink(1);
                    player.drop(book, true);

                    if (currentlyHeld == Items.BOOKSHELF){
                        player.drop(book, true);
                        player.drop(book, true);
                    }

                    source.getSource().sendSuccess(TribeSuccessType.MAKE_HOLY_BOOK.getText(), false);
                } else {
                    source.getSource().sendSuccess(TribeErrorType.HOLD_BOOK.getText(), true);
                }
            }
        }

        return Command.SINGLE_SUCCESS;
    }

}
