package flash.testmod.client.resources

import com.google.common.hash.Hashing
import flash.libapng.APNG
import flash.libapng.APNGSplitter
import flash.testmod.Environment
import flash.testmod.ModMain
import flash.testmod.api.animation.AnimatedTexture
import flash.testmod.api.animation.DelayAnimatedTexture
import flash.testmod.api.animation.ResourceLocationAnimatedTextureWrapper
import flash.testmod.api.client.resources.ByteTexture
import flash.testmod.net.messages.serverbound.ServerboundRequestTexturePacket
import flash.testmod.util.ifClient
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.resources.ResourceLocation
import org.apache.commons.io.FileUtils
import java.io.File

class AnimatedServerTextures(private val location: ResourceLocation, private val fallback: ResourceLocation) {

    companion object {
        private val CACHE_DIR : File? = if (ModMain.implementation.environment() == Environment.CLIENT) {
            val dir = File("assets/cache")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            try {
                FileUtils.cleanDirectory(dir)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
            dir
        } else null


        private val cache : HashMap<ResourceLocation, AnimatedServerTextures> = HashMap()
        private val textureCache : HashMap<ResourceLocation, AnimatedTexture> = HashMap()

        fun getOrCreate(location: ResourceLocation, fallback: ResourceLocation = MissingTextureAtlasSprite.getLocation()) : AnimatedServerTextures {
            return cache[location] ?: run {
                val texture = AnimatedServerTextures(location, fallback)
                cache[location] = texture
                texture
            }
        }

        operator fun get(location: ResourceLocation) : AnimatedServerTextures? = cache[location]
    }


    init {
        ifClient {
            tryRequest()
        }
    }

    fun location() : ResourceLocation {
        return if (isCached()) location else fallback
    }

    fun texture() : AnimatedTexture? = textureCache[location]

    private fun tryRequest() {
        if (!isCached()) {
            val minecraft = Minecraft.getInstance()
            if (minecraft.resourceManager.getResourceStack(location).size != 0) {
                minecraft.resourceManager.getResource(location).ifPresent {
                    register(it.open().readAllBytes())
                }
            } else if (!isRegistered()) {
                ServerboundRequestTexturePacket(location, true).sendToServer()
            }
        }
    }

    fun isCached() : Boolean = textureCache.containsKey(location)

    fun isRegistered() : Boolean = Minecraft.getInstance().resourceManager.getResourceStack(location).size != 0
            || Minecraft.getInstance().textureManager.getTexture(location, MissingTextureAtlasSprite.getTexture()) != MissingTextureAtlasSprite.getTexture()

    fun register(imgData: ByteArray) {
        try {
            val apng = APNG(imgData)
            if (apng.isSimplePng) {
                textureCache[location] = ResourceLocationAnimatedTextureWrapper(location)
            } else {
                val frames = APNGSplitter.splitWithOpsAppliedAndDelaysInMs(apng)
                val registeredFrames = arrayListOf<Pair<ResourceLocation, Int>>()
                for (i in frames.indices) {
                    val frame = frames[i]
                    val frameLocation = ResourceLocation("$location/$i")
                    val delay = frame.second
                    register(frame.first, frameLocation)
                    registeredFrames.add(frameLocation to delay)
                }
                register(apng.defaultImage.toBytes(), location)
                textureCache[location] = DelayAnimatedTexture(registeredFrames)
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to register animated texture")
        }
    }

    private fun register(imgBytes: ByteArray, location: ResourceLocation) {
        val s = Hashing.sha256().hashUnencodedChars(location.toString()).toString()
        val textureManager = Minecraft.getInstance().textureManager
        val dir = File(CACHE_DIR, if (s.length > 2) s.substring(0, 2) else "xx")
        val file = File(dir, s)
        val texture = ByteTexture(file, imgBytes, fallback)
        textureManager.register(location, texture)
    }
}