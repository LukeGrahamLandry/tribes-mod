package io.github.lukegrahamlandry.tribes.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.lukegrahamlandry.tribes.TribesMain;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CreateTribeScreen extends Screen {
    private static final ResourceLocation guiTexture = new ResourceLocation(TribesMain.MOD_ID,"textures/gui/create_tribe2.png");
    /** Starting X position for the Gui. Inconsistent use for Gui backgrounds. */
    protected int guiLeft;
    /** Starting Y position for the Gui. Inconsistent use for Gui backgrounds. */
    protected int guiTop;
    /** The X size of the inventory window in pixels. */
    protected int xSize = 176;
    /** The Y size of the inventory window in pixels. */
    protected int ySize = 88;
    protected int titleX = 8;
    protected int titleY = 6;
    protected TextFieldWidget nameField;
    private Button btnCreateTribe;
    private String tribeName;

    public CreateTribeScreen() {
        super(new TranslationTextComponent(TribesMain.MOD_ID+".createTribeScreen"));
    }

    @Override
    public void tick() {
        this.nameField.tick();
    }

    @Override
    protected void init() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.titleX = (this.width - this.font.getStringPropertyWidth(this.title)) / 2;
        this.titleY = (this.height - this.ySize + 20) / 2;

        this.btnCreateTribe = this.addButton(new Button((this.width - this.font.getStringPropertyWidth(this.title)) / 2, (this.height - this.ySize + 100) / 2, 150, 20, new TranslationTextComponent("selectWorld.create"), (p_214318_1_) -> {
            //this.createWorld();
        }));

        /*this.nameField = new TextFieldWidget(this.font, this.width / 2 - 100, 22, 200, 20, this.nameField, new TranslationTextComponent("selectWorld.search"));
        this.nameField.setResponder((p_214319_1_) -> {
            this.tribeName = p_214319_1_;
            this.btnCreateTribe.active = !this.nameField.getText().isEmpty();
        });
        this.btnCreateTribe = this.addButton(new Button(this.width / 2, this.height - 28, 150, 20, new TranslationTextComponent("selectWorld.create"), (p_214318_1_) -> {
            //this.createWorld();
        }));
        this.btnCreateTribe.active = !this.nameField.getText().isEmpty();
        super.init();*/
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        //this.setListener((IGuiEventListener)null);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(guiTexture);
        //this.blit(matrixStack, i, j, 0, 0, 303, 603);
        this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.font.func_243248_b(matrixStack, this.title, (float)this.titleX, (float)this.titleY, 4210752);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
