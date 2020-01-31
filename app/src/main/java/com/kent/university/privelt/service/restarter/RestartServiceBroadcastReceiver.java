package com.kent.university.privelt.service.restarter;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.kent.university.privelt.service.ProcessMainClass;

import androidx.annotation.RequiresApi;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static com.kent.university.privelt.service.Service.RESTART_INTENT;

public class RestartServiceBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = RestartServiceBroadcastReceiver.class.getSimpleName();
    private static JobScheduler jobScheduler;
    private RestartServiceBroadcastReceiver restartSensorServiceReceiver;

    /**
     * it returns the number of version code
     *
     * @param context
     * @return
     */
    public static long getVersionCode(Context context) {
        PackageInfo pInfo;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            long versionCode = System.currentTimeMillis();  //PackageInfoCompat.getLongVersionCode(pInfo);
            return versionCode;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return 0;
    }



    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "about to start timer " + context.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            scheduleJob(context);
        } else {
            registerRestarterReceiver(context);
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(Context context) {
        if (jobScheduler == null) {
            jobScheduler = (JobScheduler) context
                    .getSystemService(JOB_SCHEDULER_SERVICE);
        }
        ComponentName componentName = new ComponentName(context,
                JobService.class);

        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                // setOverrideDeadline runs it immediately - you must have at least one constraint
                // https://stackoverflow.com/questions/51064731/firing-jobservice-without-constraints
                .setOverrideDeadline(0)
                .setPersisted(true).build();
        jobScheduler.schedule(jobInfo);
    }


    public static void reStartTracker(Context context) {
        // restart the never ending service
        Log.i(TAG, "Restarting tracker");
        Intent broadcastIntent = new Intent(RESTART_INTENT);
        context.sendBroadcast(broadcastIntent);
    }


    private void registerRestarterReceiver(final Context context) {

        if (restartSensorServiceReceiver == null)
            restartSensorServiceReceiver = new RestartServiceBroadcastReceiver();
        else try{
            context.unregisterReceiver(restartSensorServiceReceiver);
        } catch (Exception e){
            // not registered
        }
        // give the time to run
        new Handler().postDelayed(() -> {
            IntentFilter filter = new IntentFilter();
            filter.addAction(RESTART_INTENT);
            try {
                context.registerReceiver(restartSensorServiceReceiver, filter);
            } catch (Exception e) {
                try {
                    context.getApplicationContext().registerReceiver(restartSensorServiceReceiver, filter);
                } catch (Exception ex) {

                }
            }
        }, 1000);

    }

}
