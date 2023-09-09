package flash.klibapng.chunks

import flash.klibapng.util.readInt
import java.io.ByteArrayInputStream

class acTLChunk : Chunk {
    internal constructor() : super()
    internal constructor(bytes: ByteArray) : super(bytes)
    internal constructor(stream: ByteArrayInputStream) : super(stream)
    internal constructor(chunk: Chunk) : super(chunk)

    var numFrames: Int = 0
        private set
    var numPlays: Int = 0
        private set

    override fun parseData(stream: ByteArrayInputStream) {
        numFrames = stream.readInt()
        numPlays = stream.readInt()
    }
}