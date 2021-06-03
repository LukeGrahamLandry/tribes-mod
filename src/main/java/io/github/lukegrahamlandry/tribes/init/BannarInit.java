package io.github.lukegrahamlandry.tribes.init;

import net.minecraft.tileentity.BannerPattern;

import java.util.ArrayList;
import java.util.List;

public class BannarInit {
    public static List<BannerPattern> patterns = new ArrayList<>();

    static {
        List<String> texturePaths = new ArrayList<>();
        texturePaths.add("test");

        for (String name : texturePaths) {
            BannerPattern pattern = BannerPattern.create(name.toLowerCase(), name, name, true);
            patterns.add(pattern);
        }
    }
}
