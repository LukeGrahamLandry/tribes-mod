package io.github.lukegrahamlandry.tribes.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.network.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketCreateTribe;
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
    /** Texture location for background. */
    private static final ResourceLocation guiTexture = new ResourceLocation(TribesMain.MOD_ID,"textures/gui/create_tribe.png");
    /** Starting X position for the Gui. Inconsistent use for Gui backgrounds. */
    protected int guiLeft;
    /** Starting Y position for the Gui. Inconsistent use for Gui backgrounds. */
    protected int guiTop;
    /** The X size of the inventory window in pixels. */
    protected int xSize = 176;
    /** The Y size of the inventory window in pixels. */
    protected int ySize = 88;
    /** Starting X position for Title String. */
    protected int titleX = 0;
    /** Starting Y position for Title String. */
    protected int titleY = 0;
    /** TextBox for name input. */
    protected TextFieldWidget nameField;
    /** Button for creating tribe. */
    private Button btnCreateTribe;
    /** Name of tribe to create taken from the TextBox */
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

        // Setting positions based on background and window sizes
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        //Setting title positions based on background, window, and text sizes
        this.titleX = (this.width - this.font.getStringPropertyWidth(this.title)) / 2;
        this.titleY = (this.height - this.ySize + 20) / 2;

        //Initialization of create tribe button
        this.btnCreateTribe = this.addButton(new Button(this.guiLeft + 13, (this.height - this.ySize + 110) / 2, 150, 20, new TranslationTextComponent(TribesMain.MOD_ID + ".createTribeButton"), (p_214318_1_) -> {
            NetworkHandler.INSTANCE.sendToServer(new PacketCreateTribe(this.tribeName));
            this.closeScreen();
        }));

        //Initialization of the textbox
        this.nameField = new TextFieldWidget(this.font, this.guiLeft + 13, (this.height - this.ySize + 50) / 2, 150, 20, this.nameField, new TranslationTextComponent("selectWorld.search"));
        this.nameField.setMaxStringLength(TribesConfig.getMaxTribeNameLength());
        this.children.add(this.nameField);
        this.setFocusedDefault(this.nameField);
        this.nameField.setResponder((tribeNameIn) -> {
            this.tribeName = tribeNameIn;
            this.btnCreateTribe.active = isValidName();
        });
        this.btnCreateTribe.active = isValidName();
        super.init();
    }

    //Rendering of background, textbox, and title
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(guiTexture);
        this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.font.func_243248_b(matrixStack, this.title, (float)this.titleX, (float)this.titleY, 4210752);
        this.nameField.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private boolean isValidName(){
        // max length check is handled by the text field
        return !this.nameField.getText().isEmpty();
    }
}
