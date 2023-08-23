package net.examplemod.quilt

import net.examplemod.fabriclike.ModMainFabricLike
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer

object ModMainQuilt: ModInitializer {
    override fun onInitialize(mod: ModContainer?) {
        ModMainFabricLike.init()
    }
}