package io.github.lukegrahamlandry.tribes.network;


import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.item.TribeCompass;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

// sends the the chunk for the tribe compass to look at to the client
public class CompassChunkPacket {
    private final BlockPos pos;
    private UUID entityID;

    public CompassChunkPacket(UUID id, BlockPos pos) {
        this.entityID = id;
        this.pos = pos == null ? BlockPos.ZERO : pos;
    }

    public static CompassChunkPacket decode(PacketBuffer buf) {
        CompassChunkPacket packet = new CompassChunkPacket(buf.readUniqueId(), buf.readBlockPos());
        return packet;
    }

    public static void encode(CompassChunkPacket packet, PacketBuffer buf) {
        buf.writeUniqueId(packet.entityID);
        buf.writeBlockPos(packet.pos);
    }

    public static void handle(CompassChunkPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            TribeCompass.toLookAt.put(packet.entityID, packet.pos);
        });

        ctx.get().setPacketHandled(true);
    }

}
