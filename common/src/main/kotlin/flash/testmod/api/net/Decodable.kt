package flash.testmod.api.net

import net.minecraft.network.FriendlyByteBuf

interface Decodable {

    /**
     * Reads an updates this instance based on the given buffer.
     *
     * @param buffer The [FriendlyByteBuf] being read from.
     */
    fun decode(buffer: FriendlyByteBuf)

}