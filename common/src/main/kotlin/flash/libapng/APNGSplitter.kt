package flash.libapng

import flash.klibapng.ops.BlendOps
import flash.klibapng.ops.DisposeOps
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object APNGSplitter {

    private val TRANSPARENT_BLACK = Color(0, 0, 0, 0)

    fun split(apng: APNG, addDefaultImage: Boolean = false) : List<ByteArray> {
        val out = arrayListOf<ByteArray>()
        if (!apng.defaultImageIsAnimated && addDefaultImage) {
            out += apng.defaultImage.toBytes()
        }
        out.addAll(apng.frames().map { f -> f.toBytes() })
        return out
    }

    fun splitWithDelaysInMs(apng: APNG) : List<Pair<ByteArray, Int>> = split(apng).zip(apng.frames().map { f ->
        val fcTL = f.fcTLChunk!!
        (fcTL.delayNum.toDouble() / fcTL.delayDen.toDouble() * 1000).toInt()
    })

    fun splitWithFrameInfo(apng: APNG, addDefaultImage: Boolean = false) : List<Pair<ByteArray, FrameInfo>> {
        val out = arrayListOf<Pair<ByteArray, FrameInfo>>()
        if (!apng.defaultImageIsAnimated && addDefaultImage) {
            out += apng.defaultImage.toBytes() to FrameInfo(apng.defaultImage.fcTLChunk!!)
        }
        out.addAll(apng.frames().map { f -> f.toBytes() to FrameInfo(f.fcTLChunk!!) })
        return out
    }

    fun splitWithFrameInfoAndDelayMs(apng: APNG, addDefaultImage: Boolean = false) : List<Triple<ByteArray, Int, FrameInfo>> {
        val out = arrayListOf<Triple<ByteArray, Int, FrameInfo>>()
        if (!apng.defaultImageIsAnimated && addDefaultImage) {
            out += Triple(
                apng.defaultImage.toBytes(),
                (apng.defaultImage.fcTLChunk!!.delayNum.toDouble() / apng.defaultImage.fcTLChunk!!.delayDen.toDouble() * 1000).toInt(),
                FrameInfo(apng.defaultImage.fcTLChunk!!)
            )
        }
        out.addAll(apng.frames().map { f -> Triple(
            f.toBytes(),
            (f.fcTLChunk!!.delayNum.toDouble() / f.fcTLChunk!!.delayDen.toDouble() * 1000).toInt(),
            FrameInfo(f.fcTLChunk!!)
        ) })
        return out
    }

    fun splitWithOpsApplied(apng: APNG, addDefaultImage: Boolean = false) : List<ByteArray> {
        val out = arrayListOf<ByteArray>()
        if (!apng.defaultImageIsAnimated && addDefaultImage) {
            out += apng.defaultImage.toBytes()
        }

        val width = apng.ihdrChunk!!.width
        val height = apng.ihdrChunk!!.height

        var activeFrame: Frame? = null
        var lastFrameImage: BufferedImage? = null
        var activeFrameImage: BufferedImage? = null

        val frames = apng.frames()
        for (i in frames.indices) {
            val renderedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            lastFrameImage?.let { mergeContents(it, renderedImage) }

            activeFrame = frames[i]
            activeFrameImage = ImageIO.read(ByteArrayInputStream(activeFrame.toBytes()))

            val region = Rectangle(
                activeFrame.fcTLChunk!!.xOffset.toInt(),
                activeFrame.fcTLChunk!!.yOffset.toInt(),
                activeFrame.fcTLChunk!!.width.toInt(),
                activeFrame.fcTLChunk!!.height.toInt()
            )
            applyBlendOp(activeFrameImage, renderedImage, BlendOps.values()[activeFrame.fcTLChunk!!.blendOp.toInt()], region)

            val baos = ByteArrayOutputStream()
            ImageIO.write(renderedImage, "png", baos)
            out += baos.toByteArray()

            applyDisposeOp(lastFrameImage, renderedImage, DisposeOps.values()[activeFrame.fcTLChunk!!.disposeOp.toInt()], region)

            lastFrameImage = renderedImage
        }
        return out
    }

    fun splitWithOpsAppliedAndDelaysInMs(apng: APNG) : List<Pair<ByteArray, Int>> = splitWithOpsApplied(apng).zip(apng.frames().map { f ->
        val fcTL = f.fcTLChunk!!
        (fcTL.delayNum.toDouble() / fcTL.delayDen.toDouble() * 1000).toInt()
    })

    private fun applyBlendOp(currentFrame: BufferedImage, dest: BufferedImage, blendOp: BlendOps, region: Rectangle) {
        when (blendOp) {
            BlendOps.SOURCE -> {
                fill(dest, region)
                mergeContentsOfRegion(currentFrame, dest, region)
            }
            BlendOps.OVER -> mergeContentsOfRegion(currentFrame, dest, region)
        }
    }

    private fun applyDisposeOp(lastFrameImage: BufferedImage?, renderedImage: BufferedImage, disposeOp: DisposeOps, region: Rectangle) {
        when (disposeOp) {
            DisposeOps.NONE -> {}
            DisposeOps.BACKGROUND -> {
                fill(renderedImage, region)
            }
            DisposeOps.PREVIOUS -> {
                fill(renderedImage, region)
                lastFrameImage?.let { mergeContentsOfRegion(it, renderedImage, region, true) }
            }
        }
    }

    private fun fill(img: BufferedImage, color: Color = TRANSPARENT_BLACK) {
        fill(img, 0, 0, img.width, img.height, color)
    }

    private fun fill(img: BufferedImage, region: Rectangle, color: Color = TRANSPARENT_BLACK) {
        fill(img, region.x, region.y, region.width, region.height, color)
    }

    private fun fill(img: BufferedImage, x: Int, y: Int, width: Int, height: Int, color: Color = TRANSPARENT_BLACK) {
        /*val colorPixels = intArrayOf(color.alpha, color.red, color.green, color.blue)
        val numPixels = width * height
        val pixels = IntArray(4 * numPixels)
        for (i in 0..<numPixels) {
            System.arraycopy(colorPixels, 0, pixels, i * 4, colorPixels.size)
        }
        img.raster.setPixels(x, y, width, height, pixels)*/
        val g = img.createGraphics()
        g.color = color
        g.composite = AlphaComposite.getInstance(AlphaComposite.CLEAR)
        g.fillRect(x, y, width, height)
        g.dispose()
    }

    private fun mergeContentsOfRegion(top: BufferedImage, bottom: BufferedImage, region: Rectangle, subimage: Boolean = false) {
        mergeContentsOfRegion(top, bottom, region.x, region.y, region.width, region.height, subimage)
    }

    private fun mergeContentsOfRegion(top: BufferedImage, bottom: BufferedImage, x: Int, y: Int, width: Int, height: Int, subimage: Boolean = false) {
        val g = bottom.createGraphics()
        g.drawImage(if (subimage) top.getSubimage(x, y, width, height) else top, x, y, width, height, null)
        g.dispose()
    }

    private fun mergeContents(top: BufferedImage, bottom: BufferedImage) {
        mergeContentsOfRegion(top, bottom, 0, 0, top.width, top.height)
    }

    private data class Rectangle(val x: Int, val y: Int, val width: Int, val height: Int)

}