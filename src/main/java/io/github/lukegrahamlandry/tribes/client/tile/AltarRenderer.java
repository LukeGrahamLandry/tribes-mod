package io.github.lukegrahamlandry.tribes.client.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import io.github.lukegrahamlandry.tribes.blocks.AlterBlock;
import io.github.lukegrahamlandry.tribes.init.BannarInit;
import io.github.lukegrahamlandry.tribes.tile.AltarTileEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.core.Direction;

import static io.github.lukegrahamlandry.tribes.blocks.AlterBlock.TYPE;

public class AltarRenderer implements BlockEntityRenderer<AltarTileEntity> {
    ModelPart model;
    public AltarRenderer(BlockEntityRendererProvider.Context provider) {
        ModelPart modelpart = provider.bakeLayer(ModelLayers.BANNER);
        this.model = modelpart.getChild("flag");
    }

    public void render(AltarTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState state = tileEntityIn.getBlockState();
        if (state.getValue(TYPE) == ChestType.LEFT) return;

        String bannerKey = tileEntityIn.getBannerKey();
        if (bannerKey == null) return;
        BannerPattern pattern = BannarInit.get(bannerKey);

        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5D, 1D, 0.5D);

        if (state.getValue(TYPE) == ChestType.RIGHT){
            Direction side = AlterBlock.getDirectionToAttached(state);
            matrixStackIn.translate(side.getStepX() * 0.5D, 0, side.getStepZ() * 0.5D);
        }

        matrixStackIn.scale(0.5F, 0.5F, 0.5F);
        float spin = (tileEntityIn.getLevel().getDayTime() + partialTicks) / 30.0F;
        matrixStackIn.mulPose(Vector3f.YP.rotation(spin));

        // new RenderMaterial(Atlases.SHIELD_ATLAS, pattern.getTextureLocation(false)) would make one instead of two but need to do the other textures

        float[] afloat = DyeColor.WHITE.getTextureDiffuseColors();

        Material rendermaterial = new Material(Sheets.BANNER_SHEET, pattern.location(true));
        this.model.render(matrixStackIn, rendermaterial.buffer(bufferIn, RenderType::entityNoOutline), combinedLightIn, combinedOverlayIn, afloat[0], afloat[1], afloat[2], 1.0F);

        matrixStackIn.popPose();
    }
}
