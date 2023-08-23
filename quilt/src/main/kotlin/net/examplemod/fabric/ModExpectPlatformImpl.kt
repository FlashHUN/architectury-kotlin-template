package net.examplemod.fabric

import org.quiltmc.loader.api.QuiltLoader
import java.nio.file.Path

object ModExpectPlatformImpl {
    /**
     * This is our actual method to [ModExpectPlatform.getConfigDirectory].
     */
    @JvmStatic // Jvm Static is required so that java can access it
    fun getConfigDirectory(): Path {
        return QuiltLoader.getConfigDir()
    }
}