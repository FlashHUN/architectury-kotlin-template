package flash.libapng

import flash.klibapng.chunks.fcTLChunk

data class FrameInfo(val width: UInt, val height: UInt, val xOffset: UInt, val yOffset: UInt, val disposeOp: UByte, val blendOp: UByte) {
    constructor(fcTLChunk: fcTLChunk) : this(fcTLChunk.width, fcTLChunk.height, fcTLChunk.xOffset, fcTLChunk.yOffset, fcTLChunk.disposeOp, fcTLChunk.blendOp)
}
