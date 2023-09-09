package flash.testmod.api.animation

import net.minecraft.resources.ResourceLocation

class FixedFramerateAnimatedTexture(frameTimeMs: Long, private val textures: List<ResourceLocation>) : FixedFramerateAnimation(frameTimeMs, textures.size), AnimatedTexture {
    override fun location() = textures[getFrame()]
    override fun copyTexture(): AnimatedTexture {
        return FixedFramerateAnimatedTexture(frameTimeMs, textures)
    }
}