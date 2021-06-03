package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.client.JoinTribeScreen;
import io.github.lukegrahamlandry.tribes.client.MyTribeScreen;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

public class PacketOpenMyTribeGUI {
    private final String tribeName;
    private final String rank;
    private final String owner;
    private final int members;
    private final int tier;

    public PacketOpenMyTribeGUI(ServerPlayerEntity player) {
        Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());
        if (tribe != null){
            this.tribeName = tribe.getName();
            this.rank = tribe.getRankOf(player.getUniqueID().toString()).asString();
            this.owner = player.getServerWorld().getPlayerByUuid(UUID.fromString(tribe.getOwner())).getScoreboardName();
            this.members = tribe.getCount();
            this.tier = tribe.getTribeTier();
        } else {
            this.tribeName = "NOT IN TRIBE";
            this.rank = "NONE";
            this.owner = "NONE";
            this.members = 0;
            this.tier = 0;
        }
    }

    public PacketOpenMyTribeGUI(String tribeName, String rank, String owner, int members, int tier) {
        this.tribeName = tribeName;
        this.rank = rank;
        this.owner = owner;
        this.members = members;
        this.tier = tier;
    }

    public static PacketOpenMyTribeGUI decode(PacketBuffer buf) {
        return new PacketOpenMyTribeGUI(buf.readString(), buf.readString(), buf.readString(), buf.readInt(), buf.readInt());
    }

    public static void encode(PacketOpenMyTribeGUI packet, PacketBuffer buf) {
        buf.writeString(packet.tribeName);
        buf.writeString(packet.rank);
        buf.writeString(packet.owner);
        buf.writeInt(packet.members);
        buf.writeInt(packet.tier);
    }

    public static void handle(PacketOpenMyTribeGUI packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Screen gui = new MyTribeScreen(packet.tribeName, packet.rank, packet.owner, packet.members, packet.tier);
            Minecraft.getInstance().displayGuiScreen(gui);
        });

        ctx.get().setPacketHandled(true);
    }
}