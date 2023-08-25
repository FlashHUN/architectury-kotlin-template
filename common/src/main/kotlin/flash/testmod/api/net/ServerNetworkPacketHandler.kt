package flash.testmod.api.net

import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

interface ServerNetworkPacketHandler<T: NetworkPacket<T>> {

    fun handle(packet: T, server: MinecraftServer, player: ServerPlayer)

    fun handleOnNettyThread(packet: T, server: MinecraftServer, player: ServerPlayer) {
        server.execute { handle(packet, server, player) }
    }
}