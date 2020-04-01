package com.kent.university.privelt.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;
import com.kent.university.privelt.BuildConfig;
import com.kent.university.privelt.PriVELTApplication;
import com.kent.university.privelt.R;
import com.kent.university.privelt.database.PriVELTDatabase;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.Settings;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.SettingsDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;
import com.kent.university.privelt.utils.DriveServiceHelper;
import com.kent.university.webviewautologin.response.ResponseCallback;
import com.kent.university.webviewautologin.response.ResponseEnum;
import com.kent.university.webviewautologin.services.LoginService;
import com.university.kent.dataextractor.DataExtractor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.kent.university.privelt.injections.Injection.provideServiceDataSource;
import static com.kent.university.privelt.injections.Injection.provideSettingsDataSource;
import static com.kent.university.privelt.injections.Injection.provideUserDataSource;
import static com.kent.university.privelt.model.UserData.DELIMITER;

public class DataExtraction {

    private static final String TAG = DataExtractor.class.getSimpleName();

    private static void saveToGoogleDrive() {

        ServiceDataRepository serviceDataRepository = provideServiceDataSource(PriVELTApplication.getInstance());

        SettingsDataRepository settingsDataRepository = provideSettingsDataSource(PriVELTApplication.getInstance());

        Settings settings = settingsDataRepository.getInstantSettings();

        if (settings != null && settings.isGoogleDriveAutoSave()) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(PriVELTApplication.getInstance());

                GoogleAccountCredential credential =
                        GoogleAccountCredential.usingOAuth2(
                                PriVELTApplication.getInstance(), Collections.singleton(DriveScopes.DRIVE_FILE));
                credential.setSelectedAccount(account.getAccount());
                com.google.api.services.drive.Drive googleDriveService =
                        new com.google.api.services.drive.Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName(PriVELTApplication.getInstance().getResources().getString(R.string.app_name))
                                .build();
                DriveServiceHelper mDriveServiceHelper = new DriveServiceHelper(googleDriveService);

                List<Service> serviceList = serviceDataRepository.getAllServices();
                List<Service> oldServices = cloneList(serviceList);
                for (int i = 0; i < serviceList.size(); i++) {
                    serviceList.get(i).setPassword("");
                    serviceList.get(i).setUser("");
                    serviceList.get(i).setPasswordSaved(false);
                    serviceDataRepository.updateServices(serviceList.get(i));
                }
                String s = mDriveServiceHelper.uploadFile(PriVELTApplication.getInstance().getDatabasePath(PriVELTDatabase.PriVELTDatabaseName), settings.getGoogleDriveFileID());
                for (Service service : oldServices)
                    serviceDataRepository.updateServices(service);

                settings.setGoogleDriveFileID(s);
                settingsDataRepository.updateSettings(settings);

                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Google drive saved");

            }
            catch (Exception ignored) {

            }
        }
    }

    public static void processExtractionForEachService(Context applicationContext) {

        ServiceHelper serviceHelper = new ServiceHelper(((PriVELTApplication) (applicationContext)).getCurrentActivity());

        ServiceDataRepository serviceDataRepository = provideServiceDataSource(applicationContext);

        List<Service> services = serviceDataRepository.getAllServices();
        for (com.kent.university.privelt.model.Service service : services) {
            if (!service.isPasswordSaved())
                continue;
            processDataExtraction(serviceHelper, service, service.getUser(), service.getPassword(), applicationContext);
        }
     }

    private static List<Service> cloneList(List<Service> list) throws CloneNotSupportedException {
        List<Service> clone = new ArrayList<>(list.size());
        for (Service item : list) clone.add((Service) item.clone());
        return clone;
    }

    public static void processDataExtraction(ServiceHelper serviceHelper, com.kent.university.privelt.model.Service service, String email, String password, Context applicationContext) {

        UserDataRepository userDataRepository = provideUserDataSource(applicationContext);

        LoginService loginService = serviceHelper.getServiceWithName(service.getName());

        DataExtractor dataExtractor = new DataExtractor(loginService);
        final ArrayList<UserData> allUserData = new ArrayList<>();

        ((PriVELTApplication) (applicationContext)).getCurrentActivity().runOnUiThread(() -> loginService.autoLogin(email, password, new ResponseCallback() {
            @Override
            public void getResponse(ResponseEnum responseEnum, String data) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, responseEnum.toString());
                if (responseEnum == ResponseEnum.SUCCESS) {
                    dataExtractor.injectAll(((PriVELTApplication)applicationContext).getCurrentActivity(), (jsonArray, status) -> {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, status.toString());
                        if (jsonArray != null) {
                            allUserData.addAll(parseJSON(jsonArray, service));
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, jsonArray.toString());
                        }
                        if (status.isDone()) {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "LOGIN SERVICE:" + allUserData.size());
                            userDataRepository.deleteUserDatasForAService(service.getId());

                            for (UserData userData : allUserData)
                                userDataRepository.insertUserDatas(userData);

                            saveToGoogleDrive();
                        }
                    });
                }
            }
        }));
    }


    private static ArrayList<UserData> parseJSON(JSONArray jsonArray, com.kent.university.privelt.model.Service service) {
        ArrayList<UserData> array = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                JSONArray datas = obj.getJSONArray("data");
                List<String> td = new ArrayList<>();
                for (int j = 0; j < datas.length(); j++) {
                    td.add(datas.getString(j));
                }
                UserData userData = new UserData(
                        obj.getString("title"),
                        obj.getString("type"),
                        obj.getString("value"),
                        TextUtils.join(DELIMITER, td),
                        service.getId());
                array.add(userData);
            }
        } catch (Exception ignored) {
        }
        return array;
    }
}
