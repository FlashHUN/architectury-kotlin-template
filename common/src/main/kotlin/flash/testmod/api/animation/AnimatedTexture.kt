package flash.testmod.api.animation

import net.minecraft.resources.ResourceLocation

interface AnimatedTexture : Animation {
    fun location() : ResourceLocation
    fun copyTexture() : AnimatedTexture
}