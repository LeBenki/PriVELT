package com.kent.university.privelt.base;

public interface GoogleDriveListener {
    void onSaveSuccess(String fileId);
    void onDownloadSuccess();
    void onSaveFailure();
    void onDownloadFailure();
}