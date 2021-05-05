package io.github.lukegrahamlandry.tribes.tribe_data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;

import java.util.*;

public class TribesManager {
    static Map<String, Tribe> tribes = new HashMap<>();

    static private List<Tribe> loadSavedTribes() {
        return new ArrayList<>();
    }

    public static TribeActionResult createNewTribe(String name, PlayerEntity player){
        if (player.getEntityWorld().isRemote()){
            TribesMain.LOGGER.error("And the lord came down from the heavens and said 'thou shall not create a tribe on the render thread'");
            return TribeActionResult.CLIENT;
        }

        if (name.length() > TribesConfig.getMaxTribeNameLength()) return TribeActionResult.LONG_NAME;  // should be caught by the create GUI
        if (playerHasTribe(player.getUniqueID())) return TribeActionResult.IN_TRIBE;


        return addNewTribe(new Tribe(name, player.getUniqueID()));
    }

    public static TribeActionResult joinTribe(String name, PlayerEntity player){
        if (playerHasTribe(player.getUniqueID())) return TribeActionResult.IN_TRIBE;
        if (isNameAvailable(name)) return TribeActionResult.INVALID_TRIBE;

        return getTribe(name).addMember(player.getUniqueID(), Tribe.Rank.MEMBER);
    }

    public static TribeActionResult deleteTribe(String name, PlayerEntity player){
        if (isNameAvailable(name)) return TribeActionResult.INVALID_TRIBE;

        if (!getTribe(name).isLeader(player.getUniqueID())) return TribeActionResult.LOW_RANK;

        tribes.remove(name);

        return TribeActionResult.SUCCESS;
    }

    static public TribeActionResult addNewTribe(Tribe newTribe){
        if (isNameAvailable(newTribe.name)){
            tribes.put(newTribe.name, newTribe);
            return TribeActionResult.SUCCESS;
        } else {
            return TribeActionResult.NAME_TAKEN;
        }
    }

    static public boolean isNameAvailable(String name){
        return !tribes.containsKey(name);
    }

    static public List<Tribe> getTribes(){
        TribesMain.LOGGER.debug(tribes);
        if (tribes.isEmpty()){
            return new ArrayList<>();
        }
        return new ArrayList<>(tribes.values());
    }

    public static Tribe getTribe(String name){
        return tribes.get(name);
    }

    public static boolean playerHasTribe(UUID playerID){
        return getTribeOf(playerID) != null;
    }

    public static Tribe getTribeOf(UUID playerID) {
        if (getTribes().size() == 0) {
            return null;
        }

        for (Tribe testTribe : getTribes()){
            if (testTribe.getMembers().contains(playerID.toString()))
               return testTribe;
        }

        return null;
    }

    public static String writeToString() {
        JsonArray tribeListJson = new JsonArray();
        for (Tribe tribe :  getTribes()){
            tribeListJson.add(tribe.write());
        }

        return tribeListJson.toString();
    }

    public static void readFromString(String data) {
        JsonArray obj = new JsonParser().parse(data).getAsJsonArray();

        tribes.clear();
        for (JsonElement e : obj){
            Tribe t = Tribe.fromJson(e.toString());
            addNewTribe(t);
        }
    }

    public static TribeActionResult leaveTribe(PlayerEntity player) {
        if (!playerHasTribe(player.getUniqueID())) return TribeActionResult.YOU_NOT_IN_TRIBE;
        Tribe tribe = getTribeOf(player.getUniqueID());
        tribe.removeMember(player.getUniqueID());
        return TribeActionResult.SUCCESS;
    }

    public static List<Tribe> getBans(PlayerEntity playerToCheck) {
        List<Tribe> bans = new ArrayList<>();
        for (Tribe tribe : getTribes()){
            if (tribe.isBanned(playerToCheck.getUniqueID())){
                bans.add(tribe);
            }
        }
        return bans;
    }

    public static int getNumberOfGoodEffects(PlayerEntity player){
        if (player.getEntityWorld().isRemote()) {
            TribesMain.LOGGER.error("And the lord came down from the heavens and said 'thou shall not use tribe commands on the render thread' Pls use a packet or something");
        }

        // later i'll do logic for doing it based on config / tribe level but for now its hard-coded
        return 2;
    }

    public static int getNumberOfBadEffects(PlayerEntity player){
        if (player.getEntityWorld().isRemote()) {
            TribesMain.LOGGER.error("And the lord came down from the heavens and said 'thou shall not use tribe commands on the render thread' Pls use a packet or something");
            //int a = 0/0; // BREAK :)
        }

        // later i'll do logic for doing it based on config / tribe level but for now its hard-coded
        return 1;
    }

    public static TribeActionResult setTribeEffects(PlayerEntity player, ArrayList<Effect> good, ArrayList<Effect> bad){
        if (player.getEntityWorld().isRemote()) {
            TribesMain.LOGGER.error("And the lord came down from the heavens and said 'thou shall not create a tribe on the render thread' Pls use a packet or something");
            return TribeActionResult.CLIENT;
        }

        if (bad.size() != getNumberOfBadEffects(player) || bad.size() != getNumberOfBadEffects(player)){
            return TribeActionResult.INVALID_INT;
        }

        return TribeActionResult.SUCCESS;
    }
}
