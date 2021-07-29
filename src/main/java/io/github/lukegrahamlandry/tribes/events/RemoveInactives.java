package io.github.lukegrahamlandry.tribes.events;

import com.google.gson.*;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RemoveInactives {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<UUID, Long> lastPlayTimes = new HashMap<>();

    public static String save(){
        JsonObject players = new JsonObject();
        lastPlayTimes.forEach((uuid, time) -> {
            players.addProperty(uuid.toString(), time);
        });

        return gson.toJson(players);
    }

    public static void load(String data){
        JsonObject players = new JsonParser().parse(data).getAsJsonObject();
        for (Map.Entry<String, JsonElement> e : players.entrySet()){
            UUID player = UUID.fromString(e.getKey());
            Long time = e.getValue().getAsLong();
            lastPlayTimes.put(player, time);
        }
    }

    public static void recordActive(UUID player){
        lastPlayTimes.put(player, System.currentTimeMillis());
    }

    public static void check(){
        if (TribesConfig.kickInactiveAfterMillis() <= 0) return;

        lastPlayTimes.forEach((uuid, loginTime) -> {
            Tribe tribe = TribesManager.getTribeOf(uuid);
            if (tribe != null){
                long timePassed = System.currentTimeMillis() - loginTime;
                boolean shouldKick = timePassed > TribesConfig.kickInactiveAfterMillis();
                if (shouldKick){
                    TribesMain.LOGGER.debug("kick inavtive: " + uuid + "| " + timePassed + " " + loginTime + " " + TribesConfig.kickInactiveAfterMillis());
                    // todo: message to everyone that the player has been kicked
                    // todo: message to the player when they join again
                    tribe.removeMember(uuid);
                }
            }
        });
    }
}
