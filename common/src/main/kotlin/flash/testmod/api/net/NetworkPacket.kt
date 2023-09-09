package flash.testmod.api.net

import flash.testmod.registries.ModPackets
import flash.testmod.util.server
import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level

/**
 * Platform abstract blueprint of a packet being sent out.
 * The handling of encoding, decoding and resolving the packet is done on the individual platform implementations.
 */
interface NetworkPacket<T: NetworkPacket<T>> : Encodable {

    /**
     *
     */
    val id: ResourceLocation

    /**
     * TODO
     *
     * @param player
     */
    fun sendToPlayer(player: ServerPlayer) = ModPackets.sendPacketToPlayer(player, this)

    /**
     * TODO
     *
     * @param players
     */
    fun sendToPlayers(players: Iterable<ServerPlayer>) {
        if (players.any()) {
            ModPackets.sendPacketToPlayers(players, this)
        }
    }

    /**
     * TODO
     *
     */
    fun sendToAllPlayers() = ModPackets.sendToAllPlayers(this)

    /**
     * TODO
     *
     */
    fun sendToServer() = ModPackets.sendPacketToServer(this)

    // A copy from PlayerManager#sendToAround to work with our packets
    /**
     * TODO
     *
     * @param x
     * @param y
     * @param z
     * @param distance
     * @param worldKey
     * @param exclusionCondition
     */
    fun sendToPlayersAround(x: Double, y: Double, z: Double, distance: Double, worldKey: ResourceKey<Level>, exclusionCondition: (ServerPlayer) -> Boolean = { false }) {
        val server = server() ?: return
        server.playerList.players.filter { player ->
            if (exclusionCondition.invoke(player))
                return@filter false
            val xDiff = x - player.x
            val yDiff = y - player.y
            val zDiff = z - player.z
            return@filter (xDiff * xDiff + yDiff * yDiff + zDiff) < distance * distance
        }
            .forEach { player -> ModPackets.sendPacketToPlayer(player, this) }
    }

    /**
     * TODO
     *
     * @return
     */
    fun toBuffer(): FriendlyByteBuf {
        val buffer = FriendlyByteBuf(Unpooled.buffer())
        this.encode(buffer)
        return buffer
    }

}