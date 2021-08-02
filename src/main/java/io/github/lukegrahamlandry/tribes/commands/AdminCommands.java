package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public class AdminCommands {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("admin")
                .requires(cs-> {
                    String id = cs.getEntity().getUUID().toString();
                    return TribesConfig.isAdmin(id);
                })  // check  here
                .then(Commands.literal("save").executes(AdminCommands::saveData))
                .then(Commands.literal("load").executes(AdminCommands::loadData))
                .then(Commands.literal("delete")
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                            .executes(AdminCommands::handleDelete))
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(TribeErrorType.ARG_TRIBE.getText(), false);
                                return 0;
                            }))
                .then(Commands.literal("rename")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("newname", StringArgumentType.string())
                                        .executes(AdminCommands::handleRename))
                                .executes(ctx -> {
                                    ctx.getSource().sendSuccess(new TextComponent("choose a new name for " + StringArgumentType.getString(ctx, "name")), false);
                                    return 0;
                                }))
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(TribeErrorType.ARG_TRIBE.getText(), false);
                            return 0;
                        }))
                ;
    }

    private static int handleRename(CommandContext<CommandSourceStack> source) {
        String name = StringArgumentType.getString(source, "name");
        String newname = StringArgumentType.getString(source, "newname");

        if (TribesManager.isNameAvailable(name)){
            source.getSource().sendSuccess(TribeErrorType.INVALID_TRIBE.getText(), true);
        } else if (!TribesManager.isNameAvailable(newname)){
            source.getSource().sendSuccess(TribeErrorType.NAME_TAKEN.getText(), true);
        }else {
            TribesManager.renameTribe(name, newname);
            source.getSource().sendSuccess(new TextComponent("The tribe <" + name + "> is now called <" + newname + ">"), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int handleDelete(CommandContext<CommandSourceStack> source) {
        String name = StringArgumentType.getString(source, "name");

        if (TribesManager.isNameAvailable(name)){
            source.getSource().sendSuccess(TribeErrorType.INVALID_TRIBE.getText(), true);
        } else {
            TribesManager.forceDeleteTribe(name);
            source.getSource().sendSuccess(new TextComponent("Tribe deleted: " + name), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int saveData(CommandContext<CommandSourceStack> source) {
        SaveHandler.save(SaveHandler.tribeDataLocation);
        source.getSource().sendSuccess(new TextComponent("tribe data has been saved in " + SaveHandler.tribeDataLocation), true);
        return Command.SINGLE_SUCCESS;
    }

    public static int loadData(CommandContext<CommandSourceStack> source) {
        SaveHandler.load(SaveHandler.tribeDataLocation);
        source.getSource().sendSuccess(new TextComponent("tribe data has been loaded from " + SaveHandler.tribeDataLocation), true);
        return Command.SINGLE_SUCCESS;
    }

}
