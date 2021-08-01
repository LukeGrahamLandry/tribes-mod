package io.github.lukegrahamlandry.tribes.client.gui;

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
    private ResourceLocation guiTexture2;
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
    protected boolean renderTitle;
    // Boolean for if GUI size exceeds 256x256
    protected boolean largeGUI = false;

    protected TribeScreen(String translationKey, String guiTextureIn, int xSizeIn, int ySizeIn, boolean renderTitleIn) {
        super(new TranslationTextComponent(TribesMain.MOD_ID + translationKey));
        this.guiTexture = new ResourceLocation(TribesMain.MOD_ID, guiTextureIn);
        this.xSize = xSizeIn;
        this.ySize = ySizeIn;
        this.renderTitle = renderTitleIn;
    }

    protected TribeScreen(String translationKey, String guiTexture1In, String guiTexture2In, int xSizeIn, int ySizeIn, boolean renderTitleIn) {
        super(new TranslationTextComponent(TribesMain.MOD_ID + translationKey));
        this.guiTexture = new ResourceLocation(TribesMain.MOD_ID, guiTexture1In);
        this.guiTexture2 = new ResourceLocation(TribesMain.MOD_ID, guiTexture2In);
        this.xSize = xSizeIn;
        this.ySize = ySizeIn;
        this.renderTitle = renderTitleIn;
        this.largeGUI = true;
    }

    public ResourceLocation getGuiTexture() {
        return guiTexture;
    }

    @Override
    protected void init() {

        // Setting positions based on background and window sizes
        this.guiLeft = !largeGUI ? (this.width - this.xSize) / 2 : (this.width - 2 * this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        //Setting title positions based on background, window, and text sizes
        this.titleX = (this.width - this.font.width(this.title)) / 2;
        this.titleY = (this.height - this.ySize + 20) / 2;

        super.init();
    }

    //Rendering of background, textbox, and title
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if(largeGUI){
            this.minecraft.getTextureManager().bind(guiTexture);
            this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, 2*this.xSize, this.ySize);
            this.minecraft.getTextureManager().bind(guiTexture2);
            this.blit(matrixStack, this.guiLeft+xSize, this.guiTop, 0, 0, 2*this.xSize, this.ySize);
        }else {
            this.minecraft.getTextureManager().bind(guiTexture);
            this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        }
        if(renderTitle) this.font.draw(matrixStack, this.title, (float) this.titleX, (float) this.titleY, 4210752);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
