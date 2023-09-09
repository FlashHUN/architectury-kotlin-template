package flash.testmod.forge

import flash.testmod.platform.events.*
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.event.server.ServerStoppedEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.server.ServerLifecycleHooks

object ForgeEventHandler {

    fun register() {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun serverStarting(e: ServerStartingEvent) {
        PlatformEvents.SERVER_STARTING.post(ServerEvent.Starting(e.server))
    }

    @SubscribeEvent
    fun serverStarted(e: ServerStartedEvent) {
        PlatformEvents.SERVER_STARTED.post(ServerEvent.Started(e.server))
    }


    @SubscribeEvent
    fun serverStopping(e: ServerStoppingEvent) {
        PlatformEvents.SERVER_STOPPING.post(ServerEvent.Stopping(e.server))
    }

    @SubscribeEvent
    fun serverStopped(e: ServerStoppedEvent) {
        PlatformEvents.SERVER_STOPPED.post(ServerEvent.Stopped(e.server))
    }

    @SubscribeEvent
    fun onTick(e: TickEvent.ServerTickEvent) {
        if (e.phase == TickEvent.Phase.START) {
            PlatformEvents.SERVER_TICK_PRE.post(ServerTickEvent.Pre(ServerLifecycleHooks.getCurrentServer()))
        }
        else {
            PlatformEvents.SERVER_TICK_POST.post(ServerTickEvent.Post(ServerLifecycleHooks.getCurrentServer()))
        }
    }

    @SubscribeEvent
    fun onLogin(e: PlayerEvent.PlayerLoggedInEvent) {
        val player = e.entity as? ServerPlayer ?: return
        PlatformEvents.SERVER_PLAYER_LOGIN.post(flash.testmod.platform.events.ServerPlayerEvent.Login(player))
    }

    @SubscribeEvent
    fun onLogout(e: PlayerEvent.PlayerLoggedOutEvent) {
        val player = e.entity as? ServerPlayer ?: return
        PlatformEvents.SERVER_PLAYER_LOGOUT.post(flash.testmod.platform.events.ServerPlayerEvent.Logout(player))
    }

    @SubscribeEvent
    fun onDeath(e: LivingDeathEvent) {
        val player = e.entity as? ServerPlayer ?: return
        PlatformEvents.PLAYER_DEATH.postThen(
            event = flash.testmod.platform.events.ServerPlayerEvent.Death(player),
            ifSucceeded = {},
            ifCanceled = { e.isCanceled = true }
        )
    }

    @SubscribeEvent
    fun onRightClickBlock(e: PlayerInteractEvent.RightClickBlock) {
        val player = e.entity as? ServerPlayer ?: return
        val hand = e.hand
        val pos = e.pos
        val face = e.face
        PlatformEvents.RIGHT_CLICK_BLOCK.postThen(
            event = flash.testmod.platform.events.ServerPlayerEvent.RightClickBlock(player, pos, hand, face),
            ifSucceeded = {},
            ifCanceled = { e.isCanceled = true }
        )
    }

    @SubscribeEvent
    fun onRightClickEntity(e: PlayerInteractEvent.EntityInteract) {
        val player = e.entity as? ServerPlayer ?: return
        val hand = e.hand
        val item = player.getItemInHand(hand)
        val entity = e.target
        PlatformEvents.RIGHT_CLICK_ENTITY.postThen(
            event = ServerPlayerEvent.RightClickEntity(player, item, hand, entity),
            ifSucceeded = {},
            ifCanceled = { e.isCanceled = true }
        )
    }

    @SubscribeEvent
    fun onChangeDimension(e: PlayerEvent.PlayerChangedDimensionEvent) {
        val player = e.entity
        if (player is ServerPlayer) {
            PlatformEvents.CHANGE_DIMENSION.post(ChangeDimensionEvent(player))
        }
    }
}