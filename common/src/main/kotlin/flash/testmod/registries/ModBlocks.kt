package flash.testmod.registries

import flash.testmod.platform.PlatformRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block

object ModBlocks : PlatformRegistry<Registry<Block>, ResourceKey<Registry<Block>>, Block>() {
    override val registry: Registry<Block> = BuiltInRegistries.BLOCK
    override val registryKey: ResourceKey<Registry<Block>> = Registries.BLOCK


}