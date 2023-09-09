package flash.klibapng.chunks

import flash.klibapng.util.*
import java.io.ByteArrayInputStream

class fcTLChunk : Chunk {
    internal constructor() : super()
    internal constructor(bytes: ByteArray) : super(bytes)
    internal constructor(stream: ByteArrayInputStream) : super(stream)
    internal constructor(chunk: Chunk) : super(chunk)

    var sequenceNumber: UInt = 0u
        private set
    var width: UInt = 0u
        private set
    var height: UInt = 0u
        private set
    var xOffset: UInt = 0u
        private set
    var yOffset: UInt = 0u
        private set
    var delayNum: UShort = 0u
        private set
    var delayDen: UShort = 0u
        private set
    var disposeOp: UByte = 0u
        private set
    var blendOp: UByte = 0u
        private set

    override fun parseData(stream: ByteArrayInputStream) {
        sequenceNumber = stream.readUInt()
        width = stream.readUInt()
        height = stream.readUInt()
        xOffset = stream.readUInt()
        yOffset = stream.readUInt()
        delayNum = stream.readUShort()
        delayDen = stream.readUShort()
        disposeOp = stream.readUByte()
        blendOp = stream.readUByte()
    }

    override fun toString(): String {
        return "fcTLChunk{" +
                "sequenceNumber = $sequenceNumber, " +
                "width = $width, " +
                "height = $height, " +
                "width = $width, " +
                "xOffset = $xOffset, " +
                "yOffset = $yOffset, " +
                "delayNum = $delayNum, " +
                "delayDen = $delayDen, " +
                "disposeOp = ${disposeOp}, " +
                "blendOp = ${blendOp} " +
                "}"
    }
}