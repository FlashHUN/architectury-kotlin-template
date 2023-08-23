package net.examplemod.forge

import net.minecraftforge.fml.loading.FMLPaths
import java.nio.file.Path

object ModExpectPlatformImpl {
    /**
     * This is our actual method to [ModExpectPlatform.getConfigDirectory].
     */
    @JvmStatic // Jvm Static is required so that java can access it
    fun getConfigDirectory(): Path {
        return FMLPaths.CONFIGDIR.get()
    }
}