package flash.testmod.platform.events

import flash.testmod.api.observer.CancelableObservable
import flash.testmod.api.observer.EventObservable

object PlatformEvents {
    @JvmField
    val SERVER_STARTING = EventObservable<ServerEvent.Starting>()
    @JvmField
    val SERVER_STARTED = EventObservable<ServerEvent.Started>()
    @JvmField
    val SERVER_STOPPING = EventObservable<ServerEvent.Stopping>()
    @JvmField
    val SERVER_STOPPED = EventObservable<ServerEvent.Stopped>()

    @JvmField
    val SERVER_TICK_PRE = EventObservable<ServerTickEvent.Pre>()
    @JvmField
    val SERVER_TICK_POST = EventObservable<ServerTickEvent.Post>()

    @JvmField
    val CLIENT_TICK_PRE = EventObservable<ClientTickEvent.Pre>()
    @JvmField
    val CLIENT_TICK_POST = EventObservable<ClientTickEvent.Post>()

    @JvmField
    val SERVER_PLAYER_LOGIN = EventObservable<ServerPlayerEvent.Login>()
    @JvmField
    val SERVER_PLAYER_LOGOUT = EventObservable<ServerPlayerEvent.Logout>()
    @JvmField
    val CLIENT_PLAYER_LOGIN = EventObservable<ClientPlayerEvent.Login>()
    @JvmField
    val CLIENT_PLAYER_LOGOUT = EventObservable<ClientPlayerEvent.Logout>()
    @JvmField
    val PLAYER_DEATH = CancelableObservable<ServerPlayerEvent.Death>()
    @JvmField
    val RIGHT_CLICK_BLOCK = CancelableObservable<ServerPlayerEvent.RightClickBlock>()
    @JvmField
    val RIGHT_CLICK_ENTITY = CancelableObservable<ServerPlayerEvent.RightClickEntity>()

    @JvmField
    val CHANGE_DIMENSION = EventObservable<ChangeDimensionEvent>()

    @JvmField
    val CLIENT_ITEM_TOOLTIP = EventObservable<ItemTooltipEvent>()
}