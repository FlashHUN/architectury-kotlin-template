package flash.testmod.fabric

import flash.testmod.ModMain
import flash.testmod.api.permission.Permission
import flash.testmod.api.permission.PermissionValidator
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.server.level.ServerPlayer

class FabricPermissionValidator : PermissionValidator {
    override fun initialize() {
        ModMain.LOGGER.info("Booting FabricPermissionValidator, permissions will be checked using fabric-permissions-api, see https://github.com/lucko/fabric-permissions-api")
    }

    override fun hasPermission(player: ServerPlayer, permission: Permission) = Permissions.check(player, permission.literal, permission.level.numericalValue)

    override fun hasPermission(source: SharedSuggestionProvider, permission: Permission) = Permissions.check(source, permission.literal, permission.level.numericalValue)
}