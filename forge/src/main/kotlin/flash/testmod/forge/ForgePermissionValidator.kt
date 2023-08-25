package flash.testmod.forge

import flash.testmod.ModMain
import flash.testmod.api.permission.ModPermissions
import flash.testmod.api.permission.Permission
import flash.testmod.api.permission.PermissionValidator
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.server.permission.PermissionAPI
import net.minecraftforge.server.permission.events.PermissionGatherEvent
import net.minecraftforge.server.permission.nodes.PermissionNode
import net.minecraftforge.server.permission.nodes.PermissionTypes

object ForgePermissionValidator : PermissionValidator {

    private val nodes = hashMapOf<ResourceLocation, PermissionNode<Boolean>>()

    init {
        MinecraftForge.EVENT_BUS.addListener<PermissionGatherEvent.Nodes> { event ->
            ModMain.LOGGER.info("Starting Forge permission node registry")
            event.addNodes(this.createNodes())
            ModMain.LOGGER.debug("Finished Forge permission node registry")
        }
    }

    override fun initialize() {
        ModMain.LOGGER.info("Booting ForgePermissionApiPermissionValidator, player permissions will be checked using MinecraftForge' PermissionAPI, non player command sources will use Minecraft' permission level system, see https://docs.minecraftforge.net/en/latest/ and https://minecraft.fandom.com/wiki/Permission_level")
    }

    override fun hasPermission(player: ServerPlayer, permission: Permission): Boolean {
        val node = this.findNode(permission) ?: return player.hasPermissions(permission.level.numericalValue)
        return PermissionAPI.getPermission(player, node)
    }

    override fun hasPermission(source: SharedSuggestionProvider, permission: Permission): Boolean {
        val player = this.extractPlayerFromSource(source) ?: return source.hasPermission(permission.level.numericalValue)
        val node = this.findNode(permission) ?: return source.hasPermission(permission.level.numericalValue)
        return PermissionAPI.getPermission(player, node)
    }

    private fun createNodes() = ModPermissions.all().map { permission ->
        // 3rd arg is default value if no implementation is present essentially
        val node = PermissionNode(permission.resourceLocation, PermissionTypes.BOOLEAN, { player, _, _ -> player?.hasPermissions(permission.level.numericalValue) == true })
        this.nodes[permission.resourceLocation] = node
        ModMain.LOGGER.debug("Registered Forge permission node ${node.nodeName}")
        node
    }

    private fun findNode(permission: Permission) = this.nodes[permission.resourceLocation]

    private fun extractPlayerFromSource(source: SharedSuggestionProvider) = if (source is CommandSourceStack) source.player else null

}