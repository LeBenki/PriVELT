package com.kent.university.privelt.utils.hat;

import android.os.AsyncTask;
import com.kent.university.privelt.base.PDAListener;
import com.privelt.pda.dataplatform.generic.DataPlatform;
import com.privelt.pda.dataplatform.generic.Result;
import com.privelt.pda.dataplatform.hat.HatPlatform;
import com.privelt.pda.dataplatform.hat.files.HatFilesOps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.ResponseBody;

public class DownloadFileTask extends AsyncTask<Double, String, Result> {

    private DataPlatformController controller;
    private String hatFileID;
    private String path;
    private PDAListener listener;

    public DownloadFileTask(DataPlatformController controller, String hatFileID, String path, PDAListener listener) {
        this.controller = controller;
        this.hatFileID = hatFileID;
        this.path = path;
        this.listener = listener;
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
    protected void onPostExecute(Result downloadFileResponse) {
        if (downloadFileResponse.isSuccessful()) {
            Result.Success<ResponseBody> successResult = (Result.Success<ResponseBody>) downloadFileResponse;

            try {
                File file = new File(path);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream stream = new FileOutputStream(path);
                stream.write(successResult.getData().bytes());
                listener.onDownloadSuccess();
            } catch (IOException e1) {
                e1.printStackTrace();
                listener.onDownloadFailure();
            }

        } else {
            Result.Failed failedResult = (Result.Failed) downloadFileResponse;
            listener.onDownloadFailure();
        }
    }
}

