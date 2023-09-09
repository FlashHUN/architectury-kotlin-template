package flash.klibapng.chunks

import flash.klibapng.util.readByte
import flash.klibapng.util.readInt
import java.io.ByteArrayInputStream

class IHDRChunk : Chunk {
    internal constructor() : super()
    internal constructor(bytes: ByteArray) : super(bytes)
    internal constructor(stream: ByteArrayInputStream) : super(stream)
    internal constructor(chunk: Chunk) : super(chunk)

    var width: Int = 0
        private set
    var height: Int = 0
        private set
    var bitDepth: Byte = 0
        private set
    var colorType: Byte = 0
        private set
    var compressionMethod: Byte = 0
        private set
    var filterMethod: Byte = 0
        private set
    var interlaceMethod: Byte = 0
        private set

    override fun parseData(stream: ByteArrayInputStream) {
        width = stream.readInt()
        height = stream.readInt()
        bitDepth = stream.readByte()
        colorType = stream.readByte()
        compressionMethod = stream.readByte()
        filterMethod = stream.readByte()
        interlaceMethod = stream.readByte()
    }
}