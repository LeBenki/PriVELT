package com.kent.university.privelt.base;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.kent.university.privelt.R;
import com.kent.university.privelt.database.PriVELTDatabase;
import com.kent.university.privelt.utils.DriveServiceHelper;

import java.util.Collections;

public abstract class GoogleDriveActivity extends BaseActivity {

    private static final int REQUEST_CODE_SIGN_IN = 1;

    protected static final int PROCESS_SAVE = 2;
    protected static final int PROCESS_DOWNLOAD = 3;

    private DriveServiceHelper mDriveServiceHelper;

    protected GoogleDriveListener listener;

    protected int process = 0;
    protected String fileId = "";

    protected void googleDriveConnection(int process, String fileId) {
        this.process = process;
        this.fileId = fileId;

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        Log.d("LUCAS", "1");
        if (account == null) {
            Log.d("LUCAS", "1.5");
            requestSignIn();
        } else {
            Log.d("LUCAS", "2");

            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                            this, Collections.singleton(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccount(account.getAccount());
            com.google.api.services.drive.Drive googleDriveService =
                    new com.google.api.services.drive.Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new GsonFactory(),
                            credential)
                            .setApplicationName(getString(R.string.app_name))
                            .build();
            mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
            saveOrDownloadFile();
        }
    }

    private void requestSignIn() {

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private void saveOrDownloadFile() {
        Log.d("LUCAS", "3");
        if (process == PROCESS_SAVE)
        mDriveServiceHelper.uploadFile(getDatabasePath(PriVELTDatabase.PriVELTDatabaseName), null)
                .addOnSuccessListener(s -> listener.onSaveSuccess(s))
                .addOnFailureListener(e -> listener.onSaveFailure());
        else if (process == PROCESS_DOWNLOAD)
            mDriveServiceHelper.downloadFile(fileId, getDatabasePath(PriVELTDatabase.PriVELTDatabaseName).getPath())
                    .addOnSuccessListener(s -> listener.onDownloadSuccess())
                    .addOnFailureListener(e -> listener.onDownloadFailure());
    }

    private void handleSignInResult(Intent result) {
        Log.d("LUCAS", "3.5");
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("Drive API Migration")
                                    .build();

                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
                    saveOrDownloadFile();
                })
                .addOnFailureListener(exception -> Log.e("TAG", "Unable to sign in.", exception));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.d("LUCAS", "3.2");
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.d("LUCAS", "3.3");
            if (resultCode == Activity.RESULT_OK && resultData != null) {
                Log.d("LUCAS", "3.4");
                handleSignInResult(resultData);
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }
}
