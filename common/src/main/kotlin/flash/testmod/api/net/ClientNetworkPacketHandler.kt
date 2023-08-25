package flash.testmod.api.net

import net.minecraft.client.Minecraft

interface ClientNetworkPacketHandler<T: NetworkPacket<T>> {

    fun handle(packet: T, client: Minecraft)

    fun handleOnNettyThread(packet: T) {
        val client = Minecraft.getInstance()
        client.execute { handle(packet, client) }
    }
}