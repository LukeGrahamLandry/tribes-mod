package io.github.lukegrahamlandry.tribes.tribe_data;

import io.github.lukegrahamlandry.tribes.TribesMain;
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
        TribesMain.LOGGER.debug("saved");
    }

    public static void load(File worldDir) {
        TribesMain.LOGGER.debug("loading");

        File dataFile = new File(worldDir, "tribes.json"); // .getIntegratedServer().getFile("tribes.json");

        if (!dataFile.exists()) return;
        if (dataFile.getAbsolutePath().contains("DIM")) return;
        TribesMain.LOGGER.debug(dataFile.getAbsolutePath());

        tribeDataLocation = worldDir;

        StringBuilder tribesData = new StringBuilder();

        try {
            Scanner reader = new Scanner(dataFile);
            while (reader.hasNext()){
                tribesData.append(reader.nextLine());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            TribesMain.LOGGER.error("couldn't read file");
            e.printStackTrace();
        }

        TribesManager.readFromString(tribesData.toString());
        LandClaimHelper.setup();
        TribesMain.LOGGER.debug("loaded");
    }
}
