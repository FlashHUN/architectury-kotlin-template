package flash.testmod.api.permission

import flash.testmod.ModMain
import net.minecraft.commands.CommandSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.server.level.ServerPlayer

/**
 * A [PermissionValidator] that uses the permission level vanilla system.
 * This is only used when the platform has no concept of permissions.
 */
class LaxPermissionValidator : PermissionValidator {

    override fun initialize() {
        ModMain.LOGGER.info("Booting LaxPermissionValidator, permissions will be checked using Minecrafts permission level system, see https://minecraft.fandom.com/wiki/Permission_level")
    }

    override fun hasPermission(player: ServerPlayer, permission: Permission) = player.hasPermissions(permission.level.numericalValue)
    override fun hasPermission(source: SharedSuggestionProvider, permission: Permission) = source.hasPermission(permission.level.numericalValue)
}