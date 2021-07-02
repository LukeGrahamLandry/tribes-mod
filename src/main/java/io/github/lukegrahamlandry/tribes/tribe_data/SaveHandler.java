package io.github.lukegrahamlandry.tribes.tribe_data;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.events.RemoveInactives;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

@Mod.EventBusSubscriber(modid = TribesMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SaveHandler {
    public static File tribeDataLocation;  // folder that the tribe data was most recently loaded from

    // note: fires 3 times, /data /DIM1/data and /DIM-1/data
    @SubscribeEvent
    public static void doLoad(WorldEvent.Load event){
        if (event.getWorld().isRemote()) return;
        File dataFile = ((ServerChunkProvider)event.getWorld().getChunkProvider()).getSavedData().folder;
        load(dataFile);
    }

    // note: fires 3 times, /data /DIM1/data and /DIM-1/data
    @SubscribeEvent
    public static void doSave(WorldEvent.Unload event){
        if (event.getWorld().isRemote()) return;
        File dataFile = ((ServerChunkProvider)event.getWorld().getChunkProvider()).getSavedData().folder;
        save(dataFile);
    }

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
            TribesMain.LOGGER.error("couldn't create file");
            e.printStackTrace();
        }

        File deitiesBooksLocation = new File(worldDir, "deities");
        if (!deitiesBooksLocation.exists()){
            deitiesBooksLocation.mkdir();

            File bookLocation = new File(deitiesBooksLocation, DeitiesManager.EXAMPLE_DEITY.key + ".txt");
            try{
                FileWriter writer = new FileWriter(bookLocation);
                writer.write("this is where you would put your holy text :) ");
                writer.write("it can be long and will be automatically broken into pages");
                writer.write("it will be given to players when they use /tribe deity book");
                writer.close();
            } catch (IOException e){
                TribesMain.LOGGER.error("couldn't create file");
                e.printStackTrace();
            }
        }

        File deityDataFile = new File(deitiesBooksLocation, "deities.json");
        if (!deityDataFile.exists()){
            try{
                FileWriter writer = new FileWriter(deityDataFile);
                writer.write(DeitiesManager.generateExampleJson());
                writer.close();
            } catch (IOException e){
                TribesMain.LOGGER.error("couldn't create file");
                e.printStackTrace();
            }
        }


        File loginTimesFile = new File(worldDir, "tribes-player-activity.json");
        try {
            FileWriter writer = new FileWriter(loginTimesFile);
            writer.write(RemoveInactives.save());
            writer.close();
        } catch (IOException e){
            TribesMain.LOGGER.error("couldn't create file");
            e.printStackTrace();
        }

        TribesMain.LOGGER.debug("saved");
    }

    public static void load(File worldDir) {
        if (worldDir.getAbsolutePath().contains("DIM")) {
            TribesMain.LOGGER.debug("skip load from " + worldDir.getAbsolutePath());
            return;
        }
        TribesMain.LOGGER.debug("loading from " + worldDir.getAbsolutePath());
        tribeDataLocation = worldDir;


        // read tribes
        File tribeDataFile = new File(worldDir, "tribes.json");
        if (tribeDataFile.exists()) {
            TribesManager.readFromString(readMultiline(tribeDataFile));
            LandClaimHelper.setup();
        }

        // read deities
        File deitiesBooksLocation = new File(worldDir, "deities");
        File deityDataFile = new File(deitiesBooksLocation, "deities.json");
        if (deityDataFile.exists()) {
            DeitiesManager.readFromString(readMultiline(deityDataFile));
        }

        // read deity books
        DeitiesManager.deities.forEach((key, deityData) -> {
            File bookLocation = new File(deitiesBooksLocation, key + ".txt");
            if (bookLocation.exists()) {
                String rawBookContent = readMultiline(bookLocation);
                deityData.generateBook(rawBookContent);
            }
        });


        File loginTimesFile = new File(worldDir, "tribes-player-activity.json");
        if (loginTimesFile.exists()) {
            RemoveInactives.load(readMultiline(loginTimesFile));
        }

        TribesMain.LOGGER.debug("loaded");
    }

    private static String readMultiline(File dataLocation){
        StringBuilder data = new StringBuilder();

        try {
            Scanner reader = new Scanner(dataLocation);
            while (reader.hasNext()){
                data.append(reader.nextLine());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            TribesMain.LOGGER.error("couldn't read file");
            e.printStackTrace();
        }

        return data.toString();
    }
}
