package flash.klibapng.chunks

import flash.klibapng.util.*
import flash.klibapng.util.BitConverter
import flash.klibapng.util.CrcHelper
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

open class Chunk internal constructor() {
    var length: Int = 0
    var chunkType: String = ""
    var chunkData: ByteArray? = null
    var crc: UInt = 0u

    internal constructor(bytes: ByteArray) : this(ByteArrayInputStream(bytes))
    internal constructor(stream: ByteArrayInputStream) : this() {
        length = stream.readInt()
        chunkType = stream.readNBytes(4).decodeToString()
        chunkData = stream.readNBytes(length)
        crc = stream.readUInt()
        if (length != chunkData!!.size) {
            throw Exception("Chunk data length is incorrect")
        }

        parseData(ByteArrayInputStream(chunkData))
    }

    internal constructor(chunk: Chunk) : this() {
        length = chunk.length
        chunkType = chunk.chunkType
        chunkData = chunk.chunkData
        crc = chunk.crc

        parseData(ByteArrayInputStream(chunkData))
    }

    val raw get() = run {
        val stream = ByteArrayOutputStream()
        stream.writeInt(length)
        stream.writeBytes(chunkType.toByteArray(Charsets.US_ASCII))
        stream.writeBytes(chunkData)
        stream.writeUInt(crc)
        stream.toByteArray()
    }

    fun modifyChunkData(position: Int, newData: ByteArray) {
        System.arraycopy(newData, 0, chunkData!!, position, newData.size)

        val crcStream = ByteArrayOutputStream()
        crcStream.writeBytes(chunkType.toByteArray(Charsets.US_ASCII))
        crcStream.writeBytes(chunkData)

        crc = CrcHelper.calculate(crcStream.toByteArray())
    }

    fun modifyChunkData(position: Int, newData: UInt) {
        modifyChunkData(position, BitConverter.getBytes(newData))
    }

    protected open fun parseData(stream: ByteArrayInputStream) {}
}