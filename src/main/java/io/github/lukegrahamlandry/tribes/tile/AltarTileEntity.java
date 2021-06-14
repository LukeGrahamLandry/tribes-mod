package io.github.lukegrahamlandry.tribes.tile;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.init.TileEntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public class AltarTileEntity extends TileEntity {
    public AltarTileEntity() {
        super(TileEntityInit.ALTAR.get());
    }

    String bannerKey;

    @Override
    public void markDirty() {
        super.markDirty();
        TribesMain.LOGGER.debug("display: " + this.bannerKey);
        this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }

    // use from block
    public void setBannerKey(String key){
        this.bannerKey = key;
        this.markDirty();
    }

    // for render
    public String getBannerKey(){
        return this.bannerKey;
    }

    // saving data
    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        this.bannerKey = tag.contains("banner") ? tag.getString("banner") : null;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        if (this.bannerKey != null) tag.putString("banner", this.bannerKey);
        return super.write(tag);
    }


    // block update
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.write(nbt);

        return new SUpdateTileEntityPacket(this.pos, 1, nbt);
    }
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(this.getBlockState(), pkt.getNbtCompound());
    }

    // chunk load
    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.read(state, tag);
    }
}
