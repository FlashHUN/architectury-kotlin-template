package flash.testmod.forge

import flash.testmod.platform.events.ClientPlayerEvent
import flash.testmod.platform.events.ClientTickEvent
import flash.testmod.platform.events.PlatformEvents
import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.ClientPlayerNetworkEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

@OnlyIn(Dist.CLIENT)
object ForgeClientEventHandler {

    fun register() {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onTick(e: TickEvent.ClientTickEvent) {
        if (e.phase == TickEvent.Phase.START) {
            PlatformEvents.CLIENT_TICK_PRE.post(ClientTickEvent.Pre(Minecraft.getInstance()))
        }
        else {
            PlatformEvents.CLIENT_TICK_POST.post(ClientTickEvent.Post(Minecraft.getInstance()))
        }
    }

    @SubscribeEvent
    fun onLogin(e: ClientPlayerNetworkEvent.LoggingIn) {
        PlatformEvents.CLIENT_PLAYER_LOGIN.post(ClientPlayerEvent.Login(e.player))
    }

    @SubscribeEvent
    fun onLogout(e: ClientPlayerNetworkEvent.LoggingOut) {
        PlatformEvents.CLIENT_PLAYER_LOGOUT.post(ClientPlayerEvent.Logout(e.player ?: return))
    }

    @SubscribeEvent
    fun onItemTooltip(e: ItemTooltipEvent) {
        PlatformEvents.CLIENT_ITEM_TOOLTIP.post(flash.testmod.platform.events.ItemTooltipEvent(e.itemStack, e.flags, e.toolTip))
    }

}