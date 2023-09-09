package flash.libapng

import flash.klibapng.chunks.*
import flash.klibapng.util.Helper
import java.io.ByteArrayInputStream
import java.nio.file.Files
import kotlin.io.path.Path

class APNG {
    val defaultImage = Frame()
    private val frames = arrayListOf<Frame>()
    var ihdrChunk: IHDRChunk? = null
        private set
    var acTLChunk: acTLChunk? = null
        private set
    var isSimplePng: Boolean = false
        private set
    var defaultImageIsAnimated: Boolean = false
        private set

    constructor(fileName: String) : this(Files.readAllBytes(Path(fileName)))
    constructor(bytes: ByteArray) {
        val stream = ByteArrayInputStream(bytes)

        if (!Helper.isBytesEqual(stream.readNBytes(Frame.SIGNATURE.size), Frame.SIGNATURE))
            throw Exception("File signature is incorrect")

        ihdrChunk = IHDRChunk(stream)
        if (ihdrChunk?.chunkType != "IHDR")
            throw Exception("First chunk must be of type IHDR")

        var chunk: Chunk
        var frame: Frame? = null
        var otherChunks = arrayListOf<OtherChunk>()
        var parsedIDAT = false
        do {
            chunk = Chunk(stream)
            when (chunk.chunkType) {
                "IHDR" -> throw Exception("Only one chunk of type IHDR is allowed")
                "acTL" -> {
                    if (isSimplePng)
                        throw Exception("acTL chunk must be located before the IDAT and fdAT chunks")
                    acTLChunk = acTLChunk(chunk)
                }
                "IDAT" -> {
                    if (acTLChunk == null)
                        isSimplePng = true // in APNGs the acTL chunk must be located before IDATs and fdATs

                    // only the default image has an IDAT chunk
                    defaultImage.ihdrChunk = ihdrChunk
                    defaultImage.idatChunks += IDATChunk(chunk)
                    parsedIDAT = true
                }
                "fcTL" -> {
                    if (isSimplePng) // simple PNGs should ignore this
                        continue

                    if (frame?.idatChunks?.size == 0)
                        throw Exception("Each frame can only have one fcTL chunk")

                    if (parsedIDAT) { // parsed IDAT means this fcTL is used by a frame
                        frame?.let { frames.add(it) }

                        frame = Frame()
                        frame.ihdrChunk = ihdrChunk
                        frame.fcTLChunk = fcTLChunk(chunk)
                    } else { // otherwise this is the default image's fcTL
                        defaultImage.fcTLChunk = fcTLChunk(chunk)
                    }
                }
                "fdAT" -> {
                    if (isSimplePng) // simple PNGs should ignore this
                        continue

                    if (frame?.fcTLChunk == null) {
                        throw Exception("fcTL chunk not found")
                    }

                    frame.idatChunks.add(fdATChunk(chunk).toIDATChunk())
                }
                "IEND" -> {
                    frame?.let { frames.add(it) }

                    val iend = IENDChunk(chunk)
                    if (defaultImage.idatChunks.size != 0)
                        defaultImage.iendChunk = iend
                    for (f in frames)
                        f.iendChunk = iend
                }
                else -> otherChunks.add(OtherChunk(chunk))
            }
        } while (chunk.chunkType != "IEND")

        defaultImage.fcTLChunk?.let {
            frames.add(0, defaultImage)
            defaultImageIsAnimated = true
        }

        frames.forEach { f -> otherChunks.forEach(f.otherChunks::add) }
    }

    fun frames() : Array<Frame> {
        return frames.toTypedArray()
    }
}