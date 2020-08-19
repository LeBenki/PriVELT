package com.kent.university.privelt.utils.hat;

import android.os.AsyncTask;

import com.privelt.pda.dataplatform.generic.DataPlatform;
import com.privelt.pda.dataplatform.generic.Result;
import com.privelt.pda.dataplatform.hat.HatPlatform;
import com.privelt.pda.dataplatform.hat.files.HatFileDetails;
import com.privelt.pda.dataplatform.hat.files.HatFilesOps;
import com.privelt.pda.dataplatform.hat.files.HatUploadedFileDetails;

public class UploadFileTask extends AsyncTask<Double, String, Result> {

    private DataPlatformController controller;
    private HatFileDetails hatFilesDetails;

    public UploadFileTask(DataPlatformController controller, HatFileDetails hatFilesDetails) {
        this.controller = controller;
        this.hatFilesDetails = hatFilesDetails;
    }

    @Override
    protected Result doInBackground(Double... strings) {
        DataPlatform dp = controller.getPlatform();
        if (dp instanceof HatPlatform) {
            HatFilesOps hatFilesOps = ((HatPlatform) dp).getHatFilesOps();
            return hatFilesOps.upload(hatFilesDetails);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Result uploadedFileResponse) {
        if (uploadedFileResponse.isSuccessful()) {
            Result.Success<HatUploadedFileDetails> successResult = (Result.Success<HatUploadedFileDetails>) uploadedFileResponse;
            successResult.getData().toString();
            successResult.getData().getFileId();
        } else {
            Result.Failed failedResult = (Result.Failed) uploadedFileResponse;
        }
    }
}