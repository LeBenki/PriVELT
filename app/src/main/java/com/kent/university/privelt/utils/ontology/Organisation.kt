/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils.ontology

enum class Organisation(var label: String, val ownedServices: List<String>) {
    GOOGLE("Google LLC", listOf("Google")),
    FACEBOOK("Facebook, Inc.", listOf("Facebook", "Instagram")),
    EXPEDIA("Expedia Group", listOf("Expedia, Hotels.com")),
    BOOKING("Booking Holdings", listOf("Booking", "Agoda")),
    TWITTER("Twitter, Inc.", listOf("Twitter")),
    PINTEREST("Pinterest, Inc.", listOf("Pinterest"))
}