package flash.testmod.net.messages.clientbound

import flash.testmod.ModMain
import flash.testmod.api.net.ClientNetworkPacketHandler
import flash.testmod.api.net.NetworkPacket
import flash.testmod.client.resources.AnimatedServerTextures
import flash.testmod.client.resources.ServerTextures
import flash.testmod.util.resource
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation

class ClientboundSendTexturePacket(val location: ResourceLocation, val imgData: ByteArray) : NetworkPacket<ClientboundSendTexturePacket> {
    override val id: ResourceLocation = ID

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeResourceLocation(location)
        buf.writeByteArray(imgData)
    }

    companion object {
        val ID = resource("send_texture")

        fun decode(buf: FriendlyByteBuf) = ClientboundSendTexturePacket(buf.readResourceLocation(), buf.readByteArray())
    }

    object Handler : ClientNetworkPacketHandler<ClientboundSendTexturePacket> {
        override fun handle(packet: ClientboundSendTexturePacket, client: Minecraft) {
            if (packet.location.toString().endsWith(".png")) {
                ServerTextures[packet.location]?.register(packet.imgData)
            }
        }
    }
}