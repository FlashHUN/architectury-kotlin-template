package flash.testmod.registries

import flash.testmod.platform.PlatformRegistry
import net.minecraft.core.Registry
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey

object ModParticles : PlatformRegistry<Registry<ParticleType<*>>, ResourceKey<Registry<ParticleType<*>>>, ParticleType<*>>() {
    override val registry: Registry<ParticleType<*>> = BuiltInRegistries.PARTICLE_TYPE
    override val registryKey: ResourceKey<Registry<ParticleType<*>>> = Registries.PARTICLE_TYPE
}