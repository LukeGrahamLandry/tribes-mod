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
    private Button backButton;
    private Button nextButton;
    private int page = 0;

    public JoinTribeScreen(Map<String, Integer> tribes) {
        super(".joinTribeScreen", "textures/gui/join_tribe.png", 185, 160, false);
        this.tribes = tribes;
        this.names = new ArrayList<>();
        this.tribes.forEach((name, members) -> this.names.add(name));
    }

    @Override
    public void tick() {

    }

    // TODO: add a help button for new players

    final int TRIBES_PER_PAGE = 3;
    @Override
    protected void init() {
        // this.minecraft.keyboardListener.enableRepeatEvents(true);

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;


        // (this.height - this.ySize + 110) / 2
        this.createButton = this.addButton(new Button(this.guiLeft + 13, this.guiTop + 10, 75, 20, new StringTextComponent("Create Tribe!"), (p_214318_1_) -> {
            this.closeScreen();
            Minecraft.getInstance().displayGuiScreen(new CreateTribeScreen());
        }));

        this.laterButton = this.addButton(new Button(this.guiLeft + 13 + 85, this.guiTop + 10, 75, 20, new StringTextComponent("Choose Later."), (p_214318_1_) -> {
            this.closeScreen();
        }));

        for (int i=0;i<TRIBES_PER_PAGE;i++){
            makeJoinButton(i);
        }

        int baseHeight = this.guiTop + 10 + 10 + ((20 + 10) * TRIBES_PER_PAGE);
        int baseX = this.guiLeft + 13;

        this.backButton = this.addButton(new Button(baseX, baseHeight + 20, 75, 20, new StringTextComponent("Back"), (p_214318_1_) -> {
            if (this.backButton.active){
                this.page--;
                this.buttons.clear();
                this.init();
            }
        }));
        this.backButton.active = this.page > 0;
        this.nextButton = this.addButton(new Button(baseX + 85, baseHeight + 20, 75, 20, new StringTextComponent("Next"), (p_214318_1_) -> {
            if (this.nextButton.active){
                this.page++;
                this.buttons.clear();
                this.init();
            }
        }));
        this.nextButton.active = ((this.page + 1) * TRIBES_PER_PAGE) < tribes.size();

        super.init();
    }

    private void makeJoinButton(int i){
        int index = (this.page * TRIBES_PER_PAGE) + i;
        if (index >= this.tribes.size()) return;

        String name = this.names.get(index);
        int members = this.tribes.get(name);

        int buttonHeight = 20;
        int x = this.guiLeft + 13;
        int y =  this.guiTop + 30 + 10 + ((buttonHeight + 10) * (i % TRIBES_PER_PAGE));

        this.addButton(new Button(x, y, 160, buttonHeight, new StringTextComponent(name), (p_214318_1_) -> {
            NetworkHandler.INSTANCE.sendToServer(new PacketJoinTribe(name));
            this.closeScreen();
        }, (p_238659_1_, p_238659_2_, p_238659_3_, p_238659_4_) -> {
            this.renderTooltip(p_238659_2_, Minecraft.getInstance().fontRenderer.trimStringToWidth(new StringTextComponent(String.valueOf(members) + (members == 1 ? " member" : " members")), Math.max(width / 2 - 43, 170)), p_238659_3_, p_238659_4_);
        }));
    }
}
