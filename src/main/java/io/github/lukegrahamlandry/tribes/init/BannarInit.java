package io.github.lukegrahamlandry.tribes.init;

import io.github.lukegrahamlandry.tribes.TribesMain;
import net.minecraft.tileentity.BannerPattern;

import java.awt.*;
import java.util.*;
import java.util.List;

public class BannarInit {
    public static Map<String, BannerPattern> patterns = new HashMap<>();

    // if i take out the static pattern item registering i have to poke this in some other way so the class gets loaded early

    // doing this too late (like syncing when you login) makes them not get atlas stiched so missing texture
    // doing it just on server makes error running /tribe deity banner
    static {
        // something in long list breaks it. numbers or capitals?
        // List<String> texturePaths = Arrays.asList("blast", "cube", "book", "scales", "bolt", "shield", "Eselia", "x", "wheat", "36b", "swords", "Julius", "15", "power", "chip", "omega", "doctor", "dollar", "12", "13", "11", "Ikea", "pick", "triforce", "heart", "21", "moon", "sword", "36", "37", "Skribit", "cross", "tree", "Efba", "24", "Pendeen", "sun", "stars", "Aqua", "Izanagi", "Faenen", "missing", "copy");
        List<String> texturePaths = new ArrayList<>();
        texturePaths.add("pick");

        for (String name : texturePaths) {
            String key = TribesMain.MOD_ID + name;
            TribesMain.LOGGER.debug(key);
            BannerPattern pattern = BannerPattern.create(key.toLowerCase(), key, key, false);
            patterns.put(key, pattern);
        }
    }

    public static BannerPattern get(String key){
        return patterns.get(TribesMain.MOD_ID + key);
    }
}
