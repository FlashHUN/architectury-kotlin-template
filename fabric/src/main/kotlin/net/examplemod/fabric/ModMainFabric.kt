package net.examplemod.fabric

import net.examplemod.fabriclike.ModMainFabricLike
import net.fabricmc.api.ModInitializer


object ModMainFabric: ModInitializer {
    override fun onInitialize() {
        ModMainFabricLike.init()
    }
}
