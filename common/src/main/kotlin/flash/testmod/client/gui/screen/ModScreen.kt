package flash.testmod.client.gui.screen

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component

abstract class ModScreen(title: Component = CommonComponents.EMPTY) : Screen(title) {
    abstract val screenHotkeys : Map<Int, Runnable>

    protected fun handleKeyPress(keyCode: Int) : Boolean {
        return screenHotkeys[keyCode]?.let {
            it.run()
            true
        } ?: false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (handleKeyPress(keyCode)) {
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}