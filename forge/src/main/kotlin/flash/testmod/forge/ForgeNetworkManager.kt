package flash.testmod.forge

import flash.testmod.NetworkManager
import flash.testmod.api.net.ClientNetworkPacketHandler
import flash.testmod.api.net.NetworkPacket
import flash.testmod.api.net.ServerNetworkPacketHandler
import flash.testmod.registries.ModPackets
import flash.testmod.util.resource
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import kotlin.reflect.KClass

object ForgeNetworkManager : NetworkManager {

    private const val PROTOCOL_VERSION = "1"
    private var id = 0

    private val channel = NetworkRegistry.newSimpleChannel(
        resource("main"),
        { PROTOCOL_VERSION },
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    )

    override fun registerClientBound() {
        ModPackets.registerClientBound()
    }

    override fun registerServerBound() {
        ModPackets.registerServerBound()
    }

    @Suppress("INACCESSIBLE_TYPE")
    override fun <T : NetworkPacket<T>> createClientBound(
        resourceLocation: ResourceLocation,
        kClass: KClass<T>,
        encoder: (T, FriendlyByteBuf) -> Unit,
        decoder: (FriendlyByteBuf) -> T,
        handler: ClientNetworkPacketHandler<T>
    ) {
        this.channel.registerMessage(this.id++, kClass.java, encoder::invoke, decoder::invoke) { msg, ctx ->
            val context = ctx.get()
            handler.handleOnNettyThread(msg)
            context.packetHandled = true
        }
    }

    @Suppress("INACCESSIBLE_TYPE")
    override fun <T : NetworkPacket<T>> createServerBound(
        resourceLocation: ResourceLocation,
        kClass: KClass<T>,
        encoder: (T, FriendlyByteBuf) -> Unit,
        decoder: (FriendlyByteBuf) -> T,
        handler: ServerNetworkPacketHandler<T>
    ) {
        this.channel.registerMessage(this.id++, kClass.java, encoder::invoke, decoder::invoke) { msg, ctx ->
            val context = ctx.get()
            handler.handleOnNettyThread(msg, context.sender!!.server, context.sender!!)
            context.packetHandled = true
        }
    }

    override fun sendPacketToPlayer(player: ServerPlayer, packet: NetworkPacket<*>) {
        this.channel.send(PacketDistributor.PLAYER.with { player }, packet)
    }

    override fun sendPacketToServer(packet: NetworkPacket<*>) {
        this.channel.sendToServer(packet)
    }

    override fun <T : NetworkPacket<*>> asVanillaClientBound(packet: T): Packet<ClientGamePacketListener> {
        return this.channel.toVanillaPacket(packet, NetworkDirection.PLAY_TO_CLIENT) as Packet<ClientGamePacketListener>
    }
}