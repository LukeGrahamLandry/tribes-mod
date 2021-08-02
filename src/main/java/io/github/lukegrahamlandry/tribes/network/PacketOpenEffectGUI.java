package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.client.gui.TribeEffectScreen;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.HashMap;
import java.util.function.Supplier;

public class PacketOpenEffectGUI {
    private final int numGood;
    private final int numBad;
    private final HashMap<MobEffect, Integer> effects;

    public PacketOpenEffectGUI(ServerPlayer player) {
        this.numGood = TribesManager.getNumberOfGoodEffects(player);
        this.numBad = TribesManager.getNumberOfBadEffects(player);
        Tribe tribe = TribesManager.getTribeOf(player.getUUID());
        this.effects = tribe.effects;
    }

    public PacketOpenEffectGUI(int g, int b, HashMap<MobEffect, Integer> e) {
        this.numGood = g;
        this.numBad = b;
        this.effects = e;
    }

    public static PacketOpenEffectGUI decode(FriendlyByteBuf buf) {
        int goodNum = buf.readInt();
        int badNum = buf.readInt();
        HashMap<MobEffect, Integer> currentEffects = new HashMap<>();
        while (true){
            boolean done = buf.readBoolean();
            if (done) break;
            else {
                MobEffect effect = MobEffect.byId(buf.readInt());
                int level = buf.readInt();
                currentEffects.put(effect, level);
            }
        }

        return new PacketOpenEffectGUI(goodNum, badNum, currentEffects);
    }

    public static void encode(PacketOpenEffectGUI packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.numGood);
        buf.writeInt(packet.numBad);
        packet.effects.forEach((effect, level) -> {
            buf.writeBoolean(false);
            buf.writeInt(MobEffect.getId(effect));
            buf.writeInt(level);
        });
        buf.writeBoolean(true);
    }

    public static void handle(PacketOpenEffectGUI packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> doOpen(packet));
        ctx.get().setPacketHandled(true);
    }


    @OnlyIn(Dist.CLIENT)
    private static void doOpen(PacketOpenEffectGUI packet){
        Screen gui = new TribeEffectScreen(packet.numGood, packet.numBad, packet.effects);
        Minecraft.getInstance().setScreen(gui);
    }
}