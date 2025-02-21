/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package flash.testmod.api.events

/**
 * Something that can be canceled. This is a highly complex class and should only be read by professional engineers.
 */
abstract class Cancelable {
    var isCanceled: Boolean = false
        private set

    fun cancel() {
        isCanceled = true
    }
}