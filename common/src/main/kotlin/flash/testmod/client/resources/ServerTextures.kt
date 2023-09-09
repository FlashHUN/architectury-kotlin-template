package flash.testmod.client.resources

import com.google.common.hash.Hashing
import flash.testmod.Environment
import flash.testmod.ModMain
import flash.testmod.api.client.resources.ByteTexture
import flash.testmod.net.messages.serverbound.ServerboundRequestTexturePacket
import flash.testmod.util.ifClient
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.resources.ResourceLocation
import org.apache.commons.io.FileUtils
import java.io.File

class ServerTextures private constructor(private val location: ResourceLocation, private val fallback: ResourceLocation) {

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


        private val cache : HashMap<ResourceLocation, ServerTextures> = HashMap()

        fun getOrCreate(location: ResourceLocation, fallback: ResourceLocation = MissingTextureAtlasSprite.getLocation()) : ServerTextures {
            return cache[location] ?: run {
                val texture = ServerTextures(location, fallback)
                cache[location] = texture
                texture
            }
        }

        operator fun get(location: ResourceLocation) : ServerTextures? = cache[location]
    }

    init {
        ifClient {
            tryRequest()
        }
    }

    fun location() : ResourceLocation {
        return if (isRegistered()) location else fallback
    }

    private fun tryRequest() {
        if (!isRegistered()) {
            ServerboundRequestTexturePacket(location).sendToServer()
        }
    }

    fun isRegistered() : Boolean = Minecraft.getInstance().resourceManager.getResourceStack(location).size != 0
            || Minecraft.getInstance().textureManager.getTexture(location, MissingTextureAtlasSprite.getTexture()) != MissingTextureAtlasSprite.getTexture()


    fun register(imgBytes: ByteArray) {
        val s = Hashing.sha256().hashUnencodedChars(location.toString()).toString()
        val textureManager = Minecraft.getInstance().textureManager
        val dir = File(CACHE_DIR, if (s.length > 2) s.substring(0, 2) else "xx")
        val file = File(dir, s)
        val texture = ByteTexture(file, imgBytes, fallback)
        textureManager.register(location, texture)
    }
}