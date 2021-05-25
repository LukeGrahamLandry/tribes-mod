package io.github.lukegrahamlandry.tribes.network;

import io.github.lukegrahamlandry.tribes.TribesMain;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    // Function increments the ID ensuring no two packets have the same ID
    public static int nextID() {
        return ID++;
    }

    public static void registerMessages(){
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(TribesMain.MOD_ID, "tribes"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(), PacketCreateTribe.class, PacketCreateTribe::toBytes, PacketCreateTribe::new, PacketCreateTribe::handle);
        INSTANCE.registerMessage(nextID(), LandOwnerPacket.class, LandOwnerPacket::encode, LandOwnerPacket::decode, LandOwnerPacket::handle);
        INSTANCE.registerMessage(nextID(), CompassChunkPacket.class, CompassChunkPacket::encode, CompassChunkPacket::decode, CompassChunkPacket::handle);
        INSTANCE.registerMessage(nextID(), SaveEffectsPacket.class, SaveEffectsPacket::toBytes, SaveEffectsPacket::new, SaveEffectsPacket::handle);
        INSTANCE.registerMessage(nextID(), PacketOpenEffectGUI.class, PacketOpenEffectGUI::encode, PacketOpenEffectGUI::decode, PacketOpenEffectGUI::handle);
        INSTANCE.registerMessage(nextID(), PacketJoinTribe.class, PacketJoinTribe::toBytes, PacketJoinTribe::new, PacketJoinTribe::handle);
        INSTANCE.registerMessage(nextID(), PacketOpenJoinGUI.class, PacketOpenJoinGUI::encode, PacketOpenJoinGUI::decode, PacketOpenJoinGUI::handle);
    }
}
