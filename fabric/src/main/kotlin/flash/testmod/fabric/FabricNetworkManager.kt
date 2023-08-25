package flash.testmod.fabric

import flash.testmod.NetworkManager
import flash.testmod.api.net.ClientNetworkPacketHandler
import flash.testmod.api.net.NetworkPacket
import flash.testmod.api.net.ServerNetworkPacketHandler
import flash.testmod.registries.ModPackets
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import kotlin.reflect.KClass

object FabricNetworkManager : NetworkManager {

    override fun registerClientBound() {
        ModPackets.registerClientBound()
    }

    override fun registerServerBound() {
        ModPackets.registerServerBound()
    }

    override fun <T : NetworkPacket<T>> createClientBound(
        resourceLocation: ResourceLocation,
        kClass: KClass<T>,
        encoder: (T, FriendlyByteBuf) -> Unit,
        decoder: (FriendlyByteBuf) -> T,
        handler: ClientNetworkPacketHandler<T>
    ) {
        ClientPlayNetworking.registerGlobalReceiver(resourceLocation, createClientBoundHandler(decoder::invoke) { msg, _ ->
            handler.handleOnNettyThread(msg)
        })
    }

    override fun <T : NetworkPacket<T>> createServerBound(
        resourceLocation: ResourceLocation,
        kClass: KClass<T>,
        encoder: (T, FriendlyByteBuf) -> Unit,
        decoder: (FriendlyByteBuf) -> T,
        handler: ServerNetworkPacketHandler<T>
    ) {
        ServerPlayNetworking.registerGlobalReceiver(resourceLocation, createServerBoundHandler(decoder::invoke, handler::handleOnNettyThread))
    }

    private fun <T : NetworkPacket<*>> createServerBoundHandler(
        decoder: (FriendlyByteBuf) -> T,
        handler: (T, MinecraftServer, ServerPlayer) -> Unit
    ) = ServerPlayNetworking.PlayChannelHandler { server, player, _, buffer, _ ->
        handler(decoder(buffer), server, player)
    }

    private fun <T : NetworkPacket<*>> createClientBoundHandler(
        decoder: (FriendlyByteBuf) -> T,
        handler: (T, Minecraft) -> Unit
    ) = ClientPlayNetworking.PlayChannelHandler { client, _, buffer, _ ->
        handler(decoder(buffer), client)
    }

    override fun sendPacketToPlayer(player: ServerPlayer, packet: NetworkPacket<*>) {
        ServerPlayNetworking.send(player, packet.id, packet.toBuffer())
    }

    override fun sendPacketToServer(packet: NetworkPacket<*>) {
        ClientPlayNetworking.send(packet.id, packet.toBuffer())
    }

    override fun <T : NetworkPacket<*>> asVanillaClientBound(packet: T): Packet<ClientGamePacketListener> {
        return ServerPlayNetworking.createS2CPacket(packet.id, packet.toBuffer())
    }
}