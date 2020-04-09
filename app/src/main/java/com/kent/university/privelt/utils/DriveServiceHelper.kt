/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.utils

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DriveServiceHelper(private val mDriveService: Drive) {
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()
    fun uploadFile(localFile: File, fileId: String?): String {
        val fileMetadata = com.google.api.services.drive.model.File()
        fileMetadata.name = localFile.name
        val mediaContent = FileContent("image/jpeg", localFile)
        var file: com.google.api.services.drive.model.File? = null
        try {
            file = if (fileId != null && !fileId.isEmpty()) mDriveService.files().update(fileId, fileMetadata, mediaContent)
                    .setFields("id")
                    .execute() else mDriveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file!!.id
    }

    fun downloadFile(fileId: String?, path: String?): Task<Void?> {
        return Tasks.call(mExecutor, Callable {
            val file = File(path)
            val outputStream = FileOutputStream(file)
            mDriveService.files()[fileId].executeMediaAndDownloadTo(outputStream)
            null
        })
    }

}