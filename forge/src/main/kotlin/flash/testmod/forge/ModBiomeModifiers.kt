package flash.testmod.forge

import com.mojang.serialization.Codec
import flash.testmod.util.resource
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import net.minecraftforge.common.world.BiomeModifier
import net.minecraftforge.common.world.ModifiableBiomeInfo
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegisterEvent
import net.minecraftforge.server.ServerLifecycleHooks

/**
 * This class serves as a cheat to inject all our features via code instead of needing to use the Forge specific biome modifications system.
 */
internal object ModBiomeModifiers : BiomeModifier {

    private var codec: Codec<out BiomeModifier>? = null
    private val entries = arrayListOf<Entry>()

    fun register(event: RegisterEvent) {
        event.register(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS) { helper ->
            this.codec = Codec.unit(ModBiomeModifiers)
            helper.register(resource("inject_coded"), this.codec)
        }
    }

    fun add(feature: ResourceKey<PlacedFeature>, step: GenerationStep.Decoration, validTag: TagKey<Biome>?) {
        this.entries += Entry(feature, step, validTag)
    }

    override fun modify(arg: Holder<Biome>, phase: BiomeModifier.Phase, builder: ModifiableBiomeInfo.BiomeInfo.Builder) {
        if (phase != BiomeModifier.Phase.ADD) {
            return
        }
        val server = ServerLifecycleHooks.getCurrentServer()
        val registry = server.registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
        this.entries.forEach { entry ->
            if (entry.validTag == null || arg.`is`(entry.validTag)) {
                builder.generationSettings.addFeature(entry.step, Holder.direct(registry.get(entry.feature)))
            }
        }
    }

    override fun codec(): Codec<out BiomeModifier> = this.codec ?: Codec.unit(ModBiomeModifiers)

    private data class Entry(val feature: ResourceKey<PlacedFeature>, val step: GenerationStep.Decoration, val validTag: TagKey<Biome>?)

}