package flash.testmod.api.permission

import net.minecraft.resources.ResourceLocation

interface Permission {

    val resourceLocation: ResourceLocation

    val literal: String

    val level: PermissionLevel

}