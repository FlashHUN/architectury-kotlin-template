package flash.testmod.api.permission

import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.server.level.ServerPlayer

interface PermissionValidator {

    /**
     * Invoked when the validator replaces the existing one in [ModMain.permissionValidator].
     *
     */
    fun initialize()

    /**
     * Validates a permission for [ServerPlayer].
     *
     * @param player The target [ServerPlayer].
     * @param permission The [Permission] being queried.
     * @return If the [player] has the [permission].
     */
    fun hasPermission(player: ServerPlayer, permission: Permission): Boolean

    /**
     * Validates a permission for [SharedSuggestionProvider].
     *
     * @param source The target [SharedSuggestionProvider].
     * @param permission The [Permission] being queried.
     * @return If the [source] has the [permission].
     */
    fun hasPermission(source: SharedSuggestionProvider, permission: Permission): Boolean

}