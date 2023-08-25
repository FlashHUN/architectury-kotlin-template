package flash.testmod.registries

import flash.testmod.ModMain
import flash.testmod.util.resource
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener

object ModDataProviders {

    fun init() {

    }

    private fun registerData(key: String, reloader: PreparableReloadListener, dependencies: Collection<ResourceLocation> = emptyList()) {
        ModMain.implementation.registerResourceReloader(resource(key), reloader, PackType.SERVER_DATA, dependencies)
    }

    private fun registerAsset(key: String, reloader: PreparableReloadListener, dependencies: Collection<ResourceLocation> = emptyList()) {
        ModMain.implementation.registerResourceReloader(resource(key), reloader, PackType.CLIENT_RESOURCES, dependencies)
    }

}