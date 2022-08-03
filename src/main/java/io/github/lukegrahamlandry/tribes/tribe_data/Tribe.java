package io.github.lukegrahamlandry.tribes.tribe_data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.lukegrahamlandry.tribes.commands.ConfirmCommand;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.events.TribeServer;
import io.github.lukegrahamlandry.tribes.tribe_data.claim.HemiAccessManager;
import io.github.lukegrahamlandry.tribes.tribe_data.claim.LandClaimWrapper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.*;

public class Tribe {
    public HemiAccessManager.Hemi hemiAccess;
    String name;
    String initials;
    public String deity;
    HashMap<String, Rank> members;  // key is player uuid
    List<String> bans;
    public HashMap<String, Relation> relationToOtherTribes;  // key is tribe name
    public List<Long> chunks;

    public int claimDisableTime = 0;
    public int deathIndex = 0;
    public boolean deathWasPVP = false;

    public long lastDeityChangeTime = 0;
    public long lastEffectsChangeTime = 0;

    public int autobanDeathThreshold = 3;
    public int autobanDaysThreshold = 2;
    public Map<Rank, Boolean> autobanRank = new HashMap<>();

    public boolean isPrivate = false;
    public List<String> pendingInvites = new ArrayList<>();

    public HashMap<Effect, Integer> effects;
    public List<BlockPos> bannerPositions = new ArrayList<>();

    public Tribe(String tribeName, UUID creater){
        this.name = tribeName;
        this.initials = Character.toString(tribeName.charAt(0));
        this.bans = new ArrayList<>();
        this.members = new HashMap<>();
        this.relationToOtherTribes = new HashMap<>();
        this.chunks = new ArrayList<>();
        this.effects = new HashMap<>();
        this.addMember(creater, Rank.LEADER);
        this.hemiAccess = HemiAccessManager.Hemi.NONE;

        autobanRank.put(Rank.MEMBER, true);
        autobanRank.put(Rank.OFFICER, true);
        autobanRank.put(Rank.VICE_LEADER, false);
        autobanRank.put(Rank.LEADER, false);
    }

    public TribeErrorType addMember(UUID playerID, Rank rank) {
        if (isBanned(playerID)) return TribeErrorType.BANNED;
        if (TribesManager.playerHasTribe(playerID) || this.getMembers().contains(playerID.toString())) return TribeErrorType.IN_TRIBE;

        this.members.put(playerID.toString(), rank);
        return TribeErrorType.SUCCESS;
    }

    public TribeErrorType banPlayer(UUID playerRunningCommand, UUID playerToBan) {
        if (this.getRankOf(playerRunningCommand.toString()).asInt() <= this.getRankOf(playerToBan.toString()).asInt()) return TribeErrorType.LOW_RANK;

        if (this.getMembers().contains(playerToBan.toString())){
           this.removeMember(playerToBan);
        }

        this.bans.add(playerToBan.toString());

        return TribeErrorType.SUCCESS;
    }

    public TribeErrorType unbanPlayer(UUID playerRunningCommand, UUID playerToUnban) {
        if (!this.isOfficer(playerRunningCommand)) return TribeErrorType.LOW_RANK;

        this.bans.remove(playerToUnban.toString());

        return TribeErrorType.SUCCESS;
    }

    public TribeErrorType promotePlayer(UUID playerRunningCommand, UUID playerToPromote) {
        if (!this.getMembers().contains(playerToPromote.toString())) return TribeErrorType.THEY_NOT_IN_TRIBE;

        int runRank = this.getRankOf(playerRunningCommand.toString()).asInt();
        int targetRank = this.getRankOf(playerToPromote.toString()).asInt();

        if ((runRank - 1) < targetRank) return TribeErrorType.LOW_RANK;

        int newRank = targetRank + 1;
        if (newRank > Rank.LEADER.asInt()) return TribeErrorType.RANK_DOESNT_EXIST;
        if (newRank == Rank.LEADER.asInt()) {
            this.setRank(playerRunningCommand, Rank.VICE_LEADER);
        }

        this.setRank(playerToPromote, Rank.fromInt(newRank));

        return TribeErrorType.SUCCESS;
    }

    public TribeErrorType demotePlayer(UUID playerRunningCommand, UUID playerToPromote) {
        if (!this.getMembers().contains(playerToPromote.toString())) return TribeErrorType.THEY_NOT_IN_TRIBE;

        int runRank = this.getRankOf(playerRunningCommand.toString()).asInt();
        int targetRank = this.getRankOf(playerToPromote.toString()).asInt();

        if (runRank <= targetRank) return TribeErrorType.LOW_RANK;

        int newRank = targetRank - 1;
        if (newRank < 0) return TribeErrorType.RANK_DOESNT_EXIST;
        this.setRank(playerToPromote, Rank.fromInt(newRank));

        return TribeErrorType.SUCCESS;
    }

    public TribeErrorType setRelation(UUID player, Tribe otherTribe, Relation relation) {
        if (!this.isViceLeader(player)) return TribeErrorType.LOW_RANK;
        if (this.getName().equals(otherTribe.getName())) return TribeErrorType.SAME_TRIBE;

        if (relation == Relation.NONE && this.relationToOtherTribes.containsKey(otherTribe.name)){
            this.relationToOtherTribes.remove(otherTribe.name);
        } else {
            this.relationToOtherTribes.put(otherTribe.name, relation);
        }

        return TribeErrorType.SUCCESS;
    }

    public TribeErrorType trySetInitials(String str, UUID player) {
        if (!this.isViceLeader(player)) return TribeErrorType.LOW_RANK;
        if (str.length() > 4) return TribeErrorType.LONG_NAME;

        this.initials = str;
        for (String uuid : this.getMembers()){
            PlayerEntity toUpdate = TribeServer.getPlayerByUuid(UUID.fromString(uuid));
            if (toUpdate != null) toUpdate.refreshDisplayName();
        }

        return TribeErrorType.SUCCESS;
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
    public void broadcastMessageNoCause(TribeSuccessType action, Object... args){
        ITextComponent text = action.getBlueText(args);

        for (String uuid : this.getMembers()){
            PlayerEntity player = TribeServer.getPlayerByUuid(UUID.fromString(uuid));
            if (player != null){
                player.displayClientMessage(text, false);
            }
        }
    }

    public void broadcastMessage(TribeSuccessType action, UUID causingPlayer, Object... args){
        ITextComponent text = action.getTextPrefixPlayer(causingPlayer, args);
        ITextComponent plainText = action.getText(args);

        for (String uuid : this.getMembers()){
            PlayerEntity player = TribeServer.getPlayerByUuid(UUID.fromString(uuid));
            if (player != null){
                boolean isCausingPlayer = uuid.equals(causingPlayer.toString());
                player.displayClientMessage(isCausingPlayer ? plainText : text, false);
            }
        }
    }


    public void broadcastMessage(TribeSuccessType action, PlayerEntity causingPlayer, Object... args){
        this.broadcastMessage(action, causingPlayer.getUUID(), args);
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


        JsonArray bannerList = new JsonArray();
        this.bannerPositions.forEach((pos) -> bannerList.add(pos.asLong()));
        obj.add("bannerClaims", bannerList);

        if (this.hemiAccess == HemiAccessManager.Hemi.NEGATIVE){
            obj.addProperty("hemi", 0);
        } else if (this.hemiAccess == HemiAccessManager.Hemi.NONE || this.hemiAccess == null){
            obj.addProperty("hemi", 1);
        } else if (this.hemiAccess == HemiAccessManager.Hemi.POSITIVE){
            obj.addProperty("hemi", 2);
        }
        JsonObject effectMap = new JsonObject();
        this.effects.forEach((effect, level) -> {
            String key = String.valueOf(Effect.getId(effect));
            effectMap.addProperty(key, level);
        });
        obj.add("effects", effectMap);

        obj.addProperty("deity", this.deity == null ? "NONE" : this.deity);

        obj.addProperty("deitytime", this.lastDeityChangeTime);
        obj.addProperty("effectstime", this.lastEffectsChangeTime);

        obj.addProperty("autobandeaths", this.autobanDeathThreshold);
        obj.addProperty("autobandays", this.autobanDaysThreshold);
        this.autobanRank.forEach((rank, value) -> {
            obj.addProperty("autoban" + rank.asString(), value);
        });

        obj.addProperty("private", this.isPrivate);

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

        if (obj.has("bannerClaims")){
            JsonArray bannerdata = obj.get("bannerClaims").getAsJsonArray();
            for (JsonElement e : bannerdata){
                BlockPos bannerPos = new BlockPos(BlockPos.of(e.getAsLong()));
                tribe.bannerPositions.add(bannerPos);
            }
        }

        int hemi = obj.get("hemi").getAsInt();
        if (hemi == 0){
            tribe.hemiAccess = HemiAccessManager.Hemi.NEGATIVE;
        } else if (hemi == 1){
            tribe.hemiAccess = HemiAccessManager.Hemi.NONE;
        } else if (hemi == 2){
            tribe.hemiAccess = HemiAccessManager.Hemi.POSITIVE;
        }

        if (obj.has("effects")){
            JsonObject effectMap = obj.get("effects").getAsJsonObject();
            for (Map.Entry<String, JsonElement> e : effectMap.entrySet()){
                int id = new Integer(e.getKey());
                Effect effect = Effect.byId(id);
                tribe.effects.put(effect, e.getValue().getAsInt());
            }
        }

        tribe.deity = obj.get("deity").getAsString();
        if (tribe.deity.equals("NONE")) tribe.deity = null;

        tribe.lastDeityChangeTime = obj.get("deitytime").getAsLong();
        tribe.lastEffectsChangeTime = obj.get("effectstime").getAsLong();

        tribe.autobanDeathThreshold = obj.get("autobandeaths").getAsInt();
        tribe.autobanDaysThreshold = obj.get("autobandays").getAsInt();
        tribe.autobanRank.put(Rank.MEMBER, obj.get("autoban" + Rank.MEMBER.asString()).getAsBoolean());
        tribe.autobanRank.put(Rank.OFFICER, obj.get("autoban" + Rank.OFFICER.asString()).getAsBoolean());
        tribe.autobanRank.put(Rank.VICE_LEADER, obj.get("autoban" + Rank.VICE_LEADER.asString()).getAsBoolean());
        tribe.autobanRank.put(Rank.LEADER, obj.get("autoban" + Rank.LEADER.asString()).getAsBoolean());

        if (obj.has("private")){
            tribe.isPrivate = obj.get("private").getAsBoolean();
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
        int oldTier = getTribeTier();

        this.members.remove(playerID.toString());

        if (this.members.size() == 0){
            TribesManager.forceDeleteTribe(this.getName());
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


        // reset effects when you go down a tier
        if (oldTier < getTribeTier()){
            this.effects.clear();
        }

        PlayerEntity left = TribeServer.getPlayerByUuid(playerID);
        if (left != null) this.broadcastMessageNoCause(TribeSuccessType.SOMEONE_LEFT, left);
    }

    public boolean isBanned(UUID uniqueID) {
        return this.bans.contains(uniqueID.toString());
    }

    public TribeErrorType commandClaimChunk(long chunk, UUID player) {
        if (!this.isOfficer(player)) return TribeErrorType.LOW_RANK;
        if (LandClaimWrapper.getCommandManager().getChunkOwner(chunk) != null) return TribeErrorType.ALREADY_CLAIMED;

        if (this.getClaimedChunks().size() >= TribesConfig.getMaxChunksClaimed().get(this.getTribeTier()-1)) return TribeErrorType.CONFIG;

        this.chunks.add(chunk);
        LandClaimWrapper.getCommandManager().setChunkOwner(chunk, this);

        return TribeErrorType.SUCCESS;
    }

    public TribeErrorType commandUnclaimChunk(long chunk, UUID player) {
        if (!this.isOfficer(player)) return TribeErrorType.LOW_RANK;
        if (!LandClaimWrapper.getCommandManager().getChunkOwner(chunk).equals(this)) return TribeErrorType.ALREADY_CLAIMED;

        this.chunks.remove(chunk);
        LandClaimWrapper.getCommandManager().setChunkOwner(chunk, null);

        return TribeErrorType.SUCCESS;
    }

    public TribeErrorType bannerClaim(PlayerEntity player, BlockPos pos) {
        if (!this.isOfficer(player.getUUID())) return TribeErrorType.LOW_RANK;
        if (!LandClaimWrapper.getBannerManager().canClaim(player, pos)) return TribeErrorType.ALREADY_CLAIMED;

        if (!checkAroundBanner(player.level, pos)) return TribeErrorType.BANNER_NEEDS_AREA;


        if (this.bannerPositions.size() >= TribesConfig.getMaxBannerClaims().get(this.getTribeTier()-1)) return TribeErrorType.CONFIG;

        this.bannerPositions.add(pos);
        LandClaimWrapper.getBannerManager().claim(this, pos);

        return TribeErrorType.SUCCESS;
    }

    private boolean checkAroundBanner(World level, BlockPos pos){
        int r = TribesConfig.bannerOpenAreaRadius.get();
        for (int x=-r;x<r+1;x++){
            for (int z=-r;z<r+1;z++){
                if (!level.canSeeSky(pos.east(x).north(z))){
                    return false;
                }
            }
        }
        return true;
    }

    public TribeErrorType bannerUnclaim(BlockPos pos) {
        this.bannerPositions.remove(pos);
        LandClaimWrapper.getBannerManager().unclaim(pos);

        return TribeErrorType.SUCCESS;
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

    public TribeErrorType validateSelectHemi(PlayerEntity player, String side) {
        int runRank = this.getRankOf(player.getUUID().toString()).asInt();
        if (runRank < TribesConfig.rankToChooseHemi()) return TribeErrorType.LOW_RANK;
        if (this.hemiAccess != HemiAccessManager.Hemi.NONE) return TribeErrorType.HAVE_HEMI;
        if (this.getTribeTier() < TribesConfig.getMinTierToSelectHemi()) return TribeErrorType.WEAK_TRIBE;
        if (TribesConfig.getUseNorthSouthHemisphereDirection()){
            if (!side.equals("north") && !side.equals("west")) return TribeErrorType.INVALID_HEMI;
        } else {
            if (!side.equals("east") && !side.equals("south")) return TribeErrorType.INVALID_HEMI;
        }
        return TribeErrorType.SUCCESS;
    }

    public TribeErrorType selectHemi(PlayerEntity player, String side) {
        int runRank = this.getRankOf(player.getUUID().toString()).asInt();
        if (runRank < TribesConfig.rankToChooseHemi()) return TribeErrorType.LOW_RANK;
        if (this.hemiAccess != HemiAccessManager.Hemi.NONE) return TribeErrorType.HAVE_HEMI;
        if (this.getTribeTier() < TribesConfig.getMinTierToSelectHemi()) return TribeErrorType.WEAK_TRIBE;
        if (TribesConfig.getUseNorthSouthHemisphereDirection()){
            if (!side.equals("north") && !side.equals("west")) return TribeErrorType.INVALID_HEMI;
        } else {
            if (!side.equals("east") && !side.equals("south")) return TribeErrorType.INVALID_HEMI;
        }

        if (side.equals("east") || side.equals("south")) this.hemiAccess = HemiAccessManager.Hemi.POSITIVE;
        else this.hemiAccess = HemiAccessManager.Hemi.NEGATIVE;
        LandClaimWrapper.getHemisphereManager().hemispheres.get(this.hemiAccess).add(this);

        broadcastMessage(TribeSuccessType.CHOOSE_HEMI, player, side);

        return TribeErrorType.SUCCESS;

    }

    public Relation getRelationTo(Tribe other){
        if (other == null || !this.relationToOtherTribes.containsKey(other.name)){
            return Relation.NONE;
        }
        return this.relationToOtherTribes.get(other.name);
    }

    public String getOwner() {
        for (String m : this.members.keySet()){
            if (this.getRankOf(m) == Rank.LEADER){
                return m;
            }
        }
        return null;
    }

    public enum Rank {
        MEMBER,
        OFFICER,
        VICE_LEADER,
        LEADER;

        public static Rank fromString(String s){
            switch (s){
                case "member":
                    return MEMBER;
                case "officer":
                    return OFFICER;
                case "vice leader":
                    return VICE_LEADER;
                case "leader":
                    return LEADER;
            }
            return null;
        }

        public String asString(){
            switch (this){
                case MEMBER:
                    return "member";
                case OFFICER:
                    return "officer";
                case VICE_LEADER:
                    return "vice leader";
                case LEADER:
                    return "leader";  // do not change this string unless you change it in MyTribeScreen.java as well (I was silly and can't be bothered to fix it incase something breaks)
            }
            return null;
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
