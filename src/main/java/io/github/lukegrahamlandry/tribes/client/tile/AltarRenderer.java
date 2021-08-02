package io.github.lukegrahamlandry.tribes.client.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.lukegrahamlandry.tribes.blocks.AlterBlock;
import io.github.lukegrahamlandry.tribes.init.BannarInit;
import io.github.lukegrahamlandry.tribes.tile.AltarTileEntity;
import net.minecraft.world.level.block.BlockState;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.core.Direction;
import net.minecraft.util.math.vector.Vector3f;

import static io.github.lukegrahamlandry.tribes.blocks.AlterBlock.TYPE;

public class AltarRenderer extends TileEntityRenderer<AltarTileEntity> {
    ModelRenderer model = getModelRender();
    public static ModelRenderer getModelRender() {
        ModelRenderer modelrenderer = new ModelRenderer(64, 64, 0, 0);
        modelrenderer.addBox(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F, 0.0F);
        return modelrenderer;
    }

    public AltarRenderer(TileEntityRendererDispatcher p_i226002_1_) {
        super(p_i226002_1_);
    }

    public void render(AltarTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
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

        RenderMaterial rendermaterial = new RenderMaterial(Atlases.BANNER_SHEET, pattern.location(true));
        this.model.render(matrixStackIn, rendermaterial.buffer(bufferIn, RenderType::entityNoOutline), combinedLightIn, combinedOverlayIn, afloat[0], afloat[1], afloat[2], 1.0F);

        matrixStackIn.popPose();
    }
}
