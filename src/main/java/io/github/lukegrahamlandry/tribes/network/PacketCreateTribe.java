package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.tribe_data.TribeErrorType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketCreateTribe {
    private String tribeName;

    // Read tribe name from PacketBuffer
    public PacketCreateTribe(FriendlyByteBuf buf) {
        this.tribeName = buf.readUtf(32767);
    }

    // Write tribe name to PacketBuffer
    public void toBytes(FriendlyByteBuf buf){
        buf.writeUtf(this.tribeName);
    }

    public PacketCreateTribe(String tribeNameIn){
        this.tribeName = tribeNameIn;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            TribeErrorType result = TribesManager.createNewTribe(tribeName, ctx.get().getSender());
            if (result == TribeErrorType.SUCCESS){
                ctx.get().getSender().sendMessage(TribeSuccessType.MADE_TRIBE.getText(tribeName), ctx.get().getSender().getUUID());
            } else {
                ctx.get().getSender().sendMessage(result.getText(), ctx.get().getSender().getUUID());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
