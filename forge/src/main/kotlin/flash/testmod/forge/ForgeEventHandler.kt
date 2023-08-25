package flash.testmod.forge

import net.minecraftforge.common.MinecraftForge

object ForgeEventHandler {

    fun register() {
        MinecraftForge.EVENT_BUS.register(this)
    }

}