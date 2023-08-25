package flash.testmod.fabric.mixin;

import flash.testmod.ModClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin
{
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;III)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private void inject_render(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        ModClient.INSTANCE.renderOverlay(guiGraphics, partialTick);
    }
}
