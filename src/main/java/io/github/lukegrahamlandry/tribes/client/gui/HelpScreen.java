package io.github.lukegrahamlandry.tribes.client.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Util;

public class HelpScreen extends TribeScreen {
    public HelpScreen() {
        super("", "textures/gui/join_tribe.png", 180, 85, false);
    }

    @Override
    protected void init() {
        // this.minecraft.keyboardListener.enableRepeatEvents(true);

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.addButton(new Button(this.guiLeft + 15, this.guiTop + 15, 150, 20, new TextComponent("Wiki"), (p_214318_1_) -> {
            this.onClose();
            openLink("https://github.com/LukeGrahamLandry/tribes-mod/blob/main/wiki.md");
        }));
        this.addButton(new Button(this.guiLeft + 15, this.guiTop + 15+20+15, 150, 20, new TextComponent("Discord Server"), (p_214318_1_) -> {
            this.onClose();
            openLink("https://discord.gg/uG4DewBcwV");
        }));

        super.init();
    }


    private static void openLink(String uri) {
        Util.getPlatform().openUri(uri);
    }

}
