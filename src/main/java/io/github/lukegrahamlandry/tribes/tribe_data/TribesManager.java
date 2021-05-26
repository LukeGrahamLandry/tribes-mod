package io.github.lukegrahamlandry.tribes.tribe_data;

import com.google.gson.*;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;

import java.util.*;

public class TribesManager {
    static Map<String, Tribe> tribes = new HashMap<>();

    public static TribeActionResult createNewTribe(String name, PlayerEntity player){
        if (player.getEntityWorld().isRemote()){
            TribesMain.LOGGER.error("And the lord came down from the heavens and said 'thou shall not create a tribe on the render thread'");
            return TribeActionResult.CLIENT;
        }

        if (name.length() > TribesConfig.getMaxTribeNameLength()) return TribeActionResult.LONG_NAME;  // should be caught by the create GUI
        if (playerHasTribe(player.getUniqueID())) return TribeActionResult.IN_TRIBE;
        if (getTribes().size() >= TribesConfig.getMaxNumberOfTribes()) return TribeActionResult.CONFIG;

        return addNewTribe(new Tribe(name, player.getUniqueID()));
    }

    public static TribeActionResult joinTribe(String name, PlayerEntity player){
        if (playerHasTribe(player.getUniqueID())) return TribeActionResult.IN_TRIBE;
        if (isNameAvailable(name)) return TribeActionResult.INVALID_TRIBE;

        return getTribe(name).addMember(player.getUniqueID(), Tribe.Rank.MEMBER);
    }

    public static TribeActionResult deleteTribe(String name, UUID playerID){
        if (isNameAvailable(name)) return TribeActionResult.INVALID_TRIBE;

        if (!getTribe(name).isLeader(playerID)) return TribeActionResult.LOW_RANK;

        tribes.remove(name);

        return TribeActionResult.SUCCESS;
    }

    public static void forceDeleteTribe(String name){
        if (!isNameAvailable(name)) {
            tribes.remove(name);
        }
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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray tribeListJson = new JsonArray();
        for (Tribe tribe : getTribes()){
            tribeListJson.add(tribe.write());
        }

        return gson.toJson(tribeListJson); //tribeListJson.toString();
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
        int tier = getTribeOf(player.getUniqueID()).getTribeTier();
        return TribesConfig.getTierPositiveEffects().get(tier - 1);
    }

    public static int getNumberOfBadEffects(PlayerEntity player) {
        int tier = getTribeOf(player.getUniqueID()).getTribeTier();
        return TribesConfig.getTierNegativeEffects().get(tier - 1);
    }

    public static void renameTribe(String name, String newname) {
        if (isNameAvailable(newname) && !isNameAvailable(name)){
            tribes.put(newname, tribes.get(name));
            tribes.remove(name);
        }
    }
}
