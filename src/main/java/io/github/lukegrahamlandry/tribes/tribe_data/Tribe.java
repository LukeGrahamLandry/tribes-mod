package io.github.lukegrahamlandry.tribes.tribe_data;

import com.google.gson.*;

import java.util.*;

public class Tribe {
    String name;
    HashMap<String, Rank> members;
    List<String> bans;
    public Tribe(String tribeName, UUID creater){
        this.name = tribeName;
        this.bans = new ArrayList<>();
        this.members = new HashMap<>();
        this.addMember(creater, Rank.LEADER);
    }

    public TribeActionResult addMember(UUID playerID, Rank rank) {
        if (isBanned(playerID)) return TribeActionResult.BANNED;
        if (TribesManager.playerHasTribe(playerID) || this.getMembers().contains(playerID.toString())) return TribeActionResult.IN_TRIBE;

        this.members.put(playerID.toString(), rank);
        return TribeActionResult.SUCCESS;
    }

    public TribeActionResult banPlayer(UUID playerRunningCommand, UUID playerToBan) {
        if (!this.isOfficer(playerRunningCommand)) return TribeActionResult.LOW_RANK;

        if (this.getMembers().contains(playerToBan.toString())){
           this.removeMember(playerToBan);
        }

        this.bans.add(playerToBan.toString());

        return TribeActionResult.SUCCESS;
    }

    public TribeActionResult unbanPlayer(UUID playerRunningCommand, UUID playerToUnban) {
        if (!this.isOfficer(playerRunningCommand)) return TribeActionResult.LOW_RANK;

        this.bans.remove(playerToUnban.toString());

        return TribeActionResult.SUCCESS;
    }

    public TribeActionResult promotePlayer(UUID playerRunningCommand, UUID playerToPromote) {
        if (!this.getMembers().contains(playerToPromote.toString())) return TribeActionResult.THEY_NOT_IN_TRIBE;

        int runRank = this.getRankOf(playerRunningCommand.toString()).asInt();
        int targetRank = this.getRankOf(playerToPromote.toString()).asInt();

        if ((runRank - 2) < targetRank) return TribeActionResult.LOW_RANK;

        int newRank = targetRank + 1;
        if (newRank > Rank.LEADER.asInt()) return TribeActionResult.RANK_DOESNT_EXIST;
        if (newRank == Rank.LEADER.asInt()) {
            this.setRank(playerRunningCommand, Rank.VICE_LEADER);
        }

        this.setRank(playerToPromote, Rank.fromInt(newRank));

        return TribeActionResult.SUCCESS;
    }

    public TribeActionResult demotePlayer(UUID playerRunningCommand, UUID playerToPromote) {
        if (!this.getMembers().contains(playerToPromote.toString())) return TribeActionResult.THEY_NOT_IN_TRIBE;

        int runRank = this.getRankOf(playerRunningCommand.toString()).asInt();
        int targetRank = this.getRankOf(playerToPromote.toString()).asInt();

        if (runRank <= targetRank) return TribeActionResult.LOW_RANK;

        int newRank = targetRank - 1;
        if (newRank < 0) return TribeActionResult.RANK_DOESNT_EXIST;
        this.setRank(playerToPromote, Rank.fromInt(newRank));

        return TribeActionResult.SUCCESS;
    }

    private void setRank(UUID player, Rank rank) {
        this.members.put(player.toString(), rank);
    }

    public String getName() {
        return this.name;
    }

    public Set<String> getMembers(){
        return this.members.keySet();
    }

    public int getCount() {
        return getMembers().size();
    }

    public Rank getRankOf(String playerID){
        return this.members.get(playerID);
    }

    public JsonObject write(){
        JsonObject obj = new JsonObject();

        obj.addProperty("name", this.getName());

        JsonObject memberList = new JsonObject();
        this.getMembers().forEach((uuid) -> {
            memberList.addProperty(uuid, this.getRankOf(uuid).asString());
        });
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

        JsonObject members = obj.get("members").getAsJsonObject();
        for (Map.Entry<String, JsonElement> e : members.entrySet()){
            Rank r = Rank.fromString(e.getValue().getAsString());
            t.addMember(UUID.fromString(e.getKey()), r);
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
        return this.getRankOf(uniqueID.toString()).asInt() >= 3;
    }

    public boolean isViceLeader(UUID uniqueID) {
        return this.getRankOf(uniqueID.toString()).asInt() >= 2;
    }

    public boolean isOfficer(UUID uniqueID) {
        return this.getRankOf(uniqueID.toString()).asInt() >= 1;
    }

    public void removeMember(UUID playerID) {
        this.members.remove(playerID.toString());
        // TODO: logic for passing on leadership or deleting tribe
    }

    public boolean isBanned(UUID uniqueID) {
        return this.bans.contains(uniqueID.toString());
    }


    public enum Rank {
        MEMBER,
        OFFICER,
        VICE_LEADER,
        LEADER;

        static Rank fromString(String s){
            switch (s){
                default:
                    return MEMBER;
                case "officer":
                    return OFFICER;
                case "vice leader":
                    return VICE_LEADER;
                case "leader":
                    return LEADER;
            }
        }

        public String asString(){
            switch (this){
                default:
                    return "member";
                case OFFICER:
                    return "officer";
                case VICE_LEADER:
                    return "vice leader";
                case LEADER:
                    return "leader";
            }
        }

        static Rank fromInt(int s){
            switch (s){
                default:
                    return MEMBER;
                case 1:
                    return OFFICER;
                case 2:
                    return VICE_LEADER;
                case 3:
                    return LEADER;
            }
        }

        public int asInt(){
            switch (this){
                default:
                    return 0;
                case OFFICER:
                    return 1;
                case VICE_LEADER:
                    return 2;
                case LEADER:
                    return 3;
            }
        }
    }
}
