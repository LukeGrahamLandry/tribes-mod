package io.github.lukegrahamlandry.tribes.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.lukegrahamlandry.tribes.TribesMain;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TribeScreen extends Screen {
    /**
     * Texture location for background.
     */
    private ResourceLocation guiTexture;
    /**
     * Starting X position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiLeft;
    /**
     * Starting Y position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiTop;
    /**
     * The X size of the inventory window in pixels.
     */
    protected int xSize;
    /**
     * The Y size of the inventory window in pixels.
     */
    protected int ySize;
    /**
     * Starting X position for Title String.
     */
    protected int titleX = 0;
    /**
     * Starting Y position for Title String.
     */
    protected int titleY = 0;

    protected TribeScreen(String translationKey, String guiTextureIn, int xSizeIn, int ySizeIn) {
        super(new TranslationTextComponent(TribesMain.MOD_ID + translationKey));
        this.guiTexture = new ResourceLocation(TribesMain.MOD_ID, guiTextureIn);
        this.xSize = xSizeIn;
        this.ySize = ySizeIn;
    }

    public ResourceLocation getGuiTexture() {
        return guiTexture;
    }

    @Override
    protected void init() {
        // Setting positions based on background and window sizes
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        //Setting title positions based on background, window, and text sizes
        this.titleX = (this.width - this.font.getStringPropertyWidth(this.title)) / 2;
        this.titleY = (this.height - this.ySize + 20) / 2;

        super.init();
    }

    //Rendering of background, textbox, and title
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(guiTexture);
        this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.font.func_243248_b(matrixStack, this.title, (float) this.titleX, (float) this.titleY, 4210752);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
