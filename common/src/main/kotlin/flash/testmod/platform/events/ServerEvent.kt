/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package flash.testmod.platform.events

import net.minecraft.server.MinecraftServer

/**
 * Events fired during the life cycle of a [MinecraftServer].
 */
interface ServerEvent {

    /**
     * The [MinecraftServer] backing the event trigger.
     */
    val server: MinecraftServer

    /**
     * Fired when the server is starting.
     */
    data class Starting(override val server: MinecraftServer) : ServerEvent

    /**
     * Fired when the server has started.
     */
    data class Started(override val server: MinecraftServer) : ServerEvent

    /**
     * Fired when the server is stopping.
     */
    data class Stopping(override val server: MinecraftServer) : ServerEvent

    /**
     * Fired when the server has stopped.
     */
    data class Stopped(override val server: MinecraftServer) : ServerEvent

}