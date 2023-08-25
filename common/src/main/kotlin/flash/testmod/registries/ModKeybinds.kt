package flash.testmod.registries

import net.minecraft.client.KeyMapping


object ModKeybinds {

    private val keyBinds = arrayListOf<KeyMapping>()

    // val CUSTOM_KEYBIND = this.queue(CustomKeybind)

    fun register(registrar: (KeyMapping) -> Unit) {
        this.keyBinds.forEach(registrar::invoke)
    }

    private fun queue(keyBinding: KeyMapping): KeyMapping {
        this.keyBinds.add(keyBinding)
        return keyBinding
    }
}