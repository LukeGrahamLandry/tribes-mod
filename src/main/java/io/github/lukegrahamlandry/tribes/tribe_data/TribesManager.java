package io.github.lukegrahamlandry.tribes.tribe_data;

import com.google.gson.*;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class TribesManager {
    static Map<String, Tribe> tribes = new HashMap<>();

    public static TribeErrorType createNewTribe(String name, Player player){
        if (player.getCommandSenderWorld().isClientSide()){
            TribesMain.LOGGER.error("And the lord came down from the heavens and said 'thou shall not create a tribe on the render thread'");
            return TribeErrorType.CLIENT;
        }

        if (name.length() > TribesConfig.getMaxTribeNameLength()) return TribeErrorType.LONG_NAME;  // should be caught by the create GUI
        if (playerHasTribe(player.getUUID())) return TribeErrorType.IN_TRIBE;
        if (getTribes().size() >= TribesConfig.getMaxNumberOfTribes()) return TribeErrorType.CONFIG;

        return addNewTribe(new Tribe(name, player.getUUID()));
    }

    public static TribeErrorType joinTribe(String name, Player player){
        if (playerHasTribe(player.getUUID())) return TribeErrorType.IN_TRIBE;
        if (isNameAvailable(name)) return TribeErrorType.INVALID_TRIBE;

        Tribe tribe = getTribe(name);

        if (tribe.isPrivate && !tribe.pendingInvites.contains(player.getUUID().toString())) return TribeErrorType.IS_PRIVATE;

        tribe.broadcastMessageNoCause(TribeSuccessType.SOMEONE_JOINED, player);

        return tribe.addMember(player.getUUID(), Tribe.Rank.MEMBER);
    }

    public static TribeErrorType deleteTribe(String name, UUID playerID){
        if (isNameAvailable(name)) return TribeErrorType.INVALID_TRIBE;

        if (!getTribe(name).isLeader(playerID)) return TribeErrorType.LOW_RANK;

        LandClaimHelper.forgetTribe(tribes.get(name));
        getTribe(name).broadcastMessage(TribeSuccessType.DELETE_TRIBE, playerID);
        tribes.remove(name);

        return TribeErrorType.SUCCESS;
    }

    public static void forceDeleteTribe(String name){
        if (!isNameAvailable(name)) {
            LandClaimHelper.forgetTribe(tribes.get(name));
            tribes.remove(name);
        }
    }

    static public TribeErrorType addNewTribe(Tribe newTribe){
        if (isNameAvailable(newTribe.name)){
            TribesMain.LOGGER.debug("new tribe: " + newTribe.name);
            tribes.put(newTribe.name, newTribe);
            return TribeErrorType.SUCCESS;
        } else {
            return TribeErrorType.NAME_TAKEN;
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

    public static TribeErrorType leaveTribe(Player player) {
        if (!playerHasTribe(player.getUUID())) return TribeErrorType.YOU_NOT_IN_TRIBE;
        Tribe tribe = getTribeOf(player.getUUID());
        tribe.removeMember(player.getUUID());
        return TribeErrorType.SUCCESS;
    }

    public static List<Tribe> getBans(Player playerToCheck) {
        List<Tribe> bans = new ArrayList<>();
        for (Tribe tribe : getTribes()){
            if (tribe.isBanned(playerToCheck.getUUID())){
                bans.add(tribe);
            }
        }
        return bans;
    }

    public static int getNumberOfGoodEffects(Player player){
        int tier = getTribeOf(player.getUUID()).getTribeTier();
        return TribesConfig.getTierPositiveEffects().get(tier - 1);
    }

    public static int getNumberOfBadEffects(Player player) {
        int tier = getTribeOf(player.getUUID()).getTribeTier();
        return TribesConfig.getTierNegativeEffects().get(tier - 1);
    }

    public static void renameTribe(String name, String newname) {
        if (isNameAvailable(newname) && !isNameAvailable(name)){
            tribes.put(newname, tribes.get(name));
            tribes.remove(name);
        }
    }
}
