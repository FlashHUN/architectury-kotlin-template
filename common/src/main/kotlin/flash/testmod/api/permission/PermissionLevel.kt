package flash.testmod.api.permission

/**
 * Represents the different permission levels used in Minecraft.
 * See the Minecraft Wiki [entry](https://minecraft.fandom.com/wiki/Permission_level) for more information.
 * This is mean as a human friendly util over the obfuscated fields in [CommandManager].
 */
enum class PermissionLevel(val numericalValue: Int) {

    NONE(0),
    SPAWN_PROTECTION_BYPASS(1),
    CHEAT_COMMANDS_AND_COMMAND_BLOCKS(2),
    MULTIPLAYER_MANAGEMENT(3),
    ALL_COMMANDS(4)

}