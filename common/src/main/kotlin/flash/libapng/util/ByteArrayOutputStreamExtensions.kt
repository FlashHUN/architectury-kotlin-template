package flash.klibapng.util

import java.io.ByteArrayOutputStream

fun ByteArrayOutputStream.writeByte(i: Byte) = writeBytes(BitConverter.getBytes(i))
fun ByteArrayOutputStream.writeShort(i: Short) = writeBytes(BitConverter.getBytes(i))
fun ByteArrayOutputStream.writeInt(i: Int) = writeBytes(BitConverter.getBytes(i))
fun ByteArrayOutputStream.writeLong(i: Long) = writeBytes(BitConverter.getBytes(i))
fun ByteArrayOutputStream.writeUByte(i: UByte) = writeBytes(BitConverter.getBytes(i))
fun ByteArrayOutputStream.writeUShort(i: UShort) = writeBytes(BitConverter.getBytes(i))
fun ByteArrayOutputStream.writeUInt(i: UInt) = writeBytes(BitConverter.getBytes(i))
fun ByteArrayOutputStream.writeULong(i: ULong) = writeBytes(BitConverter.getBytes(i))