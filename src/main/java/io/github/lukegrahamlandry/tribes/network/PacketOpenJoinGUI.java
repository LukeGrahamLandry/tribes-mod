package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.client.gui.JoinTribeScreen;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.HashMap;
import java.util.function.Supplier;

public class PacketOpenJoinGUI {
    private final HashMap<String, Integer> tribes;
    boolean allowClose;

    public PacketOpenJoinGUI(ServerPlayer player) {
        this.tribes = new HashMap<>();
        TribesManager.getTribes().forEach((tribe) -> {
            this.tribes.put(tribe.getName(), tribe.getCount());
        });

        this.allowClose = TribesManager.playerHasTribe(player.getUUID())|| !TribesConfig.isTribeRequired();
    }

    public PacketOpenJoinGUI(HashMap<String, Integer> tribes, boolean allowClose) {
        this.tribes = tribes;
        this.allowClose = allowClose;
    }

    public static PacketOpenJoinGUI decode(FriendlyByteBuf buf) {
        HashMap<String, Integer> tribes = new HashMap<>();
        while (!buf.readBoolean()){
            tribes.put(buf.readUtf(32767), buf.readInt());
        }

        return new PacketOpenJoinGUI(tribes, buf.readBoolean());
    }

    public static void encode(PacketOpenJoinGUI packet, FriendlyByteBuf buf) {
        packet.tribes.forEach((name, members) -> {
            buf.writeBoolean(false);
            buf.writeUtf(name);
            buf.writeInt(members);
        });

        buf.writeBoolean(true);

        buf.writeBoolean(packet.allowClose);
    }

    public static void handle(PacketOpenJoinGUI packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> doOpen(packet));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void doOpen(PacketOpenJoinGUI packet){
        Screen gui = new JoinTribeScreen(packet.tribes, packet.allowClose);
        // dont auto close the create screen every tick when tribes are forced
        if (Minecraft.getInstance().screen == null) Minecraft.getInstance().setScreen(gui);
    }
}