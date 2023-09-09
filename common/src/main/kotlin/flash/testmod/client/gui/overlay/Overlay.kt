package flash.testmod.client.gui.overlay

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen

object Overlay : Gui(Minecraft.getInstance(), Minecraft.getInstance().itemRenderer) {

    private val screenExemptions: List<Class<out Screen>> = listOf(
        ChatScreen::class.java
    )

    override fun render(g: GuiGraphics, partialTick: Float) {
        val minecraft = Minecraft.getInstance()
        if (minecraft.currentServer != null) {
            if (screenExemptions.contains(minecraft.screen?.javaClass as Class<out Screen>))
                return
        }
        if (minecraft.options.renderDebug)
            return
    }
}