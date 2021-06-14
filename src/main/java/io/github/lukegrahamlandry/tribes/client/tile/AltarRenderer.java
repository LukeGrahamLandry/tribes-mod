package io.github.lukegrahamlandry.tribes.client.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import io.github.lukegrahamlandry.tribes.init.BannarInit;
import io.github.lukegrahamlandry.tribes.tile.AltarTileEntity;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.math.vector.Vector3f;

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

    public void render(AltarTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        String bannerKey = tileEntityIn.getBannerKey();
        if (bannerKey == null) return;
        BannerPattern pattern = BannarInit.get(bannerKey);

        matrixStackIn.push();
        matrixStackIn.translate(0.5D, 1D, 0.5D);
        matrixStackIn.scale(0.5F, 0.5F, 0.5F);
        float spin = (tileEntityIn.getWorld().getDayTime() + partialTicks) / 30.0F;
        matrixStackIn.rotate(Vector3f.YP.rotation(spin));

        // todo: shift when is double altar
        // todo: fix the positioning of the first side of banner and inverse the second
        // or new RenderMaterial(Atlases.SHIELD_ATLAS, pattern.getTextureLocation(false))

        float[] afloat = DyeColor.WHITE.getColorComponentValues();

        RenderMaterial rendermaterial = new RenderMaterial(Atlases.BANNER_ATLAS, pattern.getTextureLocation(true));
        this.model.render(matrixStackIn, rendermaterial.getBuffer(bufferIn, RenderType::getEntityNoOutline), combinedLightIn, combinedOverlayIn, afloat[0], afloat[1], afloat[2], 1.0F);

        matrixStackIn.pop();
    }
}
