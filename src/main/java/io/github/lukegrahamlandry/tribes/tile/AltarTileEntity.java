package io.github.lukegrahamlandry.tribes.tile;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.init.TileEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Constants;

public class AltarTileEntity extends BlockEntity {
    public AltarTileEntity(BlockPos pos, BlockState state) {
        super(TileEntityInit.ALTAR.get(), pos, state);
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
    public void load(CompoundTag tag) {
        super.load(tag);
        this.bannerKey = tag.contains("banner") ? tag.getString("banner") : null;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        if (this.bannerKey != null) tag.putString("banner", this.bannerKey);
        return super.save(tag);
    }


    // block update
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag nbt = new CompoundTag();
        this.save(nbt);

        return new ClientboundBlockEntityDataPacket(this.worldPosition, 1, nbt);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    // chunk load
    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }
}
