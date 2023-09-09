package flash.testmod.util

import flash.testmod.Environment
import flash.testmod.ModMain
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.Level
import java.util.concurrent.CompletableFuture

/** Runs the given [Runnable] if the caller is on the CLIENT side. */
fun ifClient(runnable: Runnable) {
    if (ModMain.implementation.environment() == Environment.CLIENT) {
        runnable.run()
    }
}

/** Runs the given [Runnable] if the caller is on the SERVER side. */
fun ifServer(runnable: Runnable) {
    if (ModMain.implementation.environment() == Environment.SERVER) {
        runnable.run()
    }
}

/** Runs the given [Runnable] if the caller is a dedicated server. */
fun ifDedicatedServer(action: Runnable) {
    if (ModMain.implementation.environment() == Environment.SERVER) {
        action.run()
    }
}

/*
 * Schedules the given block of code to run on the main thread and returns a [CompletableFuture] that completes with the result of the block when the code has executed.
 */
fun <T> runOnServer(block: () -> T): CompletableFuture<T> {
    val future = CompletableFuture<T>()
    val server = server()
    if (server == null) {
        future.completeExceptionally(IllegalStateException("There is no server to schedule it on."))
    } else {
        server.execute { future.complete(block()) }
    }
    return future
}

fun server(): MinecraftServer? = ModMain.implementation.server()

fun Level.isServerSide() = !isClientSide