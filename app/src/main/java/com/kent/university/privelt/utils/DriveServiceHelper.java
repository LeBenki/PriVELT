package com.kent.university.privelt.utils;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.Nullable;

public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;

    public DriveServiceHelper(Drive driveService) {
        mDriveService = driveService;
    }

    public Task<String> uploadFile(final java.io.File localFile, @Nullable final String folderId) {
        Log.d("LUCAS", "4");
        return Tasks.call(mExecutor, () -> {

            Log.d("LUCAS", "5");
            File fileMetadata = new File();
            fileMetadata.setName(localFile.getName());
            FileContent mediaContent = new FileContent("image/jpeg", localFile);
            File file = null;
            try {
                file = mDriveService.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("LUCAS", "6");
            return file.getId();
        });
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