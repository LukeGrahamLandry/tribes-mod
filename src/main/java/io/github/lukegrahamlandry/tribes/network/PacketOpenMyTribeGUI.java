package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.client.gui.MyTribeScreen;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class PacketOpenMyTribeGUI {
    private final String tribeName;
    private final String rank;
    private final String owner;
    private final int members;
    private final int tier;
    private final List<String> goodTribes;
    private final List<String> badTribes;

    public PacketOpenMyTribeGUI(ServerPlayer player) {
        Tribe tribe = TribesManager.getTribeOf(player.getUUID());
        this.goodTribes = new ArrayList<>();
        this.badTribes = new ArrayList<>();
        if (tribe != null){
            this.tribeName = tribe.getName();
            this.rank = tribe.getRankOf(player.getUUID().toString()).asString();
            this.owner = player.getLevel().getPlayerByUUID(UUID.fromString(tribe.getOwner())).getScoreboardName();
            this.members = tribe.getCount();
            this.tier = tribe.getTribeTier();
            tribe.relationToOtherTribes.forEach((name, relation) -> {
                if (relation == Tribe.Relation.ALLY) this.goodTribes.add(name);
                if (relation == Tribe.Relation.ENEMY) this.badTribes.add(name);
            });
        } else {
            this.tribeName = "NOT IN TRIBE";
            this.rank = "NONE";
            this.owner = "NONE";
            this.members = 0;
            this.tier = 0;
        }
    }

    public PacketOpenMyTribeGUI(String tribeName, String rank, String owner, int members, int tier, List<String> goodTribes, List<String> badTribes) {
        this.tribeName = tribeName;
        this.rank = rank;
        this.owner = owner;
        this.members = members;
        this.tier = tier;
        this.goodTribes = goodTribes;
        this.badTribes = badTribes;
    }

    public static PacketOpenMyTribeGUI decode(FriendlyByteBuf buf) {
        return new PacketOpenMyTribeGUI(buf.readUtf(32767), buf.readUtf(32767), buf.readUtf(32767), buf.readInt(), buf.readInt(), PacketUtil.readStringList(buf), PacketUtil.readStringList(buf));
    }

    public static void encode(PacketOpenMyTribeGUI packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.tribeName);
        buf.writeUtf(packet.rank);
        buf.writeUtf(packet.owner);
        buf.writeInt(packet.members);
        buf.writeInt(packet.tier);
        PacketUtil.writeStringList(buf, packet.goodTribes);
        PacketUtil.writeStringList(buf, packet.badTribes);
    }

    public static void handle(PacketOpenMyTribeGUI packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> doOpen(packet));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void doOpen(PacketOpenMyTribeGUI packet) {
        Screen gui = new MyTribeScreen(packet.tribeName, packet.rank, packet.owner, packet.members, packet.tier, packet.goodTribes, packet.badTribes);
        Minecraft.getInstance().setScreen(gui);
    }
}