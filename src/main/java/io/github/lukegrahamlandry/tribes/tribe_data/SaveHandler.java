package io.github.lukegrahamlandry.tribes.tribe_data;

import io.github.lukegrahamlandry.tribes.TribesMain;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.world.World;
import net.minecraft.world.storage.DimensionSavedDataManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class SaveHandler {
    public static void save(File worldDir) {
        TribesMain.LOGGER.debug("saving");
        File dataFile = new File(worldDir, "tribes.json");
        if (dataFile.getAbsolutePath().contains("DIM")) return;

        String tribesData = TribesManager.writeToString();
        try{
            // file.createNewFile();
            FileWriter writer = new FileWriter(dataFile);
            writer.write(tribesData);
            writer.close();
        } catch (IOException e){
            TribesMain.LOGGER.error("couldnt create file");
            e.printStackTrace();
        }
        TribesMain.LOGGER.debug("saved");
    }

    public static void load(File worldDir) {
        TribesMain.LOGGER.debug("loading");

        File dataFile = new File(worldDir, "tribes.json"); // .getIntegratedServer().getFile("tribes.json");

        if (!dataFile.exists()) return;
        if (dataFile.getAbsolutePath().contains("DIM")) return;
        TribesMain.LOGGER.debug(dataFile.getAbsolutePath());

        String tribesData = "";

        try {
            Scanner reader = new Scanner(dataFile);
            tribesData = reader.nextLine();
            reader.close();
        } catch (FileNotFoundException e) {
            TribesMain.LOGGER.error("couldnt read file");
            e.printStackTrace();
        }

        TribesManager.readFromString(tribesData);
        TribesMain.LOGGER.debug("loaded");
    }
}
