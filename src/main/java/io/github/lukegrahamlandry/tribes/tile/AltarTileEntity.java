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
    public void setChanged() {
        super.setChanged();
        TribesMain.LOGGER.debug("display: " + this.bannerKey);
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }

    // use from block
    public void setBannerKey(String key){
        this.bannerKey = key;
        this.setChanged();
    }

    // for render
    public String getBannerKey(){
        return this.bannerKey;
    }

    // saving data
    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.bannerKey = tag.contains("banner") ? tag.getString("banner") : null;
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        if (this.bannerKey != null) tag.putString("banner", this.bannerKey);
        return super.save(tag);
    }


    // block update
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.save(nbt);

        return new SUpdateTileEntityPacket(this.worldPosition, 1, nbt);
    }
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.load(this.getBlockState(), pkt.getTag());
    }

    // chunk load
    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.load(state, tag);
    }
}
