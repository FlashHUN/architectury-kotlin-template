package flash.klibapng.util

internal object Helper {
    fun isBytesEqual(bytes1: ByteArray, bytes2: ByteArray): Boolean {
        if (bytes1.size != bytes2.size) return false
        for (i in bytes1.indices) {
            if (bytes1[i] != bytes2[i]) return false
        }
        return true
    }
}