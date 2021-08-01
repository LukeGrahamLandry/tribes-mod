package io.github.lukegrahamlandry.tribes.init;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.tile.AltarTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityInit {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, TribesMain.MOD_ID);

    public static final RegistryObject<TileEntityType<AltarTileEntity>> ALTAR
            = TILE_ENTITY_TYPES.register("altar", () -> TileEntityType.Builder.of(AltarTileEntity::new, BlockInit.ALTER.get()).build(null));
}