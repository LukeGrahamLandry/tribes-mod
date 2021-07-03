package io.github.lukegrahamlandry.tribes.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.*;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class AdminCommands {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("admin")
                .requires(cs-> {
                    String id = cs.getEntity().getUniqueID().toString();
                    return TribesConfig.isAdmin(id);
                })  // check  here
                .then(Commands.literal("save").executes(AdminCommands::saveData))
                .then(Commands.literal("load").executes(AdminCommands::loadData))
                .then(Commands.literal("delete")
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                            .executes(AdminCommands::handleDelete))
                        .executes(ctx -> {
                                ctx.getSource().sendFeedback(new StringTextComponent("choose a tribe to delete"), false);
                                return 0;
                            }))
                .then(Commands.literal("rename")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("newname", StringArgumentType.string())
                                        .executes(AdminCommands::handleRename))
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
            source.getSource().sendFeedback(TribeActionResult.INVALID_TRIBE.getErrorComponent(), true);
        } else if (!TribesManager.isNameAvailable(newname)){
            source.getSource().sendFeedback(TribeActionResult.NAME_TAKEN.getErrorComponent(), true);
        }else {
            TribesManager.renameTribe(name, newname);
            source.getSource().sendFeedback(new StringTextComponent("The tribe <" + name + "> is now called <" + newname + ">"), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int handleDelete(CommandContext<CommandSource> source) {
        String name = StringArgumentType.getString(source, "name");

        if (TribesManager.isNameAvailable(name)){
            source.getSource().sendFeedback(TribeActionResult.INVALID_TRIBE.getErrorComponent(), true);
        } else {
            TribesManager.forceDeleteTribe(name);
            source.getSource().sendFeedback(new StringTextComponent("Tribe deleted: " + name), true);
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
