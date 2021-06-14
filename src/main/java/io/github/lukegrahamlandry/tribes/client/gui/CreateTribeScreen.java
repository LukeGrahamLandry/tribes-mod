package io.github.lukegrahamlandry.tribes.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketCreateTribe;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class CreateTribeScreen extends TribeScreen {
    /** TextBox for name input. */
    protected TextFieldWidget nameField;
    /** Button for creating tribe. */
    private Button btnCreateTribe;
    /** Name of tribe to create taken from the TextBox */
    private String tribeName;

    public CreateTribeScreen() {
        super(".createTribeScreen", "textures/gui/create_tribe.png", 176, 88, true);
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
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.nameField.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private boolean isValidName(){
        // max length check is handled by the text field
        return !this.nameField.getText().isEmpty();
    }
}
