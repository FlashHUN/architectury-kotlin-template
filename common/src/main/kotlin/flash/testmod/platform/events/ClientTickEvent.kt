/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package flash.testmod.platform.events

import net.minecraft.client.Minecraft

/**
 * Event fired whenever the game ticks on the client side.
 */
interface ClientTickEvent {

    /**
     * The [Minecraft] instance.
     */
    val client: Minecraft

    /**
     * Fired during the Pre tick phase.
     */
    data class Pre(override val client: Minecraft) : ClientTickEvent

    /**
     * Fired during the Post tick phase.
     */
    data class Post(override val client: Minecraft) : ClientTickEvent

}