package io.github.lukegrahamlandry.tribes.tribe_data;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.events.RemoveInactives;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = TribesMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SaveHandler {
    public static File tribeDataLocation;  // folder that the tribe data was most recently loaded from

    // note: fires 3 times, /data /DIM1/data and /DIM-1/data
    @SubscribeEvent
    public static void doLoad(WorldEvent.Load event){
        if (event.getWorld().isClientSide()) return;

        File dataFile = ((ServerChunkCache)event.getWorld().getChunkSource()).getDataStorage().dataFolder;
        load(dataFile);
    }

    // note: fires 3 times, /data /DIM1/data and /DIM-1/data
    @SubscribeEvent
    public static void doSave(WorldEvent.Unload event){
        if (event.getWorld().isClientSide()) return;
        File dataFile = ((ServerChunkCache)event.getWorld().getChunkSource()).getDataStorage().dataFolder;
        save(dataFile);
    }

    public static void save(File worldDir) {
        TribesMain.LOGGER.debug("saving");
        File dataFile = new File(worldDir, "tribes.json");
        if (dataFile.getAbsolutePath().contains("DIM")){
            TribesMain.LOGGER.debug("skip save to " + worldDir.getAbsolutePath());
            return;
        }

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
        } else {
            TribesManager.readFromString("[]");
        }
        LandClaimHelper.setup();

        // read deities
        File deitiesBooksLocation = new File(worldDir, "deities");
        File deityDataFile = new File(deitiesBooksLocation, "deities.json");
        if (!deityDataFile.exists()) {
            createDefaultDeityFiles(deitiesBooksLocation);
        }
        DeitiesManager.readFromString(readMultiline(deityDataFile));

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

    private static void createDefaultDeityFiles(File deityLocation) {
        if (!deityLocation.exists()) deityLocation.mkdirs();

        try {
            URI uri = SaveHandler.class.getResource("/deities").toURI();
            AtomicReference<Path> myPath = new AtomicReference<>();
            TribesMain.LOGGER.error("scheme " + uri.getScheme());
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                myPath.set(fileSystem.getPath("/deities"));
            } else if (uri.getScheme().equals("modjar")){
                // fixes java.nio.file.FileSystemNotFoundException: Provider modjar not installed
                FMLLoader.getLoadingModList().getModFiles().forEach((modFile) -> {
                    modFile.getMods().forEach((modInfo) -> {
                        if (modInfo.getModId().equals(TribesMain.MOD_ID)){
                            myPath.set(modFile.getFile().findResource("deities"));
                        }
                    });
                });
            }
            else {
                myPath.set(Paths.get(uri));
            }
            Stream<Path> walk = Files.walk(myPath.get(), 1);
            for (Iterator<Path> it = walk.iterator(); it.hasNext();){
                String filename = it.next().getFileName().toString();

                if (!filename.contains(".")) continue;
                TribesMain.LOGGER.debug("load default: /deities/" + filename);

                InputStream in = SaveHandler.class.getClassLoader().getResourceAsStream("/deities/" + filename);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                File newFile = new File(deityLocation, filename);
                if (!newFile.exists()) newFile.createNewFile();
                FileWriter writer = new FileWriter(newFile);

                reader.lines().forEach(str -> {
                    try {
                        writer.write(str);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                writer.close();


            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }



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
