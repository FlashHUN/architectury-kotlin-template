package flash.testmod

import flash.testmod.api.permission.LaxPermissionValidator
import flash.testmod.api.permission.PermissionValidator
import flash.testmod.registries.ModDataProviders
import flash.testmod.util.ifDedicatedServer
import flash.testmod.util.server
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.properties.Delegates

object ModMain {
    const val MOD_ID = "test"
    val LOGGER: Logger = LogManager.getLogger()

    lateinit var implementation: PlatformImplementation
    var permissionValidator: PermissionValidator by Delegates.observable(LaxPermissionValidator().also { it.initialize() }) { _, _, newValue -> newValue.initialize() }

    var isDedicatedServer = false

    fun preInit(implementation: PlatformImplementation) {
        this.implementation = implementation
        implementation.registerPermissionValidator()
        implementation.registerSoundEvents()
        implementation.registerBlocks()
        implementation.registerItems()
        implementation.registerEntityTypes()
        implementation.registerEntityAttributes()
        implementation.registerBlockEntityTypes()
        implementation.registerWorldGenFeatures()
        implementation.registerParticles()
        registerArgumentTypes()
    }

    fun init() {
        ModDataProviders.init()

        ifDedicatedServer {
            isDedicatedServer = true
        }
    }

    fun getLevel(dimension: ResourceKey<Level> = Level.OVERWORLD): Level? {
        return if (isDedicatedServer) {
            server()?.getLevel(dimension)
        } else {
            val mc = Minecraft.getInstance()
            return mc.singleplayerServer?.getLevel(dimension) ?: mc.level
        }
    }

    private fun registerArgumentTypes() {
        //this.implementation.registerCommandArgument(resource("pokemon"), CustomArgumentType::class, SingletonArgumentInfo.contextAware(CustomArgumentType::arg))
    }
}