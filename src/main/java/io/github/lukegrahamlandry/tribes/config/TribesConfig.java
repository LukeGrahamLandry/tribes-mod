package io.github.lukegrahamlandry.tribes.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class TribesConfig {
    //Declaration of config variables
    private static ForgeConfigSpec.IntValue numTribes;
    private static ForgeConfigSpec.BooleanValue tribeRequired;
    private static ForgeConfigSpec.IntValue effectChangeDays;
    private static ForgeConfigSpec.ConfigValue<List<? extends Integer>> tierThresholds;
    private static ForgeConfigSpec.ConfigValue<List<? extends Integer>> tierNegEffects;
    private static ForgeConfigSpec.ConfigValue<List<? extends Integer>> tierPosEffects;

    private static ForgeConfigSpec.BooleanValue friendlyFire;
    private static ForgeConfigSpec.IntValue tierForClaiming;
    private static ForgeConfigSpec.ConfigValue<List<? extends Integer>> maxChunksClaimed;

    //Initialization of the config files and their respective variables
    public static void init(ForgeConfigSpec.Builder server, ForgeConfigSpec.Builder client){
        server.comment("Server configuration settings")
                .push("server");
        numTribes = server
                .comment("Maximum Number of Tribes: ")
                .defineInRange("numberOfTribes", 10, 1, 999);
        effectChangeDays = server
                .comment("Days between Effect Change: ")
                .defineInRange("daysBetweenEffectChange", 10, 0, 999);
        tribeRequired = server
                .comment("Tribe Required: ")
                .define("tribesRequired", true);
        tierThresholds = server
                .comment("I:Tier Thresholds: ")
                .defineList("tier_thresholds", Arrays.asList(4,12),i -> (int)i>=0);
        tierNegEffects = server
                .comment("I:Tier Negative Effects: ")
                .defineList("tier_negative_effects", Arrays.asList(1,1,0),i -> (int)i>=0);
        tierPosEffects = server
                .comment("I:Tier Positive Effects: ")
                .defineList("tier_positive_effects", Arrays.asList(1,2,3),i -> (int)i>=0);
        friendlyFire = server
                .comment("Whether players should be able to harm other members of their tribe: ")
                .define("tribesRequired", false);
        tierForClaiming = server
                .comment("Minimum tribe tier to claim land: ")
                .defineInRange("numberOfTribes", 2, 0, 2);
        maxChunksClaimed = server
                .comment("I:Maximum number of chunks able to be claimed at each tribe rank: ")
                .defineList("tier_positive_effects", Arrays.asList(1,4,10,20,30),i -> (int)i>=0);
        server.pop();
    }

    public static int getMaxNumberOfTribes(){
        return numTribes.get();
    }

    public static boolean getTribesRequired(){
        return tribeRequired.get();
    }

    //Getter Method for the number of days between effects change
    public static int getDaysBetweenEffects(){ return effectChangeDays.get(); }

    public static List<Integer> getTierThresholds(){
        return (List<Integer>) tierThresholds.get();
    }

    public static List<Integer> getTierNegativeEffects(){
        return (List<Integer>) tierNegEffects.get();
    }

    public static List<Integer> getTierPositiveEffects(){
        return (List<Integer>) tierPosEffects.get();
    }

    public static boolean getFriendlyFireEnabled(){
        return friendlyFire.get();
    }

    public static int getMaxTribeNameLength(){
        return 24;
    }

    public static int getMinTierToClaimLand(){
        return tierForClaiming.get();
    }

    public static List<Integer> getMaxChunksClaimed() {
        return (List<Integer>) maxChunksClaimed.get();
    }
}
