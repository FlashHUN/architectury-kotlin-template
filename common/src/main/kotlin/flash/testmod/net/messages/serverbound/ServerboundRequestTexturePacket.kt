package flash.testmod.net.messages.serverbound

import flash.testmod.api.net.NetworkPacket
import flash.testmod.api.net.ServerNetworkPacketHandler
import flash.testmod.net.messages.clientbound.ClientboundSendTexturePacket
import flash.testmod.net.messages.clientbound.ClientboundSendAnimatedTexturePacket
import flash.testmod.util.resource
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

class ServerboundRequestTexturePacket(val location: ResourceLocation, val animated: Boolean = false) : NetworkPacket<ServerboundRequestTexturePacket> {
    override val id: ResourceLocation = ID

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeResourceLocation(location)
        buf.writeBoolean(animated)
    }

    companion object {
        val ID = resource("request_texture")

        fun decode(buf: FriendlyByteBuf) = ServerboundRequestTexturePacket(buf.readResourceLocation(), buf.readBoolean())
    }

    object Handler : ServerNetworkPacketHandler<ServerboundRequestTexturePacket> {
        override fun handle(packet: ServerboundRequestTexturePacket, server: MinecraftServer, player: ServerPlayer) {
            server.resourceManager.getResource(packet.location).ifPresent {
                val stream = it.open()
                val bytes = stream.readAllBytes()
                if (packet.animated) {
                    ClientboundSendAnimatedTexturePacket(packet.location, bytes).sendToPlayer(player)
                } else {
                    ClientboundSendTexturePacket(packet.location, bytes).sendToPlayer(player)
                }
                stream.close()
            }
        }
    }
}