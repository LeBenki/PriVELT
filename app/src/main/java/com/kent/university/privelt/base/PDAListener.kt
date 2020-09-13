/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.base

interface PDAListener {
    fun onDownloadSuccess()
    fun onDownloadFailure()
    fun onConnectionSuccess()
    //TODO before Automatic upload
    fun onHatUploadSuccess(fileId: String)
    fun onHatUploadFailure(error: String)
}