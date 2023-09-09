/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package flash.testmod.api.observer

interface Observable<T> {
    fun subscribe(priority: Priority = Priority.NORMAL, handler: (T) -> Unit): ObservableSubscription<T>
    fun unsubscribe(subscription: ObservableSubscription<T>)
}