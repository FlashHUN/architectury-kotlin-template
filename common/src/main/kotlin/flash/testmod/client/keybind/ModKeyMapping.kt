package flash.testmod.client.keybind

import com.mojang.blaze3d.platform.InputConstants
import flash.testmod.ModMain
import net.minecraft.client.KeyMapping

abstract class ModKeyMapping(
    name: String,
    key: Int,
    category: String? = null,
    type: InputConstants.Type = InputConstants.Type.KEYSYM
): KeyMapping("${ModMain.MOD_ID}.$name", type, key, category ?: "${ModMain.MOD_ID}.category") {

    abstract fun onPress()

    open fun onTick() {
        if (this.consumeClick()) {
            onPress()
        }
    }
}