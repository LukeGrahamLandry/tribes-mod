package io.github.lukegrahamlandry.tribes.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import io.github.lukegrahamlandry.tribes.TribesMain;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.io.File;

@Mod.EventBusSubscriber
public class Config {
    //Decleration of the server config and it's builder
    private static final ForgeConfigSpec.Builder server_builder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec server_config;

    //Decleration of the client config and it's builder
    private static final ForgeConfigSpec.Builder client_builder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec client_config;

    static {
        //Initiliazation of the main configs
        TribesConfig.init(server_builder, client_builder);

        server_config = server_builder.build();
        client_config = client_builder.build();
    }

    //Loading function for config files
    public static void loadConfig(ForgeConfigSpec config, String path){
        TribesMain.LOGGER.info("Loading config: " + path);
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        TribesMain.LOGGER.info("Built config: " + path);
        file.load();
        TribesMain.LOGGER.info("Loaded config: " + path);
        config.setConfig(file);
    }
}
