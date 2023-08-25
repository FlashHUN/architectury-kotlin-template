package flash.testmod.api.net

import net.minecraft.network.FriendlyByteBuf

/**
 * Represents an object that can be encoded to a [FriendlyByteBuf].
 */
interface Encodable {

    /**
     * Writes this instance to the given buffer.
     *
     * @param buffer The [FriendlyByteBuf] being written to.
     */
    fun encode(buffer: FriendlyByteBuf)

}