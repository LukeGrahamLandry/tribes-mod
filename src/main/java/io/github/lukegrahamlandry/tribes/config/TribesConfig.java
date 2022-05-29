package io.github.lukegrahamlandry.tribes.config;

import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TribesConfig {
    //Declaration of config variables
    private static ForgeConfigSpec.IntValue numTribes;
    private static ForgeConfigSpec.BooleanValue tribeRequired;
    private static ForgeConfigSpec.ConfigValue<List<? extends Integer>> tierThresholds;
    private static ForgeConfigSpec.ConfigValue<List<? extends Integer>> tierNegEffects;
    private static ForgeConfigSpec.ConfigValue<List<? extends Integer>> tierPosEffects;

    private static ForgeConfigSpec.BooleanValue friendlyFire;

    // land claiming
    private static ForgeConfigSpec.BooleanValue requireHemiAccess;
    private static ForgeConfigSpec.IntValue tierForSelectHemi;
    private static ForgeConfigSpec.ConfigValue<List<? extends Integer>> maxChunksClaimed;
    private static ForgeConfigSpec.BooleanValue useNorthSouthHemisphereDirection;
    private static ForgeConfigSpec.IntValue halfNoMansLandWidth;
    private static ForgeConfigSpec.IntValue rankToChooseHemi;

    private static ForgeConfigSpec.ConfigValue<List<? extends Integer>> nonpvpDeathPunishTimes;
    private static ForgeConfigSpec.ConfigValue<List<? extends Integer>> pvpDeathPunishTimes;

    private static ForgeConfigSpec.IntValue daysBetweenDeityChange;
    private static ForgeConfigSpec.IntValue daysBetweenEffectsChange;
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> admins;

    private static ForgeConfigSpec.ConfigValue<List<? extends String>> ignoredEffects;
    private static ForgeConfigSpec.IntValue removeInactiveAfterDays;

    private static ForgeConfigSpec.ConfigValue<String> landOwnerDisplayPosition;

    private static ForgeConfigSpec.IntValue bannerClaimRadius;
    private static ForgeConfigSpec.BooleanValue enemyAllowsBlockInteractions;
    private static ForgeConfigSpec.BooleanValue allyAllowsBlockInteractions;
    private static ForgeConfigSpec.IntValue maxBannerClaims;

    //Initialization of the config files and their respective variables
    public static void init(ForgeConfigSpec.Builder server, ForgeConfigSpec.Builder client){
        server.push("server");
        numTribes = server
                .comment("Maximum Number of Tribes: ")
                .defineInRange("numberOfTribes", 10, 1, 999);
        tribeRequired = server
                .comment("Tribe Required: ")
                .define("tribesRequired", true);
        tierThresholds = server
                .comment("I:Tier Thresholds: ")
                .defineList("tier_thresholds", Arrays.asList(4,12,30,100),i -> (int)i>=0);
        tierNegEffects = server
                .comment("I:Number of negative effects by tribe tier: ")
                .defineList("tier_negative_effects", Arrays.asList(2,2,1,1,0),i -> (int)i>=0);
        tierPosEffects = server
                .comment("I:Number of positive effects by tribe tier: ")
                .defineList("tier_positive_effects", Arrays.asList(1,2,2,3,3),i -> (int)i>=0);
        friendlyFire = server
                .comment("Whether players should be able to harm other members of their tribe: ")
                .define("friendlyFire", false);
        tierForSelectHemi = server
                .comment("Minimum tribe tier to access a hemisphere: ")
                .defineInRange("tierForSelectHemi", 2, 0, 10);
        requireHemiAccess = server
                .comment("Whether player's tribe must select a hemisphere to access it: ")
                .define("requireHemiAccess", true);
        maxChunksClaimed = server
                .comment("I:Maximum number of chunks able to be claimed at each tribe rank: ")
                .defineList("max_claimed_chunks", Arrays.asList(1,4,10,20,30),i -> (int)i>=0);
        useNorthSouthHemisphereDirection = server
                .comment("true for north/south or false for east/west: ")
                .define("useNorthSouthHemisphereDirection", true);
        halfNoMansLandWidth = server
                .comment("The distance from zero to the edge of a hemisphere, half the width of no mans land : ")
                .defineInRange("halfNoMansLandWidth", 500, 0, Integer.MAX_VALUE);
        nonpvpDeathPunishTimes = server
                .comment("I:How long your chunk claims will be disabled by how many times people have died (out of PVP) in the interval: ")
                .defineList("nonpvpDeathPunishTimes", Arrays.asList(10, 60, 360),i -> (int)i>=0);
        pvpDeathPunishTimes = server
                .comment("I:How long your chunk claims will be disabled by how many times people have died (by PVP) in the interval: ")
                .defineList("pvpDeathPunishTimes", Arrays.asList(30, 120, 1440),i -> (int)i>=0);
        rankToChooseHemi = server
                .comment("A member must have equal or greater than this rank to select a hemisphere for thier tribe [member, officer, vice leader, leader]: ")
                .defineInRange("rankToChooseHemi", 2, 0, 3);
        admins = server
                .comment("S: UUIDs of server admins: ")
                .defineList("admins", Arrays.asList("380df991-f603-344c-a090-369bad2a924a", "bcb2252d-70de-4abc-9932-bc46bd5dc62f"),i ->((String) i).split("-").length == 5);
        daysBetweenDeityChange = server
                .comment("The number of days you must wait between changing your tribe's deity : ")
                .defineInRange("daysBetweenDeityChange", 30, 0, Integer.MAX_VALUE);
        daysBetweenEffectsChange = server
                .comment("The number of days you must wait between changing your tribe's effects : ")
                .defineInRange("daysBetweenEffectsChange", 10, 0, Integer.MAX_VALUE);
        ignoredEffects = server
                .comment("S: effects that cannot be chosen as a persistent tribe effect : ")
                .defineList("ignoredEffects", Arrays.asList("minecraft:bad_omen", "minecraft:conduit_power", "minecraft:health_boost", "minecraft:luck", "minecraft:unluck", "minecraft:hero_of_the_village", "minecraft:absorption"), i -> ((String) i).contains(":"));
        removeInactiveAfterDays = server
                .comment("Players who haven't logged on in this many days will automatically be removed from the tribe they're in. Setting this value to 0 will disable this feature: ")
                .defineInRange("removeInactiveAfterDays", 10, 0, Integer.MAX_VALUE);

        bannerSystemAddonConfigs(server);

        server.pop();

        client.push("client");

        landOwnerDisplayPosition = client
                .comment("position of the land owner ui. options: top_left, top_right, top_middle, bottom_left, bottom_right, bottom_middle, none ")
                .define("landOwnerDisplayPosition", "top_left");

        client.pop();
    }

    private static void bannerSystemAddonConfigs(ForgeConfigSpec.Builder server){
        bannerClaimRadius = server
                .comment("How many chunks will be claimed around a placed banner. set to 0 to disable")
                .defineInRange("bannerClaimRadius", 0, 0, Integer.MAX_VALUE);
        enemyAllowsBlockInteractions = server
                .comment("Whether tribes that have declared you as an enemy should be able to interact with your claimed blocks (ie doors, chests, use flint and steel etc) NOT including spawn points. Even when true, cannot place or break blocks")
                .define("enemyAllowsBlockInteractions", false);
        allyAllowsBlockInteractions = server
                .comment("Whether tribes that have you have declared as an ally should be able to interact with your claimed blocks (ie doors, beds) NOT including containers like chests. Even when true, cannot place or break blocks")
                .define("allyAllowsBlockInteractions", false);

        maxBannerClaims = server
                .comment("How many times you may use tribe talisman on a banner to claim a radius around it. set to 0 to disable")
                .defineInRange("maxBannerClaims", 0, 0, Integer.MAX_VALUE);
    }

    public static boolean bannerClaimsEnabled(){
        return maxBannerClaims.get() > 0 && bannerClaimRadius.get() > 0;
    }

    public static boolean commandClaimsEnabled(){
        return getMaxChunksClaimed().stream().reduce(0, Integer::sum) != 0;
    }

    public static boolean canEnemiesInteract(){
        return enemyAllowsBlockInteractions.get();
    }

    public static boolean canAlliesInteract(){
        return allyAllowsBlockInteractions.get();
    }

    public static int getMaxNumberOfTribes(){
        return numTribes.get();
    }

    public static boolean isTribeRequired(){
        return tribeRequired.get();
    }

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

    public static boolean getRequireHemiAccess(){
        return requireHemiAccess.get();
    }

    public static int getMinTierToSelectHemi(){
        return tierForSelectHemi.get();
    }

    public static List<Integer> getMaxChunksClaimed() {
        return (List<Integer>) maxChunksClaimed.get();
    }

    public static boolean getUseNorthSouthHemisphereDirection(){
        return useNorthSouthHemisphereDirection.get();
    }

    public static int getHalfNoMansLandWidth(){
        return halfNoMansLandWidth.get();
    }

    public static int rankToChooseHemi(){
        return rankToChooseHemi.get();
    }

    public static int getBannerClaimRadius(){
        return bannerClaimRadius.get();
    }

    public static int getDeathClaimDisableTime(int index, boolean deathWasPVP) {
        List<Integer> punishments = (List<Integer>) (deathWasPVP ? pvpDeathPunishTimes.get() : nonpvpDeathPunishTimes.get());
        index = Math.min(index, punishments.size() - 1);
        return punishments.get(index);
    }

    public static List<Effect> getGoodEffects(){
        ArrayList<Effect> disabledEffects = new ArrayList<>();
        for (String key : ignoredEffects.get()){
            disabledEffects.add(ForgeRegistries.POTIONS.getValue(new ResourceLocation(key)));
        }

        ArrayList<Effect> theEffects = new ArrayList<>();

        for (Field field : Effects.class.getFields()){
            try {
                if (field.get(null) instanceof Effect){
                    Effect toCheck = (Effect) field.get(null);


                    if (toCheck.isBeneficial() && !toCheck.isInstantenous() && !disabledEffects.contains(toCheck)){
                        theEffects.add(toCheck);
                    }
                }
            } catch (IllegalAccessException ignored) {}
        }

        return theEffects;
    }

    public static List<Effect> getBadEffects(){
        ArrayList<Effect> disabledEffects = new ArrayList<>();
        for (String key : ignoredEffects.get()){
            disabledEffects.add(ForgeRegistries.POTIONS.getValue(new ResourceLocation(key)));
        }

        ArrayList<Effect> theEffects = new ArrayList<>();

        for (Field field : Effects.class.getFields()){
            try {
                if (field.get(null) instanceof Effect){
                    Effect toCheck = (Effect) field.get(null);
                    if (!toCheck.isBeneficial() && !toCheck.isInstantenous() && !disabledEffects.contains(toCheck)){
                        theEffects.add(toCheck);
                    }
                }
            } catch (IllegalAccessException ignored) {}
        }

        return theEffects;
    }

    public static boolean areEffectsEnabled(){
        // if all tiers allow you to choose no effects, then they're not enabled
        return getTierNegativeEffects().stream().reduce(0, Integer::sum) != 0 || getTierPositiveEffects().stream().reduce(0, Integer::sum) != 0;

    }

    public static boolean isAdmin(String id) {
        List<String> adminIDs = (List<String>) admins.get();
        return adminIDs.contains(id);
    }

    public static long betweenDeityChangeMillis(){
        return daysBetweenDeityChange.get() * 24 * 60 * 60 * 1000;
    }

    public static long betweenEffectsChangeMillis(){
        return daysBetweenEffectsChange.get() * 24 * 60 * 60 * 1000;
    }

    public static long kickInactiveAfterMillis(){
        return removeInactiveAfterDays.get() * 24 * 60 * 60 * 1000;
    }


    // CLIENT

    public static String getLandOwnerDisplayPosition(){
        return landOwnerDisplayPosition.get();
    }

}
