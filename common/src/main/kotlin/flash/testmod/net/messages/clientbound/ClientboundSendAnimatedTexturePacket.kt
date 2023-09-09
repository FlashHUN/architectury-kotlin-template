package flash.testmod.net.messages.clientbound

import flash.testmod.ModMain
import flash.testmod.api.net.ClientNetworkPacketHandler
import flash.testmod.api.net.NetworkPacket
import flash.testmod.client.resources.AnimatedServerTextures
import flash.testmod.util.resource
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation

class ClientboundSendAnimatedTexturePacket(val location: ResourceLocation, val imgData: ByteArray) : NetworkPacket<ClientboundSendAnimatedTexturePacket> {
    override val id: ResourceLocation = ID

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeResourceLocation(location)
        buf.writeByteArray(imgData)
    }

    companion object {
        val ID = resource("send_animated_texture")

        fun decode(buf: FriendlyByteBuf) : ClientboundSendAnimatedTexturePacket {
            return ClientboundSendAnimatedTexturePacket(buf.readResourceLocation(), buf.readByteArray())
        }
    }

    object Handler : ClientNetworkPacketHandler<ClientboundSendAnimatedTexturePacket> {
        override fun handle(packet: ClientboundSendAnimatedTexturePacket, client: Minecraft) {
            AnimatedServerTextures[packet.location]?.register(packet.imgData)
        }
    }
}