package flash.libapng

import flash.klibapng.chunks.*
import java.io.ByteArrayOutputStream

class Frame {
    companion object {
        val SIGNATURE: ByteArray = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
    }

    var ihdrChunk: IHDRChunk? = null
    var fcTLChunk: fcTLChunk? = null
    var iendChunk: IENDChunk? = null
    var idatChunks = arrayListOf<IDATChunk>()
    var otherChunks = arrayListOf<OtherChunk>()

    fun toBytes() : ByteArray {
        val ihdrChunk = IHDRChunk(ihdrChunk!!)
        if (fcTLChunk != null) {
            ihdrChunk.modifyChunkData(0, fcTLChunk!!.width)
            ihdrChunk.modifyChunkData(4, fcTLChunk!!.height)
        }

        val stream = ByteArrayOutputStream()
        stream.writeBytes(SIGNATURE)
        stream.writeBytes(ihdrChunk.raw)
        otherChunks.forEach { o -> stream.writeBytes(o.raw) }
        idatChunks.forEach { i -> stream.writeBytes(i.raw) }
        stream.writeBytes(iendChunk!!.raw)

        return stream.toByteArray()
    }
}