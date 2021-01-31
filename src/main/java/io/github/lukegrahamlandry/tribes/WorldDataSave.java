package io.github.lukegrahamlandry.tribes;

import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class WorldDataSave extends WorldSavedData {
    static String ID = "tribes:worlddata";

    public WorldDataSave(){
        super(ID);
    }

    // TODO: something has to call is and markDirty()
    public static WorldDataSave getInstance(World world){
        return ((ServerWorld) world).getSavedData().getOrCreate(WorldDataSave::new, ID);
    }

    @Override
    public void read(CompoundNBT nbt) {
        TribesMain.LOGGER.debug("read save data");
        String data = nbt.getString("tribelist");
        TribesManager.readFromString(data);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        TribesMain.LOGGER.debug("write save data");
        String data = TribesManager.writeToString();
        compound.putString("tribelist", data);

        return compound;
    }
}