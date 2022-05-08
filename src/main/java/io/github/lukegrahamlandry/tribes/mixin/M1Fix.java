package io.github.lukegrahamlandry.tribes.mixin;

import net.minecraft.client.MainWindow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.util.function.BiConsumer;

@Mixin(MainWindow.class)
public class M1Fix {
    @Inject(at = @At("HEAD"), method = "setIcon", cancellable = true)
    private void glfwSetWindowIcon(InputStream intbuffer1, InputStream intbuffer2, CallbackInfo ci) {
        if (isAppleSlilicon()) ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "checkGlfwError", cancellable = true)
    private static void checkGlfwError(BiConsumer<Integer, String> j, CallbackInfo ci) {
        if (isAppleSlilicon()) ci.cancel();
    }

    private static boolean isAppleSlilicon() {
        return System.getProperty("os.arch").equals("aarch64") && System.getProperty("os.name").equals("Mac OS X");
    }
}