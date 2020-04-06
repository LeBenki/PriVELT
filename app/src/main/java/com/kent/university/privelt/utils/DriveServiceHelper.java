/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;

    public DriveServiceHelper(Drive driveService) {
        mDriveService = driveService;
    }

    public String uploadFile(final java.io.File localFile, String fileId) {
            File fileMetadata = new File();
            fileMetadata.setName(localFile.getName());
            FileContent mediaContent = new FileContent("image/jpeg", localFile);
            File file = null;
            try {
                if (fileId != null && !fileId.isEmpty())
                    file = mDriveService.files().update(fileId, fileMetadata, mediaContent)
                            .setFields("id")
                            .execute();
                else
                    file = mDriveService.files().create(fileMetadata, mediaContent)
                            .setFields("id")
                            .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file.getId();
    }

    public Task<Void> downloadFile(String fileId, String path) {
        return Tasks.call(mExecutor, () -> {
            java.io.File file = new java.io.File(path);
            FileOutputStream outputStream = new FileOutputStream(file);
            mDriveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
            return null;
        });
    }
}