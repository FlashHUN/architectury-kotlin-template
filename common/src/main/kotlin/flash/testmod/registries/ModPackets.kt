package flash.testmod.registries

import flash.testmod.ModMain
import flash.testmod.NetworkManager
import flash.testmod.api.net.ClientNetworkPacketHandler
import flash.testmod.api.net.NetworkPacket
import flash.testmod.api.net.ServerNetworkPacketHandler
import flash.testmod.net.messages.clientbound.ClientboundSendAnimatedTexturePacket
import flash.testmod.net.messages.clientbound.ClientboundSendTexturePacket
import flash.testmod.net.messages.serverbound.ServerboundRequestTexturePacket
import flash.testmod.util.server
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import kotlin.reflect.KClass

object ModPackets : NetworkManager {

    fun ServerPlayer.sendTo(packet: NetworkPacket<*>) = sendPacketToPlayer(this, packet)
    fun sendToServer(packet: NetworkPacket<*>) = sendPacketToServer(packet)
    fun sendToAllPlayers(packet: NetworkPacket<*>) = sendPacketToPlayers(server()!!.playerList.players, packet)
    fun sendPacketToPlayers(players: Iterable<ServerPlayer>, packet: NetworkPacket<*>) = players.forEach { sendPacketToPlayer(it, packet) }

    override fun registerClientBound() {
        this.createClientBound(ClientboundSendTexturePacket.ID, ClientboundSendTexturePacket::decode, ClientboundSendTexturePacket.Handler)
        this.createClientBound(ClientboundSendAnimatedTexturePacket.ID, ClientboundSendAnimatedTexturePacket::decode, ClientboundSendAnimatedTexturePacket.Handler)
    }

    override fun registerServerBound() {
        this.createServerBound(ServerboundRequestTexturePacket.ID, ServerboundRequestTexturePacket::decode, ServerboundRequestTexturePacket.Handler)
    }

    private inline fun <reified T : NetworkPacket<T>> createClientBound(resourceLocation: ResourceLocation, noinline decoder: (FriendlyByteBuf) -> T, handler: ClientNetworkPacketHandler<T>) {
        ModMain.implementation.networkManager.createClientBound(resourceLocation, T::class, { message, buffer -> message.encode(buffer) }, decoder, handler)
    }

    private inline fun <reified T : NetworkPacket<T>> createServerBound(resourceLocation: ResourceLocation, noinline decoder: (FriendlyByteBuf) -> T, handler: ServerNetworkPacketHandler<T>) {
        ModMain.implementation.networkManager.createServerBound(resourceLocation, T::class, { message, buffer -> message.encode(buffer) }, decoder, handler)
    }

    override fun <T : NetworkPacket<T>> createClientBound(
        resourceLocation: ResourceLocation,
        kClass: KClass<T>,
        encoder: (T, FriendlyByteBuf) -> Unit,
        decoder: (FriendlyByteBuf) -> T,
        handler: ClientNetworkPacketHandler<T>
    ) {
        ModMain.implementation.networkManager.createClientBound(resourceLocation, kClass, encoder, decoder, handler)
    }

    override fun <T : NetworkPacket<T>> createServerBound(
        resourceLocation: ResourceLocation,
        kClass: KClass<T>,
        encoder: (T, FriendlyByteBuf) -> Unit,
        decoder: (FriendlyByteBuf) -> T,
        handler: ServerNetworkPacketHandler<T>
    ) {
        ModMain.implementation.networkManager.createServerBound(resourceLocation, kClass, encoder, decoder, handler)
    }

    override fun sendPacketToPlayer(player: ServerPlayer, packet: NetworkPacket<*>) = ModMain.implementation.networkManager.sendPacketToPlayer(player, packet)

    override fun sendPacketToServer(packet: NetworkPacket<*>) = ModMain.implementation.networkManager.sendPacketToServer(packet)

    override fun <T : NetworkPacket<*>> asVanillaClientBound(packet: T): Packet<ClientGamePacketListener> = ModMain.implementation.networkManager.asVanillaClientBound(packet)
}