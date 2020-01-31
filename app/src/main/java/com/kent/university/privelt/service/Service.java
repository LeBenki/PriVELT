package com.kent.university.privelt.service;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.R;
import com.kent.university.privelt.api.ServiceHelper;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;
import com.kent.university.privelt.service.utilities.Notification;
import com.kent.university.webviewautologin.response.ResponseCallback;
import com.kent.university.webviewautologin.response.ResponseEnum;
import com.kent.university.webviewautologin.services.LoginService;
import com.university.kent.dataextractor.DataExtractor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;

import static com.kent.university.privelt.database.injections.Injection.provideServiceDataSource;
import static com.kent.university.privelt.database.injections.Injection.provideUserDataSource;
import static com.kent.university.privelt.model.UserData.DELIMITER;

public class Service extends android.app.Service {
    public static final String RESTART_INTENT = "uk.ac.shef.oak.restarter";
    protected static final int NOTIFICATION_ID = 1337;
    private static String TAG = "Service";
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private ServiceHelper serviceHelper;

    public Service() {
        super();
    }

    /**
     * static to avoid multiple timers to be created when the service is called several times
     */
    private static Timer timer;
    private static TimerTask timerTask;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "restarting Service !!");

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(this);
        }

        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }

        process();

        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "restarting foreground");
            try {
                Notification notification = new Notification();
                startForeground(NOTIFICATION_ID, notification.setNotification(this, "Service notification", "We are extracting data for you", R.drawable.ic_sleep));
                Log.i(TAG, "restarting foreground successful");
                process();
            } catch (Exception e) {
                Log.e(TAG, "Error in notification " + e.getMessage());
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        // do not call stoptimertask because on some phones it is called asynchronously
        // after you swipe out the app and therefore sometimes
        // it will stop the timer after it was restarted
        // stoptimertask();
    }

    public void process() {

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 0;
        params.width = 0;
        params.height = 0;

        Log.i(TAG, "Starting timer");

        //set a new Timer - if one is already running, cancel it to avoid two running at the same time
        stoptimertask();
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        Log.i(TAG, "initialising TimerTask");
        timerTask = new TimerTask() {
            public void run() {
                if (((PriVELT)getApplicationContext()).getIdentityManager().getPassword() != null)
                    processExtractionForEachService();
            }
        };

        Log.i(TAG, "Scheduling...");
        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000*60*60);
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        Log.i(TAG, "initialising TimerTask");
        timerTask = new TimerTask() {
            public void run() {
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void processExtractionForEachService() {

        serviceHelper = new ServiceHelper(((PriVELT) (getApplicationContext())).getCurrentActivity());

        ServiceDataRepository serviceDataRepository = provideServiceDataSource(getApplicationContext());

        List<com.kent.university.privelt.model.Service> services = serviceDataRepository.getAllServices();
        for (com.kent.university.privelt.model.Service service : services) {
            if (!service.isPasswordSaved())
                continue;
            processDataExtraction(service, service.getUser(), service.getPassword());
        }
    }

    private void processDataExtraction(com.kent.university.privelt.model.Service service, String email, String password) {

        Log.d("DAMNNN", "LULU");
        UserDataRepository userDataRepository = provideUserDataSource(getApplicationContext());

        LoginService loginService = serviceHelper.getServiceWithName(service.getName());

        DataExtractor dataExtractor = new DataExtractor(loginService);
        final ArrayList<UserData> allUserData = new ArrayList<>();

        ((PriVELT) (getApplicationContext())).getCurrentActivity().runOnUiThread(() -> {
            loginService.load();
            windowManager.addView(loginService.getWebview(), params);
            loginService.autoLogin(email, password, new ResponseCallback() {
                @Override
                public void getResponse(ResponseEnum responseEnum, String data) {
                    Log.d("LUCAS", responseEnum.toString());
                    if (responseEnum == ResponseEnum.SUCCESS) {
                        dataExtractor.injectAll(((PriVELT)getApplicationContext()).getCurrentActivity(), (jsonArray, status) -> {
                            Log.d("LUCAS", status.toString());
                            if (status.isFailed() || jsonArray == null)
                                return;
                            allUserData.addAll(parseJSON(jsonArray, service));
                            Log.d("LUCASCACABOUDIN", jsonArray.toString());
                            if (status.isDone()) {
                                Log.d("LUCAS", "LOGIN SERVICE:" + allUserData.size());

                                userDataRepository.deleteUserDatasForAService(service.getId());

                                for (UserData userData : allUserData)
                                    userDataRepository.insertUserDatas(userData);

                            }
                        });
                    }
                }
            });
        });
    }

    private ArrayList<UserData> parseJSON(JSONArray jsonArray, com.kent.university.privelt.model.Service service) {
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
                Log.d("Response", userData.getType());
                Log.d("Response", userData.getValue());
                Log.d("Response", userData.getConcatenatedData());
                array.add(userData);
            }
        } catch (Exception e) {
            Log.d("Response", Objects.requireNonNull(e.getLocalizedMessage()));
        }
        return array;
    }
}
