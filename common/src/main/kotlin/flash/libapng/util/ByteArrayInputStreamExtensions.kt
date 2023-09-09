package flash.klibapng.util

import java.io.ByteArrayInputStream

fun ByteArrayInputStream.readByte() = readNBytes(1)[0]
fun ByteArrayInputStream.readShort() = BitConverter.toShort(readNBytes(2), 0)
fun ByteArrayInputStream.readInt() = BitConverter.toInt(readNBytes(4), 0)
fun ByteArrayInputStream.readLong() = BitConverter.toLong(readNBytes(8), 0)
fun ByteArrayInputStream.readUByte() = readNBytes(1)[0].toUByte()
fun ByteArrayInputStream.readUShort() = BitConverter.toUShort(readNBytes(2), 0)
fun ByteArrayInputStream.readUInt() = BitConverter.toUInt(readNBytes(4), 0)
fun ByteArrayInputStream.readULong() = BitConverter.toULong(readNBytes(8), 0)