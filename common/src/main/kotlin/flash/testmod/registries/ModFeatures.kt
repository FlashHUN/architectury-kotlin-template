package flash.testmod.registries

import flash.testmod.platform.PlatformRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.feature.Feature

object ModFeatures : PlatformRegistry<Registry<Feature<*>>, ResourceKey<Registry<Feature<*>>>, Feature<*>>() {
    override val registry: Registry<Feature<*>> = BuiltInRegistries.FEATURE
    override val registryKey: ResourceKey<Registry<Feature<*>>> = Registries.FEATURE
}