package flash.testmod.api.client.resources

import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.platform.TextureUtil
import com.mojang.blaze3d.systems.RenderSystem
import flash.testmod.ModMain
import flash.testmod.ModMain.LOGGER
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.SimpleTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import org.apache.commons.io.FileUtils
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class ByteTexture(private val file: File?, private val imgBytes: ByteArray, location: ResourceLocation) : SimpleTexture(location) {
    private var uploaded: Boolean = false
    private var future: CompletableFuture<*>? = null

    private fun loadCallback(img: NativeImage) {

        Minecraft.getInstance().execute {
            this.uploaded = true
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall { this.upload(img) }
            } else {
                this.upload(img)
            }
        }
    }

    private fun upload(img: NativeImage) {
        TextureUtil.prepareImage(this.getId(), img.width, img.height)
        img.upload(0, 0, 0, true)
    }

    private fun load(inputStream: InputStream) : NativeImage? {
        var img: NativeImage? = null
        try {
            img = NativeImage.read(inputStream)
        } catch (e: Exception) {
            LOGGER.warn("Error while loading texture", e)
        }
        return img
    }

    override fun load(resourceManager: ResourceManager) {
        Minecraft.getInstance().execute {
            if (!this.uploaded) {
                try {
                    super.load(resourceManager)
                } catch (e: IOException) {
                    LOGGER.warn("Failed to load texture: {}", this.location, e)
                }
                this.uploaded = true
            }
        }
        if (this.future == null) {
            val img: NativeImage? = if (this.file?.isFile == true) {
                this.load(FileInputStream(this.file))
            } else null

            if (img != null) {
                loadCallback(img)
            } else {
                this.future = CompletableFuture.runAsync({
                    var inputStream: InputStream = ByteArrayInputStream(imgBytes)
                    file?.let {
                        try {
                            FileUtils.copyInputStreamToFile(inputStream, it)
                            inputStream = FileInputStream(it)
                        } catch (e: IOException) {
                            LOGGER.error(e)
                            throw RuntimeException(e)
                        }
                    }

                    Minecraft.getInstance().execute {
                        this.load(inputStream)?.let {
                            loadCallback(it)
                        }
                    }
                }, Util.backgroundExecutor())
            }
        }
    }
}