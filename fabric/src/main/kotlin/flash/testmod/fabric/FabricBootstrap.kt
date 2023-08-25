package flash.testmod.fabric

import net.fabricmc.api.ModInitializer

class FabricBootstrap : ModInitializer {

    override fun onInitialize() {
        FabricImplementation.init()
    }

}