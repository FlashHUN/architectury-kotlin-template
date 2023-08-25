package flash.testmod

import com.mojang.brigadier.arguments.ArgumentType
import flash.testmod.api.net.ClientNetworkPacketHandler
import flash.testmod.api.net.NetworkPacket
import flash.testmod.api.net.ServerNetworkPacketHandler
import net.minecraft.advancements.CriterionTrigger
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.tags.TagKey
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import kotlin.reflect.KClass

interface PlatformImplementation {
    val platform: Platform

    val networkManager: NetworkManager

    fun environment(): Environment

    fun isModInstalled(id: String): Boolean

    fun registerPermissionValidator()
    fun registerSoundEvents()
    fun registerItems()
    fun registerBlocks()
    fun registerEntityTypes()
    fun registerEntityAttributes()
    fun registerBlockEntityTypes()
    fun registerWorldGenFeatures()
    fun registerParticles()
    fun addFeatureToWorldGen(feature: ResourceKey<PlacedFeature>, step: GenerationStep.Decoration, validTag: TagKey<Biome>?)
    fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerCommandArgument(resourceLocation: ResourceLocation, argumentClass: KClass<A>, serializer: ArgumentTypeInfo<A, T>)
    fun <T : GameRules.Value<T>> registerGameRule(name: String, category: GameRules.Category, type: GameRules.Type<T>): GameRules.Key<T>
    fun <T : CriterionTrigger<*>> registerCriteria(criteria: T): T
    fun registerResourceReloader(resourceLocation: ResourceLocation, reloader: PreparableReloadListener, type: PackType, dependencies: Collection<ResourceLocation>)

    fun server(): MinecraftServer?
}

interface NetworkManager {
    fun registerClientBound()
    fun registerServerBound()

    fun <T: NetworkPacket<T>> createClientBound(resourceLocation: ResourceLocation, kClass: KClass<T>, encoder: (T, FriendlyByteBuf) -> Unit, decoder: (FriendlyByteBuf) -> T, handler: ClientNetworkPacketHandler<T>)
    fun <T: NetworkPacket<T>> createServerBound(resourceLocation: ResourceLocation, kClass: KClass<T>, encoder: (T, FriendlyByteBuf) -> Unit, decoder: (FriendlyByteBuf) -> T, handler: ServerNetworkPacketHandler<T>)

    fun sendPacketToPlayer(player: ServerPlayer, packet: NetworkPacket<*>)
    fun sendPacketToServer(packet: NetworkPacket<*>)

    fun <T : NetworkPacket<*>> asVanillaClientBound(packet: T): Packet<ClientGamePacketListener>
}

enum class Platform {
    FABRIC,
    QUILT,
    FORGE
}

enum class Environment {
    CLIENT,
    SERVER
}
