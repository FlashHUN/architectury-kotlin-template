package flash.testmod.registries

import flash.testmod.client.keybind.*
import flash.testmod.platform.events.PlatformEvents
import net.minecraft.client.KeyMapping


object ModKeybinds {

    init {
        PlatformEvents.CLIENT_TICK_POST.subscribe { this.onTick() }
    }

    private val keyBinds = arrayListOf<ModKeyMapping>()

    // val CUSTOM_KEY = this.queue(CustomKeyMapping)

    fun register(registrar: (KeyMapping) -> Unit) {
        this.keyBinds.forEach(registrar::invoke)
    }

    private fun queue(keyBinding: ModKeyMapping): KeyMapping {
        this.keyBinds.add(keyBinding)
        return keyBinding
    }

    private fun onTick() {
        this.keyBinds.forEach(ModKeyMapping::onTick)
    }
}