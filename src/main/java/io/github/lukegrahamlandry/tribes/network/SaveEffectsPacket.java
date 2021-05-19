package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

public class SaveEffectsPacket {
    private final HashMap<Effect, Integer> good;
    private final HashMap<Effect, Integer> bad;

    // send this from the client (effect screen) to save the chosen effects on the server
    public SaveEffectsPacket(HashMap<Effect, Integer> good, HashMap<Effect, Integer> bad){
        this.good = good;
        this.bad = bad;
    }

    public SaveEffectsPacket(PacketBuffer buf) {
        good = new HashMap<>();
        bad = new HashMap<>();

        while (true){
            int type = buf.readInt();
            if (type == 0){
                Effect effect = Effect.get(buf.readInt());
                int level = buf.readInt();
                good.put(effect, level);
            } else if (type == 1){
                Effect effect = Effect.get(buf.readInt());
                int level = buf.readInt();
                bad.put(effect, level);
            } else {
                break;
            }
        }
    }

    public void toBytes(PacketBuffer buf){
        good.forEach((effect, level) -> {
            buf.writeInt(0);  // good
            buf.writeInt(Effect.getId(effect));
            buf.writeInt(level);
        });
        bad.forEach((effect, level) -> {
            buf.writeInt(1);  // bad
            buf.writeInt(Effect.getId(effect));
            buf.writeInt(level);
        });
        buf.writeInt(2);  // done
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            UUID playerID = player.getUniqueID();
            Tribe tribe = TribesManager.getTribeOf(playerID);
            if (tribe != null){
                // TODO: validate numbers of effects

                tribe.effects.clear();
                this.good.forEach((effect, level) -> tribe.effects.put(effect, level));
                this.bad.forEach((effect, level) -> tribe.effects.put(effect, level));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
