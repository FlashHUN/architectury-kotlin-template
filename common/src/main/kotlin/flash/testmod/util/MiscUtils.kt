package flash.testmod.util

import flash.testmod.ModMain
import flash.testmod.client.resources.AnimatedServerTextures
import flash.testmod.client.resources.ServerTextures
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import kotlin.random.Random

fun resource(path: String, namespace: String = ModMain.MOD_ID) = ResourceLocation(namespace, path)

fun String.translated() = Component.translatable(this)
fun String.translated(vararg data: Any) = Component.translatable(this, *data)
fun String.isInt() = this.toIntOrNull() != null

val Pair<Boolean, Boolean>.either: Boolean get() = first || second

fun Random.nextBetween(min: Float, max: Float): Float {
    return nextFloat() * (max - min) + min;
}
fun Random.nextBetween(min: Double, max: Double): Double {
    return nextDouble() * (max - min) + min;
}
fun Random.nextBetween(min: Int, max: Int): Int {
    return nextInt(max - min + 1) + min
}

fun ResourceLocation.texture(fallback: ResourceLocation = MissingTextureAtlasSprite.getLocation()) = ServerTextures.getOrCreate(this, fallback).location()
fun ResourceLocation.textureAnimated(fallback: ResourceLocation = MissingTextureAtlasSprite.getLocation()) = AnimatedServerTextures.getOrCreate(this, fallback).texture()
fun GuiGraphics.blitMissingIcon(x: Int, y: Int, w: Int, h: Int) {
    blit(MissingTextureAtlasSprite.getLocation(), x, y, w, h, 0f, 0f, 256, 256, 256, 256)
}

fun imageChar(c: Char, fontLocation: ResourceLocation = resource("mod_font")): MutableComponent = Component.literal(c.toString()).withStyle(Style.EMPTY.withFont(fontLocation))