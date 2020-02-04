package com.kent.university.privelt.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.kent.university.privelt.BuildConfig;
import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;
import com.kent.university.webviewautologin.response.ResponseCallback;
import com.kent.university.webviewautologin.response.ResponseEnum;
import com.kent.university.webviewautologin.services.LoginService;
import com.university.kent.dataextractor.DataExtractor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.kent.university.privelt.database.injections.Injection.provideServiceDataSource;
import static com.kent.university.privelt.database.injections.Injection.provideUserDataSource;
import static com.kent.university.privelt.model.UserData.DELIMITER;

public class DataExtraction {

    private static final String TAG = DataExtractor.class.getSimpleName();

    public static void processExtractionForEachService(Context applicationContext) {

        ServiceHelper serviceHelper = new ServiceHelper(((PriVELT) (applicationContext)).getCurrentActivity());

        ServiceDataRepository serviceDataRepository = provideServiceDataSource(applicationContext);

        List<Service> services = serviceDataRepository.getAllServices();
        for (com.kent.university.privelt.model.Service service : services) {
            if (!service.isPasswordSaved())
                continue;
            processDataExtraction(serviceHelper, service, service.getUser(), service.getPassword(), applicationContext);
        }
    }

    public static void processDataExtraction(ServiceHelper serviceHelper, com.kent.university.privelt.model.Service service, String email, String password, Context applicationContext) {

        UserDataRepository userDataRepository = provideUserDataSource(applicationContext);

        LoginService loginService = serviceHelper.getServiceWithName(service.getName());

        DataExtractor dataExtractor = new DataExtractor(loginService);
        final ArrayList<UserData> allUserData = new ArrayList<>();

        ((PriVELT) (applicationContext)).getCurrentActivity().runOnUiThread(() -> loginService.autoLogin(email, password, new ResponseCallback() {
            @Override
            public void getResponse(ResponseEnum responseEnum, String data) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, responseEnum.toString());
                if (responseEnum == ResponseEnum.SUCCESS) {
                    dataExtractor.injectAll(((PriVELT)applicationContext).getCurrentActivity(), (jsonArray, status) -> {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, status.toString());
                        if (status.isFailed() || jsonArray == null)
                            return;
                        allUserData.addAll(parseJSON(jsonArray, service));
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, jsonArray.toString());
                        if (status.isDone()) {
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "LOGIN SERVICE:" + allUserData.size());
                            userDataRepository.deleteUserDatasForAService(service.getId());

                            for (UserData userData : allUserData)
                                userDataRepository.insertUserDatas(userData);

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
