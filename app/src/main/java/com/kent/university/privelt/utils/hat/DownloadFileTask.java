package com.kent.university.privelt.utils.hat;

import android.os.AsyncTask;

import com.privelt.pda.dataplatform.generic.DataPlatform;
import com.privelt.pda.dataplatform.generic.Result;
import com.privelt.pda.dataplatform.hat.HatPlatform;
import com.privelt.pda.dataplatform.hat.files.HatFilesOps;
import com.privelt.pda.dataplatform.hat.files.HatUploadedFileDetails;

public class DownloadFileTask extends AsyncTask<Double, String, Result> {

    private DataPlatformController controller;
    private String hatFileID;

    public DownloadFileTask(DataPlatformController controller, String hatFileID) {
        this.controller = controller;
        this.hatFileID = hatFileID;
    }

    @Override
    protected Result doInBackground(Double... strings) {
        DataPlatform dp = controller.getPlatform();
        if (dp instanceof HatPlatform) {
            HatFilesOps hatFilesOps = ((HatPlatform) dp).getHatFilesOps();
            return hatFilesOps.download(hatFileID);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Result uploadedFileResponse) {
        if (uploadedFileResponse.isSuccessful()) {
            Result.Success<HatUploadedFileDetails> successResult = (Result.Success<HatUploadedFileDetails>) uploadedFileResponse;
        } else {
            Result.Failed failedResult = (Result.Failed) uploadedFileResponse;
        }
    }
}

