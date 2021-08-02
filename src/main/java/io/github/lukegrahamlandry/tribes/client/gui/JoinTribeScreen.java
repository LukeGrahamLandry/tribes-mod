package io.github.lukegrahamlandry.tribes.client.gui;

import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketJoinTribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;

import java.util.*;

public class JoinTribeScreen extends TribeScreen {
    private final Map<String, Integer> tribes;
    private final List<String> names;
    private final boolean allowClose;
    private Button laterButton;
    private Button createButton;
    private Button backButton;
    private Button nextButton;
    private int page = 0;

    public JoinTribeScreen(Map<String, Integer> tribes, boolean allowClose) {
        super(".joinTribeScreen", "textures/gui/join_tribe.png", 185, 160, false);
        this.tribes = tribes;
        this.names = new ArrayList<>();
        this.tribes.forEach((name, members) -> this.names.add(name));
        this.allowClose = allowClose;
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.allowClose;
    }

    // TODO: add a help button for new players

    final int TRIBES_PER_PAGE = 3;
    @Override
    protected void init() {
        // this.minecraft.keyboardListener.enableRepeatEvents(true);

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        // (this.height - this.ySize + 110) / 2
        this.createButton = this.addRenderableWidget(new Button(this.guiLeft + 13, this.guiTop + 10, 75, 20, new TextComponent("Create Tribe!"), (p_214318_1_) -> {
            // this.closeScreen();
            Minecraft.getInstance().setScreen(new CreateTribeScreen());
        }));

        this.laterButton = this.addRenderableWidget(new Button(this.guiLeft + 13 + 85, this.guiTop + 10, 75, 20, new TextComponent("Choose Later."), (p_214318_1_) -> {
            if (this.laterButton.active){
                this.onClose();
            }
        }));
        this.laterButton.active = this.allowClose;

        for (int i=0;i<TRIBES_PER_PAGE;i++){
            makeJoinButton(i);
        }

        int baseHeight = this.guiTop + 10 + 10 + ((20 + 10) * TRIBES_PER_PAGE);
        int baseX = this.guiLeft + 13;

        this.backButton = this.addRenderableWidget(new Button(baseX, baseHeight + 20, 75, 20, new TextComponent("Back"), (p_214318_1_) -> {
            if (this.backButton.active){
                this.page--;
                this.clearWidgets();
                this.init();
            }
        }));
        this.backButton.active = this.page > 0;
        this.nextButton = this.addRenderableWidget(new Button(baseX + 85, baseHeight + 20, 75, 20, new TextComponent("Next"), (p_214318_1_) -> {
            if (this.nextButton.active){
                this.page++;
                this.clearWidgets();
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

        this.addRenderableWidget(new Button(x, y, 160, buttonHeight, new TextComponent(name), (p_214318_1_) -> {
            NetworkHandler.INSTANCE.sendToServer(new PacketJoinTribe(name));
            this.onClose();
        }, (p_238659_1_, p_238659_2_, p_238659_3_, p_238659_4_) -> {
            this.renderTooltip(p_238659_2_, Minecraft.getInstance().font.split(new TextComponent(String.valueOf(members) + (members == 1 ? " member" : " members")), Math.max(width / 2 - 43, 170)), p_238659_3_, p_238659_4_);
        }));
    }
}
