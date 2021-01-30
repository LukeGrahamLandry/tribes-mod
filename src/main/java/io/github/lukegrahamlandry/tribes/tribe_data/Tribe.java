package io.github.lukegrahamlandry.tribes.tribe_data;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tribe {
    String name;
    UUID owner;
    List<String> members;
    public Tribe(String tribeName, UUID creater){
        this.name = tribeName;
        this.owner = creater;
        this.members = new ArrayList<>();
        this.addMember(this.owner);
    }

    public void addMember(UUID playerID) {
        if (!TribesManager.playerHasTribe(playerID) && !this.members.contains(playerID.toString())){
            this.members.add(playerID.toString());
        }
    }

    public String getName() {
        return this.name;
    }

    public List<String> getMembers(){
        return this.members;
    }

    public JsonObject write(){
        JsonObject obj = new JsonObject();
        obj.addProperty("name", this.getName());
        obj.addProperty("owner", this.owner.toString());
        JsonArray memberList = new JsonArray();
        this.getMembers().forEach(memberList::add);
        obj.add("members", memberList);

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

        return t;
    }

    @Override
    public String toString() {
        return write().toString();
    }
}
