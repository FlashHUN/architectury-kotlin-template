package flash.testmod.registries

import flash.testmod.api.PlatformRegistry
import flash.testmod.util.resource
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent

object ModSounds : PlatformRegistry<Registry<SoundEvent>, ResourceKey<Registry<SoundEvent>>, SoundEvent>() {
    override val registry: Registry<SoundEvent> = BuiltInRegistries.SOUND_EVENT
    override val registryKey: ResourceKey<Registry<SoundEvent>> = Registries.SOUND_EVENT

    private fun create(name: String): SoundEvent = this.create(name, SoundEvent.createVariableRangeEvent(resource(name)))
}