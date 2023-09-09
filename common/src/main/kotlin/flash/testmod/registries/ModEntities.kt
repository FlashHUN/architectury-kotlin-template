package flash.testmod.registries

import flash.testmod.platform.PlatformRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeSupplier

object ModEntities : PlatformRegistry<Registry<EntityType<*>>, ResourceKey<Registry<EntityType<*>>>, EntityType<*>>() {
    override val registry: Registry<EntityType<*>> = BuiltInRegistries.ENTITY_TYPE
    override val registryKey: ResourceKey<Registry<EntityType<*>>> = Registries.ENTITY_TYPE

    fun registerAttributes(consumer: (EntityType<out LivingEntity>, AttributeSupplier.Builder) -> Unit) {
//        consumer(CUSTOM_ENTITY, CustomEntity.createAttributes())
    }
}