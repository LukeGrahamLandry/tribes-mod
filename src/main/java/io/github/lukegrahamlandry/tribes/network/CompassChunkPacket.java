package io.github.lukegrahamlandry.tribes.network;


import io.github.lukegrahamlandry.tribes.item.TribeCompass;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
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

    public static CompassChunkPacket decode(FriendlyByteBuf buf) {
        CompassChunkPacket packet = new CompassChunkPacket(buf.readUUID(), buf.readBlockPos());
        return packet;
    }

    public static void encode(CompassChunkPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.entityID);
        buf.writeBlockPos(packet.pos);
    }

    public static void handle(CompassChunkPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            TribeCompass.toLookAt.put(packet.entityID, packet.pos);
        });

        ctx.get().setPacketHandled(true);
    }

}
