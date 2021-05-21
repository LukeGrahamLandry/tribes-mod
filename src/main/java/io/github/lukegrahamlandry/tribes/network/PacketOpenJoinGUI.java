package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.client.JoinTribeScreen;
import io.github.lukegrahamlandry.tribes.client.TribeEffectScreen;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.function.Supplier;

public class PacketOpenJoinGUI {
    private final HashMap<String, Integer> tribes;

    public PacketOpenJoinGUI(ServerPlayerEntity player) {
        this.tribes = new HashMap<>();
        TribesManager.getTribes().forEach((tribe) -> {
            this.tribes.put(tribe.getName(), tribe.getCount());
        });
    }

    public PacketOpenJoinGUI(HashMap<String, Integer> tribes) {
        this.tribes = tribes;
    }

    public static PacketOpenJoinGUI decode(PacketBuffer buf) {
        HashMap<String, Integer> tribes = new HashMap<>();
        while (!buf.readBoolean()){
            tribes.put(buf.readString(), buf.readInt());
        }

        return new PacketOpenJoinGUI(tribes);
    }

    public static void encode(PacketOpenJoinGUI packet, PacketBuffer buf) {
        packet.tribes.forEach((name, members) -> {
            buf.writeBoolean(false);
            buf.writeString(name);
            buf.writeInt(members);
        });
        buf.writeBoolean(true);
    }

    public static void handle(PacketOpenJoinGUI packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Screen gui = new JoinTribeScreen(packet.tribes);
            Minecraft.getInstance().displayGuiScreen(gui);
        });

        ctx.get().setPacketHandled(true);
    }
}