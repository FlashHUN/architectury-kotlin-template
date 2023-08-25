package flash.testmod.api.permission

object ModPermissions {
    private const val COMMAND_PREFIX = "command."
    private val permissions = arrayListOf<Permission>()

    fun all(): Iterable<Permission> = this.permissions

    private fun create(node: String, level: PermissionLevel): Permission {
        val permission = ModPermission(node, level)
        this.permissions += permission
        return permission
    }

}