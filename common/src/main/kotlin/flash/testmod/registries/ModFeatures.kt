package flash.testmod.registries

import flash.testmod.api.PlatformRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.levelgen.feature.Feature

object ModFeatures : PlatformRegistry<Registry<Feature<*>>, ResourceKey<Registry<Feature<*>>>, Feature<*>>() {
    override val registry: Registry<Feature<*>> = BuiltInRegistries.FEATURE
    override val registryKey: ResourceKey<Registry<Feature<*>>> = Registries.FEATURE
}