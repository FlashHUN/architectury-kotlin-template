package flash.testmod.util

import flash.testmod.ModMain
import net.minecraft.network.chat.Component
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