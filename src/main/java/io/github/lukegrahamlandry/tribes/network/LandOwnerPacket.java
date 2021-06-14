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

    public LandOwnerPacket(UUID id, String displayText) {
        this.entityID = id;
        this.toDisplay = displayText;
    }

    public static LandOwnerPacket decode(PacketBuffer buf) {
        LandOwnerPacket packet = new LandOwnerPacket(buf.readUniqueId(), buf.readString());
        return packet;
    }

    public static void encode(LandOwnerPacket packet, PacketBuffer buf) {
        buf.writeUniqueId(packet.entityID);
        buf.writeString(packet.toDisplay);
    }

    public static void handle(LandOwnerPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ShowLandOwnerUI.chunkOwnerDisplayForPlayer.put(packet.entityID, packet.toDisplay);
        });

        ctx.get().setPacketHandled(true);
    }

}
