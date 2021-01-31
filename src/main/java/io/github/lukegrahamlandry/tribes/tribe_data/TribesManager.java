package io.github.lukegrahamlandry.tribes.tribe_data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.lukegrahamlandry.tribes.TribesMain;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TribesManager {
    static List<Tribe> tribes = new ArrayList<>();

    static private List<Tribe> loadSavedTribes() {
        return new ArrayList<>();
    }

    public static TribeActionResult createNewTribe(String name, PlayerEntity player){
        if (player.getEntityWorld().isRemote()){
            TribesMain.LOGGER.error("And the lord came down from the heavens and said 'thou shall not create a tribe on the render thread'");
            return TribeActionResult.CLIENT;
        }

        if (name.length() > 24) return TribeActionResult.LONG_NAME;
        if (playerHasTribe(player.getUniqueID())) return TribeActionResult.IN_TRIBE;


        return addNewTribe(new Tribe(name, player.getUniqueID()));
    }

    static public TribeActionResult addNewTribe(Tribe newTribe){
        if (isNameAvailable(newTribe.name)){
            tribes.add(newTribe);
            return TribeActionResult.SUCCESS;
        } else {
            return TribeActionResult.NAME_TAKEN;
        }
    }

    static private boolean isNameAvailable(String name){
        boolean validName = true;
        for (Tribe testTribe : tribes){
            if (testTribe.getName().equals(name))
                validName = false;
        }

        return validName;
    }

    public static boolean playerHasTribe(UUID playerID) {
        boolean isInTribe = false;
        for (Tribe testTribe : tribes){
            TribesMain.LOGGER.debug("player: " + playerID.toString());
            TribesMain.LOGGER.debug("members: " + testTribe.getMembers());
            if (testTribe.getMembers().contains(playerID.toString()))
                isInTribe = true;
        }

        TribesMain.LOGGER.debug(isInTribe);

        return isInTribe;
    }

    public static String writeToString() {
        JsonArray tribeListJson = new JsonArray();
        for (Tribe tribe : tribes){
            tribeListJson.add(tribe.write());
        }

        return tribeListJson.toString();
    }

    public static void readFromString(String data) {
        JsonArray obj = new JsonParser().parse(data).getAsJsonArray();

        tribes = new ArrayList<>();
        for (JsonElement e : obj){
            Tribe t = Tribe.fromJson(e.toString());
            addNewTribe(t);
        }
    }
}
