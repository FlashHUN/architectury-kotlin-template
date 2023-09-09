package flash.klibapng.util

import java.nio.ByteBuffer

internal object BitConverter {
    fun getBytes(i: Byte): ByteArray {
        return ByteBuffer.allocate(1).put(i).array()
    }
    fun getBytes(i: Short): ByteArray {
        return ByteBuffer.allocate(2).putShort(i).array()
    }
    fun getBytes(i: Int): ByteArray {
        return ByteBuffer.allocate(4).putInt(i).array()
    }
    fun getBytes(i: Long): ByteArray {
        return ByteBuffer.allocate(8).putLong(i).array()
    }
    fun getBytes(i: UByte): ByteArray {
        return ByteBuffer.allocate(1).put(i.toByte()).array()
    }
    fun getBytes(i: UShort): ByteArray {
        return ByteBuffer.allocate(2).putShort(i.toShort()).array()
    }
    fun getBytes(i: UInt): ByteArray {
        return ByteBuffer.allocate(4).putInt(i.toInt()).array()
    }
    fun getBytes(i: ULong): ByteArray {
        return ByteBuffer.allocate(8).putLong(i.toLong()).array()
    }
    fun toShort(bytes: ByteArray, offset: Int): Short {
        return ByteBuffer.wrap(bytes).getShort(offset)
    }
    fun toInt(bytes: ByteArray, offset: Int): Int {
        return ByteBuffer.wrap(bytes).getInt(offset)
    }
    fun toLong(bytes: ByteArray, offset: Int): Long {
        return ByteBuffer.wrap(bytes).getLong(offset)
    }
    fun toUShort(bytes: ByteArray, offset: Int): UShort {
        return ByteBuffer.wrap(bytes).getShort(offset).toUShort()
    }
    fun toUInt(bytes: ByteArray, offset: Int): UInt {
        return ByteBuffer.wrap(bytes).getInt(offset).toUInt()
    }
    fun toULong(bytes: ByteArray, offset: Int): ULong {
        return ByteBuffer.wrap(bytes).getLong(offset).toULong()
    }
}