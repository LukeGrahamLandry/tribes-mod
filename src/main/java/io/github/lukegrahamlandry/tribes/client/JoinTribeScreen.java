package io.github.lukegrahamlandry.tribes.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.network.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketCreateTribe;
import io.github.lukegrahamlandry.tribes.network.PacketJoinTribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.*;

public class JoinTribeScreen extends TribeScreen {
    private final Map<String, Integer> tribes;
    private final List<String> names;
    private Button laterButton;
    private Button createButton;
    private int page = 1;

    public JoinTribeScreen(Map<String, Integer> tribes) {
        super(".createTribeScreen", "textures/gui/create_tribe.png", 176, 88, false);
        this.tribes = tribes;
        this.names = new ArrayList<>();
        Collections.addAll(this.names, (String[]) this.tribes.keySet().toArray());
    }


    @Override
    public void tick() {

    }

    // TODO: add a help button for new players

    final int ROWS_OF_BUTTONS = 3;
    @Override
    protected void init() {
        // this.minecraft.keyboardListener.enableRepeatEvents(true);

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.createButton = this.addButton(new Button(this.guiLeft + 13, (this.height - this.ySize + 110) / 2, 150, 20, new StringTextComponent("Create Tribe"), (p_214318_1_) -> {
            this.closeScreen();
            Minecraft.getInstance().displayGuiScreen(new CreateTribeScreen());
        }));

        for (int i=0;i<(ROWS_OF_BUTTONS * 2);i++){
            makeJoinButton(i);
        }

        int baseHeight = (this.height - this.ySize + 110) / 2 + ((this.height - this.ySize + 110) / 2) + ((20 + 10) * ROWS_OF_BUTTONS);
        int baseX = this.guiLeft + 13;

        Button backButton = this.addButton(new Button(baseX, baseHeight + 10, 75, 20, new StringTextComponent("Back"), (p_214318_1_) -> {
            this.page--;
        }));
        backButton.active = this.page > 0;
        Button nextButton = this.addButton(new Button(baseX + 85, baseHeight + 10 + 20 + 10, 75, 20, new StringTextComponent("Next"), (p_214318_1_) -> {
            this.page++;
        }));
        nextButton.active = (this.page * ROWS_OF_BUTTONS * 20) < tribes.size();

        this.laterButton = this.addButton(new Button(this.guiLeft + 13, baseHeight + 30 + 30, 150, 20, new StringTextComponent("Choose Later"), (p_214318_1_) -> {
            this.closeScreen();
        }));

        super.init();
    }

    private void makeJoinButton(int i){
        int index = (this.page * (ROWS_OF_BUTTONS * 2)) + i;
        if (index >= this.tribes.size()) return;

        String name = this.names.get(index);
        int members = this.tribes.get(name);

        int buttonWidth = 75;
        int buttonHeight = 20;
        int x = i > ROWS_OF_BUTTONS ? this.guiLeft + 13 : this.guiLeft + 26 + buttonWidth;
        int y = ((this.height - this.ySize + 110) / 2) + ((buttonHeight + 10) * i);

        this.addButton(new Button(x, y, buttonWidth, buttonHeight, new StringTextComponent(name), (p_214318_1_) -> {
            NetworkHandler.INSTANCE.sendToServer(new PacketJoinTribe(name));
            this.closeScreen();
        }, (p_238659_1_, p_238659_2_, p_238659_3_, p_238659_4_) -> {
            this.renderTooltip(p_238659_2_, Minecraft.getInstance().fontRenderer.trimStringToWidth(new StringTextComponent(String.valueOf(members)), Math.max(width / 2 - 43, 170)), p_238659_3_, p_238659_4_);
        }));
    }
}
