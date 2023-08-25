package flash.testmod.forge

import com.mojang.brigadier.arguments.ArgumentType
import flash.testmod.*
import flash.testmod.registries.*
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.advancements.CriterionTrigger
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.tags.TagKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.ForgeMod
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.OnDatapackSyncEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.EntityAttributeCreationEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegisterEvent
import net.minecraftforge.server.ServerLifecycleHooks
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import kotlin.reflect.KClass

@Mod(ModMain.MOD_ID)
class ForgeImplementation : PlatformImplementation {

    override val platform: Platform = Platform.FORGE
    override val networkManager: NetworkManager = ForgeNetworkManager

    private val commandArgumentTypes = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, ModMain.MOD_ID)
    private val reloadableResources = arrayListOf<PreparableReloadListener>()

    init {
        with(MOD_BUS) {
            this@ForgeImplementation.commandArgumentTypes.register(this)
            addListener(this@ForgeImplementation::init)
            addListener(this@ForgeImplementation::serverInit)
            ModMain.preInit(this@ForgeImplementation)
            addListener(ModBiomeModifiers::register)
        }
        with(MinecraftForge.EVENT_BUS) {
            addListener(this@ForgeImplementation::registerCommands)
            addListener(this@ForgeImplementation::addReloadListeners)
        }
        ForgeEventHandler.register()
        DistExecutor.safeRunWhenOn(Dist.CLIENT) { DistExecutor.SafeRunnable(ForgeImplementationClient::init) }
    }

    fun serverInit(event: FMLDedicatedServerSetupEvent) {
    }

    fun init(event: FMLCommonSetupEvent) {
        ModMain.LOGGER.info("Initializing...")
        this.networkManager.registerClientBound()
        this.networkManager.registerServerBound()
        ModMain.init()
    }

    override fun environment(): Environment {
        return if (FMLEnvironment.dist.isClient) Environment.CLIENT else Environment.SERVER
    }

    override fun isModInstalled(id: String): Boolean = ModList.get().isLoaded(id)

    override fun registerPermissionValidator() {
        ModMain.permissionValidator = ForgePermissionValidator
    }

    override fun registerSoundEvents() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(ModSounds.registryKey) { helper ->
                ModSounds.register { resourceLocation, sounds -> helper.register(resourceLocation, sounds) }
            }
        }
    }

    override fun registerBlocks() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(ModBlocks.registryKey) { helper ->
                ModBlocks.register { resourceLocation, block -> helper.register(resourceLocation, block) }
            }
        }
    }

    override fun registerParticles() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(ModParticles.registryKey) { helper ->
                ModParticles.register { resourceLocation, particleType -> helper.register(resourceLocation, particleType) }
            }
        }
    }

    override fun registerItems() {
        with(MOD_BUS) {
            addListener<RegisterEvent> { event ->
                event.register(ModItems.registryKey) { helper ->
                    ModItems.register { resourceLocation, item -> helper.register(resourceLocation, item) }
                }
            }
            addListener<RegisterEvent> { event ->
                event.register(Registries.CREATIVE_MODE_TAB) { helper ->
                    ModCreativeTabs.register { holder ->
                        val tab = CreativeModeTab.builder()
                            .title(holder.displayName)
                            .icon(holder.displayIconProvider)
                            .displayItems(holder.entryCollector)
                            .withTabsBefore(*CreativeModeTabs.allTabs().mapNotNull { BuiltInRegistries.CREATIVE_MODE_TAB.getKey(it) }.toTypedArray())
                            .build()
                        helper.register(holder.key, tab)
                        tab
                    }
                }
            }
        }
    }

    override fun registerEntityTypes() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(ModEntities.registryKey) { helper ->
                ModEntities.register { resourceLocation, type -> helper.register(resourceLocation, type) }
            }
        }
    }

    override fun registerEntityAttributes() {
        MOD_BUS.addListener<EntityAttributeCreationEvent> { event ->
            ModEntities.registerAttributes { entityType, builder ->
                builder.add(ForgeMod.ENTITY_GRAVITY.get())
                    .add(ForgeMod.NAMETAG_DISTANCE.get())
                    .add(ForgeMod.SWIM_SPEED.get())
                event.put(entityType, builder.build())
            }
        }
    }

    override fun registerBlockEntityTypes() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(ModBlockEntities.registryKey) { helper ->
                ModBlockEntities.register { resourceLocation, type -> helper.register(resourceLocation, type) }
            }
        }
    }

    override fun registerWorldGenFeatures() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(ModFeatures.registryKey) { helper ->
                ModFeatures.register { resourceLocation, feature -> helper.register(resourceLocation, feature) }
            }
        }
    }

    override fun addFeatureToWorldGen(
        feature: ResourceKey<PlacedFeature>,
        step: GenerationStep.Decoration,
        validTag: TagKey<Biome>?
    ) {
        ModBiomeModifiers.add(feature, step, validTag)
    }

    override fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerCommandArgument(
        resourceLocation: ResourceLocation,
        argumentClass: KClass<A>,
        serializer: ArgumentTypeInfo<A, T>
    ) {
        this.commandArgumentTypes.register(resourceLocation.path) { ArgumentTypeInfos.registerByClass(argumentClass.java, serializer) }
    }

    private fun registerCommands(e: RegisterCommandsEvent) {
        ModCommands.register(e.dispatcher, e.buildContext, e.commandSelection)
    }

    override fun <T : GameRules.Value<T>> registerGameRule(
        name: String,
        category: GameRules.Category,
        type: GameRules.Type<T>
    ): GameRules.Key<T> = GameRules.register(name, category, type)

    override fun <T : CriterionTrigger<*>> registerCriteria(criteria: T): T = CriteriaTriggers.register(criteria)

    override fun registerResourceReloader(
        resourceLocation: ResourceLocation,
        reloader: PreparableReloadListener,
        type: PackType,
        dependencies: Collection<ResourceLocation>
    ) {
        if (type == PackType.SERVER_DATA) {
            this.reloadableResources += reloader
        }
        else {
            ForgeImplementationClient.registerResourceReloader(reloader)
        }
    }

    override fun server(): MinecraftServer? = ServerLifecycleHooks.getCurrentServer()

    private fun addReloadListeners(e: AddReloadListenerEvent) {
        this.reloadableResources.forEach(e::addListener)
    }
}