package flash.klibapng.chunks

import flash.klibapng.util.CrcHelper
import flash.klibapng.util.readUInt
import flash.klibapng.util.writeInt
import flash.klibapng.util.writeUInt
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class fdATChunk : Chunk {
    internal constructor() : super()
    internal constructor(bytes: ByteArray) : super(bytes)
    internal constructor(stream: ByteArrayInputStream) : super(stream)
    internal constructor(chunk: Chunk) : super(chunk)

    var sequenceNumber: UInt = 0u
        private set
    var frameData: ByteArray? = null
        private set

    override fun parseData(stream: ByteArrayInputStream) {
        sequenceNumber = stream.readUInt()
        frameData = stream.readNBytes(length - 4)
    }

    fun toIDATChunk() : IDATChunk {
        val crcStream = ByteArrayOutputStream()
        crcStream.writeBytes("IDAT".toByteArray(Charsets.US_ASCII))
        crcStream.writeBytes(frameData)
        val newCrc = CrcHelper.calculate(crcStream.toByteArray())

        val stream = ByteArrayOutputStream()
        stream.writeInt(length - 4)
        stream.writeBytes("IDAT".toByteArray(Charsets.US_ASCII))
        stream.writeBytes(frameData)
        stream.writeUInt(newCrc)

        return IDATChunk(stream.toByteArray())
    }
}