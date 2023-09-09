package flash.testmod.api.animation

import net.minecraft.resources.ResourceLocation


class DelayAnimatedTexture(private val frames: List<Pair<ResourceLocation, Int>>) : DelayAnimation(frames.map { it.second }), AnimatedTexture {
    private val textures = frames.map { it.first }

    override fun location() = textures[getFrame()]

    override fun copyTexture(): AnimatedTexture {
        return DelayAnimatedTexture(frames)
    }
}