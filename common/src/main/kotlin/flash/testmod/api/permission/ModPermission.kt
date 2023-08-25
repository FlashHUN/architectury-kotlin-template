package flash.testmod.api.permission

import flash.testmod.ModMain
import flash.testmod.util.resource

data class ModPermission(
    private val node: String,
    override val level: PermissionLevel
) : Permission {

    override val resourceLocation = resource(this.node)

    override val literal = "${ModMain.MOD_ID}.${this.node}"
}