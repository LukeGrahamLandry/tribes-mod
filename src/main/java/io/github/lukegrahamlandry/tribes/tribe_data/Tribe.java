package io.github.lukegrahamlandry.tribes.tribe_data;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tribe {
    String name;
    UUID owner;
    List<String> members;
    List<String> bans;
    public Tribe(String tribeName, UUID creater){
        this.name = tribeName;
        this.owner = creater;
        this.bans = new ArrayList<>();
        this.members = new ArrayList<>();
        this.addMember(this.owner);
    }

    public TribeActionResult addMember(UUID playerID) {
        if (isBanned(playerID)) return TribeActionResult.BANNED;
        if (TribesManager.playerHasTribe(playerID) || this.members.contains(playerID.toString())) return TribeActionResult.IN_TRIBE;

        this.members.add(playerID.toString());
        return TribeActionResult.SUCCESS;
    }

    public TribeActionResult banPlayer(UUID playerRunningCommand, UUID playerToBan) {
        if (!this.isLeader(playerRunningCommand)) return TribeActionResult.LOW_RANK;

        if (this.members.contains(playerToBan.toString())){
           this.removeMember(playerToBan);
        }

        this.bans.add(playerToBan.toString());

        return TribeActionResult.SUCCESS;
    }

    public TribeActionResult unbanPlayer(UUID playerRunningCommand, UUID playerToUnban) {
        if (!this.isLeader(playerRunningCommand)) return TribeActionResult.LOW_RANK;

        this.bans.remove(playerToUnban.toString());

        return TribeActionResult.SUCCESS;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getMembers(){
        return this.members;
    }

    public int getCount() {
        return getMembers().size();
    }

    public JsonObject write(){
        JsonObject obj = new JsonObject();

        obj.addProperty("name", this.getName());
        obj.addProperty("owner", this.owner.toString());

        JsonArray memberList = new JsonArray();
        this.getMembers().forEach(memberList::add);
        obj.add("members", memberList);

        JsonArray banList = new JsonArray();
        this.bans.forEach(banList::add);
        obj.add("bans", banList);

        return obj;
    }

    static Tribe fromJson(String str){
        JsonObject obj = new JsonParser().parse(str).getAsJsonObject();

        String name = obj.get("name").getAsString();
        String owner = obj.get("owner").getAsString();
        Tribe t = new Tribe(name, UUID.fromString(owner));

        JsonArray members = obj.get("members").getAsJsonArray();
        for (JsonElement e : members){
            t.addMember(UUID.fromString(e.getAsString()));
        }

        JsonArray bans = obj.get("bans").getAsJsonArray();
        for (JsonElement e : bans){
            t.banPlayer(UUID.fromString(owner), UUID.fromString(e.getAsString()));
        }

        return t;
    }

    @Override
    public String toString() {
        return write().toString();
    }

    public boolean isLeader(UUID uniqueID) {
        return uniqueID.equals(owner);
    }

    public void removeMember(UUID playerID) {
        this.members.remove(playerID.toString());
        // TODO: logic for passing on leadership or deleting tribe
    }

    public boolean isBanned(UUID uniqueID) {
        return this.bans.contains(uniqueID.toString());
    }
}
