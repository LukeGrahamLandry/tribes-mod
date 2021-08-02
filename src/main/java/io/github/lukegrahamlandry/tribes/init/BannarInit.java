package io.github.lukegrahamlandry.tribes.init;

import io.github.lukegrahamlandry.tribes.TribesMain;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.*;
import java.util.List;

public class BannarInit {
    public static Map<String, BannerPattern> patterns = new HashMap<>();

    public static void setup(){
        TribesMain.LOGGER.debug("banner init");
        List<String> texturePaths = Arrays.asList("blast", "cube", "book", "scales", "bolt", "shield", "eselia", "x", "wheat", "36b", "swords", "julius", "15", "power", "chip", "omega", "doctor", "dollar", "12", "13", "11", "ikea", "pick", "triforce", "heart", "21", "moon", "sword", "36", "37", "skribit", "cross", "tree", "efba", "24", "pendeen", "sun", "stars", "aqua", "izanagi", "faenen", "missing", "copy");
        for (String name : texturePaths){
            create(name);
        }
    }

    private static void create(String name){
        String key = TribesMain.MOD_ID + name;
        TribesMain.LOGGER.debug(key);
        BannerPattern pattern = BannerPattern.create(key.toLowerCase(), key, key, false);
        patterns.put(key, pattern);
    }

    public static BannerPattern get(String key){
        return patterns.get(TribesMain.MOD_ID + key);
    }
}
