package flash.testmod.fabric

import com.mojang.brigadier.arguments.ArgumentType
import flash.testmod.*
import flash.testmod.platform.events.*
import flash.testmod.registries.*
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.advancements.CriterionTrigger
import net.minecraft.client.Minecraft
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.tags.TagKey
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.InteractionResult
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlin.reflect.KClass


object FabricImplementation: PlatformImplementation {

    override val platform: Platform = Platform.FABRIC
    override val networkManager: NetworkManager = FabricNetworkManager
    private var server: MinecraftServer? = null

    fun init() {
        ModMain.preInit(this)
        this.networkManager.registerServerBound()
        ModMain.init()

        ServerLifecycleEvents.SERVER_STARTING.register { server ->
            this.server = server
            PlatformEvents.SERVER_STARTING.post(ServerEvent.Starting(server))
        }
        ServerLifecycleEvents.SERVER_STARTED.register { server -> PlatformEvents.SERVER_STARTED.post(ServerEvent.Started(server)) }
        ServerLifecycleEvents.SERVER_STOPPING.register { server -> PlatformEvents.SERVER_STOPPING.post(ServerEvent.Stopping(server)) }
        ServerLifecycleEvents.SERVER_STOPPED.register { server -> PlatformEvents.SERVER_STOPPED.post(ServerEvent.Stopped(server)) }
        ServerTickEvents.START_SERVER_TICK.register { server -> PlatformEvents.SERVER_TICK_PRE.post(ServerTickEvent.Pre(server)) }
        ServerTickEvents.END_SERVER_TICK.register { server -> PlatformEvents.SERVER_TICK_POST.post(ServerTickEvent.Post(server)) }
        ServerPlayConnectionEvents.JOIN.register { handler, _, _ -> PlatformEvents.SERVER_PLAYER_LOGIN.post(ServerPlayerEvent.Login(handler.player)) }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ -> PlatformEvents.SERVER_PLAYER_LOGOUT.post(ServerPlayerEvent.Logout(handler.player)) }
        ServerLivingEntityEvents.ALLOW_DEATH.register { entity, _, _ ->
            if (entity is ServerPlayer) {
                PlatformEvents.PLAYER_DEATH.postThen(
                    event = ServerPlayerEvent.Death(entity),
                    ifSucceeded = {},
                    ifCanceled = { return@register false }
                )
            }
            return@register true
        }
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register { player, _, _ ->
            PlatformEvents.CHANGE_DIMENSION.post(ChangeDimensionEvent(player))
        }
        UseBlockCallback.EVENT.register { player, _, hand, hitResult ->
            val serverPlayer = player as? ServerPlayer ?: return@register InteractionResult.PASS
            PlatformEvents.RIGHT_CLICK_BLOCK.postThen(
                event = ServerPlayerEvent.RightClickBlock(serverPlayer, hitResult.blockPos, hand, hitResult.direction),
                ifSucceeded = {},
                ifCanceled = { return@register InteractionResult.FAIL }
            )
            return@register InteractionResult.PASS
        }
        UseEntityCallback.EVENT.register { player, _, hand, entity, _ ->
            val item = player.getItemInHand(hand)
            val serverPlayer = player as? ServerPlayer ?: return@register InteractionResult.PASS

            PlatformEvents.RIGHT_CLICK_ENTITY.postThen(
                event = ServerPlayerEvent.RightClickEntity(serverPlayer, item, hand, entity),
                ifSucceeded = {},
                ifCanceled = { return@register InteractionResult.FAIL }
            )

            return@register InteractionResult.PASS
        }

        CommandRegistrationCallback.EVENT.register(ModCommands::register)
    }

    override fun environment(): Environment {
        return when(FabricLoader.getInstance().environmentType) {
            EnvType.CLIENT -> Environment.CLIENT
            EnvType.SERVER -> Environment.SERVER
            else -> throw IllegalStateException("Fabric implementation cannot resolve environment yet")
        }
    }

    override fun isModInstalled(id: String): Boolean = FabricLoader.getInstance().isModLoaded(id)

    override fun registerPermissionValidator() {
        if (this.isModInstalled("fabric-permissions-api-v0")) {
            ModMain.permissionValidator = FabricPermissionValidator()
        }
    }

    override fun registerSoundEvents() {
        ModSounds.register { resourceLocation, soundEvent -> Registry.register(ModSounds.registry, resourceLocation, soundEvent) }
    }

    override fun registerItems() {
        ModItems.register { resourceLocation, item -> Registry.register(ModItems.registry, resourceLocation, item) }
        ModCreativeTabs.register { provider ->
            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, provider.key, FabricItemGroup.builder()
                .title(provider.displayName)
                .icon(provider.displayIconProvider)
                .displayItems(provider.entryCollector)
                .build()
            )
        }
        ModCreativeTabs.inject { injector ->
            ItemGroupEvents.modifyEntriesEvent(injector.key).register { content ->
                injector.entryInjector(content.context).forEach(content::accept)
            }
        }
    }

    override fun registerBlocks() {
        ModBlocks.register { resourceLocation, block -> Registry.register(ModBlocks.registry, resourceLocation, block)  }
    }

    override fun registerEntityTypes() {
        ModEntities.register { resourceLocation, entityType -> Registry.register(ModEntities.registry, resourceLocation, entityType) }
    }

    override fun registerEntityAttributes() {
        ModEntities.registerAttributes { entityType, builder -> FabricDefaultAttributeRegistry.register(entityType, builder) }
    }

    override fun registerBlockEntityTypes() {
        ModBlockEntities.register { resourceLocation, blockEntityType -> Registry.register(ModBlockEntities.registry, resourceLocation, blockEntityType) }
    }

    override fun registerWorldGenFeatures() {
        ModFeatures.register { resourceLocation, feature -> Registry.register(ModFeatures.registry, resourceLocation, feature) }
    }

    override fun registerParticles() {
        ModParticles.register { resourceLocation, particleType -> Registry.register(ModParticles.registry, resourceLocation, particleType) }
    }

    override fun addFeatureToWorldGen(
        feature: ResourceKey<PlacedFeature>,
        step: GenerationStep.Decoration,
        validTag: TagKey<Biome>?
    ) {
        val predicate: (BiomeSelectionContext) -> Boolean = { context -> validTag == null || context.hasTag(validTag) }
        BiomeModifications.addFeature(predicate, step, feature)
    }

    override fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerCommandArgument(
        resourceLocation: ResourceLocation,
        argumentClass: KClass<A>,
        serializer: ArgumentTypeInfo<A, T>
    ) {
        ArgumentTypeRegistry.registerArgumentType(resourceLocation, argumentClass.java, serializer)
    }

    override fun <T : GameRules.Value<T>> registerGameRule(
        name: String,
        category: GameRules.Category,
        type: GameRules.Type<T>
    ): GameRules.Key<T> = GameRuleRegistry.register(name, category, type)

    override fun <T : CriterionTrigger<*>> registerCriteria(criteria: T): T = CriteriaTriggers.register(criteria)

    override fun registerResourceReloader(
        resourceLocation: ResourceLocation,
        reloader: PreparableReloadListener,
        type: PackType,
        dependencies: Collection<ResourceLocation>
    ) {
        ResourceManagerHelper.get(type).registerReloadListener(ModReloadListener(resourceLocation, reloader, dependencies))
    }

    override fun server(): MinecraftServer? = if (this.environment() == Environment.CLIENT) Minecraft.getInstance().singleplayerServer else this.server

    private class ModReloadListener(private val resourceLocation: ResourceLocation, private val reloader: PreparableReloadListener, private val dependencies: Collection<ResourceLocation>) : IdentifiableResourceReloadListener {

        override fun reload(
            synchronizer: PreparableReloadListener.PreparationBarrier,
            manager: ResourceManager,
            prepareProfiler: ProfilerFiller,
            applyProfiler: ProfilerFiller,
            prepareExecutor: Executor,
            applyExecutor: Executor
        ): CompletableFuture<Void> = this.reloader.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor)

        override fun getFabricId(): ResourceLocation = this.resourceLocation

        override fun getName(): String = this.reloader.name

        override fun getFabricDependencies(): MutableCollection<ResourceLocation> = this.dependencies.toMutableList()
    }
}
