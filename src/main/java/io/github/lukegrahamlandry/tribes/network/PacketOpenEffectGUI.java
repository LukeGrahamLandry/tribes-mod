package io.github.lukegrahamlandry.tribes.network;

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
import java.util.UUID;
import java.util.function.Supplier;

public class PacketOpenEffectGUI {
    private final int numGood;
    private final int numBad;
    private final HashMap<Effect, Integer> effects;

    public PacketOpenEffectGUI(ServerPlayerEntity player) {
        this.numGood = TribesManager.getNumberOfGoodEffects(player);
        this.numBad = TribesManager.getNumberOfBadEffects(player);
        Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());
        this.effects = tribe.effects;
    }

    public PacketOpenEffectGUI(int g, int b, HashMap<Effect, Integer> e) {
        this.numGood = g;
        this.numBad = b;
        this.effects = e;
    }

    public static PacketOpenEffectGUI decode(PacketBuffer buf) {
        int goodNum = buf.readInt();
        int badNum = buf.readInt();
        HashMap<Effect, Integer> currentEffects = new HashMap<>();
        while (true){
            boolean done = buf.readBoolean();
            if (done) break;
            else {
                Effect effect = Effect.get(buf.readInt());
                int level = buf.readInt();
                currentEffects.put(effect, level);
            }
        }

        return new PacketOpenEffectGUI(goodNum, badNum, currentEffects);
    }

    public static void encode(PacketOpenEffectGUI packet, PacketBuffer buf) {
        buf.writeInt(packet.numGood);
        buf.writeInt(packet.numBad);
        packet.effects.forEach((effect, level) -> {
            buf.writeBoolean(false);
            buf.writeInt(Effect.getId(effect));
            buf.writeInt(level);
        });
        buf.writeBoolean(true);
    }

    public static void handle(PacketOpenEffectGUI packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Screen gui = new TribeEffectScreen(packet.numGood, packet.numBad, packet.effects);
            Minecraft.getInstance().displayGuiScreen(gui);
        });

        ctx.get().setPacketHandled(true);
    }
}