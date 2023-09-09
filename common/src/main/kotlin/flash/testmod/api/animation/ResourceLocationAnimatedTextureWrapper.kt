package flash.testmod.api.animation

import net.minecraft.resources.ResourceLocation

class ResourceLocationAnimatedTextureWrapper(private val location: ResourceLocation) : AnimatedTexture {
    override fun location(): ResourceLocation = location

    override fun getFrame(): Int = 0

    override fun isReverse(): Boolean = false

    override fun reverse() {
    }

    override fun start(force: Boolean) {
    }

    override fun restart() {
    }

    override fun stop() {
    }

    override fun reset() {
    }

    override fun isRunning(): Boolean = true

    override fun tick() {
    }

    override fun loop() {
    }

    override fun loopWithReverse() {
    }

    override fun copy(): Animation {
        return this
    }

    override fun copyTexture(): AnimatedTexture {
        return this
    }
}