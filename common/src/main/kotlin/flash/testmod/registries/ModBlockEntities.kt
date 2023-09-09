package flash.testmod.registries

import flash.testmod.platform.PlatformRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.entity.BlockEntityType

object ModBlockEntities : PlatformRegistry<Registry<BlockEntityType<*>>, ResourceKey<Registry<BlockEntityType<*>>>, BlockEntityType<*>>() {
    override val registry: Registry<BlockEntityType<*>> = BuiltInRegistries.BLOCK_ENTITY_TYPE
    override val registryKey: ResourceKey<Registry<BlockEntityType<*>>> = Registries.BLOCK_ENTITY_TYPE
}