package io.github.lukegrahamlandry.tribes.network;


import io.github.lukegrahamlandry.tribes.client.gui.ShowLandOwnerUI;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

// sends the owner of the current chunk to the client for ShowLandOwnerUI to display
public class LandOwnerPacket {
    private UUID entityID;
    private String toDisplay;
    boolean access;

    public LandOwnerPacket(UUID id, String displayText, boolean access) {
        this.entityID = id;
        this.toDisplay = displayText;
        this.access = access;
    }

    public static LandOwnerPacket decode(PacketBuffer buf) {
        LandOwnerPacket packet = new LandOwnerPacket(buf.readUUID(), buf.readUtf(32767), buf.readBoolean());
        return packet;
    }

    public static void encode(LandOwnerPacket packet, PacketBuffer buf) {
        buf.writeUUID(packet.entityID);
        buf.writeUtf(packet.toDisplay);
        buf.writeBoolean(packet.access);
    }

    public static void handle(LandOwnerPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ShowLandOwnerUI.chunkOwnerDisplayForPlayer.put(packet.entityID, packet.toDisplay);
            ShowLandOwnerUI.playerCanAccess.put(packet.entityID, packet.access);
        });

        ctx.get().setPacketHandled(true);
    }

}
