package io.github.lukegrahamlandry.tribes.tribe_data;

import com.google.gson.*;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class Tribe {
    String name;
    String initials;
    HashMap<String, Rank> members;  // key is player uuid
    List<String> bans;
    HashMap<String, Relation> relationToOtherTribes;  // key is tribe name
    List<Long> chunks;
    public Tribe(String tribeName, UUID creater){
        this.name = tribeName;
        this.initials = Character.toString(tribeName.charAt(0));
        this.bans = new ArrayList<>();
        this.members = new HashMap<>();
        this.relationToOtherTribes = new HashMap<>();
        this.chunks = new ArrayList<>();
        this.addMember(creater, Rank.LEADER);
    }

    public TribeActionResult addMember(UUID playerID, Rank rank) {
        if (isBanned(playerID)) return TribeActionResult.BANNED;
        if (TribesManager.playerHasTribe(playerID) || this.getMembers().contains(playerID.toString())) return TribeActionResult.IN_TRIBE;

        this.members.put(playerID.toString(), rank);
        return TribeActionResult.SUCCESS;
    }

    public TribeActionResult banPlayer(UUID playerRunningCommand, UUID playerToBan) {
        if (this.getRankOf(playerRunningCommand.toString()).asInt() <= this.getRankOf(playerToBan.toString()).asInt()) return TribeActionResult.LOW_RANK;

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

    public TribeActionResult setRelation(UUID player, Tribe otherTribe, Relation relation) {
        if (!this.isViceLeader(player)) return TribeActionResult.LOW_RANK;
        if (this.getName().equals(otherTribe.getName())) return TribeActionResult.SAME_TRIBE;

        if (relation == Relation.NONE && this.relationToOtherTribes.containsKey(otherTribe.name)){
            this.relationToOtherTribes.remove(otherTribe.name);
        } else {
            this.relationToOtherTribes.put(otherTribe.name, relation);
        }

        return TribeActionResult.SUCCESS;
    }

    public TribeActionResult trySetInitials(String str, UUID player) {
        if (!this.isViceLeader(player)) return TribeActionResult.LOW_RANK;

        this.initials = str;
        return TribeActionResult.SUCCESS;
    }

    public String getInitials() {
        return this.initials;
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

    // CANNOT be called from the client side
    public void broadcastMessage(String message, PlayerEntity playerRunningCommand){
        ServerWorld world = (ServerWorld) playerRunningCommand.getEntityWorld();

        // show which player ran the command that caused the broadcast
        String name = playerRunningCommand.getName().getString();
        String fullMessage = "<" + name + ">: " + message;

        for (String uuid : this.getMembers()){
            PlayerEntity player = world.getPlayerByUuid(UUID.fromString(uuid));
            if (player != null){
                player.sendStatusMessage(new StringTextComponent(fullMessage), false);
            }
        }
    }

    public int getTribeTier(){
        List<Integer> membersRequired = TribesConfig.getTierThresholds();
        for (int i=membersRequired.size()-1;i>=0;i--){
            if (getCount() >= membersRequired.get(i)) return i+2;
        }

        return 1;
    }

    public JsonObject write(){
        JsonObject obj = new JsonObject();

        obj.addProperty("name", this.getName());

        JsonObject memberList = new JsonObject();
        this.getMembers().forEach((uuid) -> {
            if (this.getRankOf(uuid) == Rank.LEADER){
                obj.addProperty("owner", uuid);
            } else {
                memberList.addProperty(uuid, this.getRankOf(uuid).asString());
            }
        });
        obj.add("members", memberList);

        JsonArray banList = new JsonArray();
        this.bans.forEach(banList::add);
        obj.add("bans", banList);

        JsonObject relationsList = new JsonObject();
        this.relationToOtherTribes.forEach((name, relation) -> {
            relationsList.addProperty(name, relation.asString());
        });
        obj.add("relations", relationsList);

        obj.addProperty("initials", this.getInitials());

        JsonArray chunksList = new JsonArray();
        this.chunks.forEach(chunksList::add);
        obj.add("chunks", chunksList);

        return obj;
    }

    static Tribe fromJson(String str){
        JsonObject obj = new JsonParser().parse(str).getAsJsonObject();

        String name = obj.get("name").getAsString();
        String owner = obj.get("owner").getAsString();
        Tribe tribe = new Tribe(name, UUID.fromString(owner));

        JsonObject members = obj.get("members").getAsJsonObject();
        for (Map.Entry<String, JsonElement> e : members.entrySet()){
            Rank r = Rank.fromString(e.getValue().getAsString());
            tribe.addMember(UUID.fromString(e.getKey()), r);
        }

        JsonArray bans = obj.get("bans").getAsJsonArray();
        for (JsonElement e : bans){
            tribe.banPlayer(UUID.fromString(owner), UUID.fromString(e.getAsString()));
        }

        JsonObject relations = obj.get("relations").getAsJsonObject();
        for (Map.Entry<String, JsonElement> e : relations.entrySet()){
            Relation r = Relation.fromString(e.getValue().getAsString());
            String otherTribeName = e.getKey();
            tribe.relationToOtherTribes.put(otherTribeName, r);
        }

        tribe.initials = obj.get("initials").getAsString();

        JsonArray claimedChunks = obj.get("chunks").getAsJsonArray();
        for (JsonElement e : claimedChunks){
            tribe.chunks.add(e.getAsLong());
        }

        return tribe;
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

        if (this.members.size() == 0){
            TribesManager.deleteTribe(this.getName(), playerID);
            return;
        }

        if (isLeader(playerID)){
            UUID toPromote = null;

            // if there's a vice leader pick them, otherwise officer
            // should use a list instead of set to select the person who joined the tribe first
            for (String testPlayer : getMembers()){
                UUID id = UUID.fromString(testPlayer);
                if (isViceLeader(id)){
                    toPromote = id;
                    break;
                } else if (toPromote == null && isOfficer(id)){
                    toPromote = id;
                }
            }

            // no vice leaders or officer (can't be nobody in tribe cause that would be caught earlier and just delete the tribe)
            if (toPromote == null){
                toPromote = UUID.fromString(getMembers().iterator().next());
            }

            this.setRank(toPromote, Rank.LEADER);
            // TODO broadcast message but needs world
        }
    }

    public boolean isBanned(UUID uniqueID) {
        return this.bans.contains(uniqueID.toString());
    }

    public TribeActionResult claimChunk(long chunk, UUID player) {
        if (!this.isOfficer(player)) return TribeActionResult.LOW_RANK;
        if (LandClaimHelper.getChunkOwner(chunk) != null) return TribeActionResult.ALREADY_CLAIMED;

        if (this.getClaimedChunks().size() >= TribesConfig.getMaxChunksClaimed().get(this.getTribeTier()-1)) return TribeActionResult.CONFIG;

        this.chunks.add(chunk);
        LandClaimHelper.setChunkOwner(chunk, this);

        return TribeActionResult.SUCCESS;
    }

    public TribeActionResult unclaimChunk(long chunk, UUID player) {
        if (!this.isOfficer(player)) return TribeActionResult.LOW_RANK;
        if (!LandClaimHelper.getChunkOwner(chunk).equals(this)) return TribeActionResult.ALREADY_CLAIMED;

        this.chunks.remove(chunk);
        LandClaimHelper.setChunkOwner(chunk, null);

        return TribeActionResult.SUCCESS;
    }

    public List<Long> getClaimedChunks(){
        return this.chunks;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Tribe){
            return this.getName().equals(((Tribe) obj).getName());
        }
        return false;
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

    public enum Relation {
        ALLY,
        ENEMY,
        NONE;

        static Relation fromString(String s) {
            switch (s) {
                default:
                    return ALLY;
                case "enemy":
                    return ENEMY;
                case "none":
                    return NONE;
            }
        }

        public String asString() {
            switch (this) {
                default:
                    return "ally";
                case ENEMY:
                    return "enemy";
                case NONE:
                    return "none";
            }
        }
    }
}
