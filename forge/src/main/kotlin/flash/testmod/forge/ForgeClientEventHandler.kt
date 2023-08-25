package flash.testmod.forge

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.MinecraftForge

@OnlyIn(Dist.CLIENT)
object ForgeClientEventHandler {

    fun register() {
        MinecraftForge.EVENT_BUS.register(this)
    }

}