package net.examplemod.forge

import dev.architectury.platform.forge.EventBuses
import net.examplemod.ModMain
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(ModMain.MOD_ID)
object ModMainForge {
    init {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(ModMain.MOD_ID, MOD_BUS)
        ModMain.init()
    }
}