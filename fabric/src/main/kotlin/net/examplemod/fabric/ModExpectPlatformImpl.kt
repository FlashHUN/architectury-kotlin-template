package net.examplemod.fabric

import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Path

object ModExpectPlatformImpl {
    /**
     * This is our actual method to [ModExpectPlatform.getConfigDirectory].
     */
    @JvmStatic // Jvm Static is required so that java can access it
    fun getConfigDirectory(): Path {
        return FabricLoader.getInstance().configDir
    }
}