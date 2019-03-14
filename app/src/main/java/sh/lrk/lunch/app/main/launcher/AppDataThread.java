package sh.lrk.lunch.app.main.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.Nullable;
import sh.lrk.lunch.R;
import sh.lrk.lunch.app.settings.SettingsActivity;

public class AppDataThread extends Thread {
    private static final String TAG = AppDataThread.class.getCanonicalName();

    private final PackageManager packageManager;
    private final String packageName;
    private final String settingsTitle;
    private final ApplicationInfo appInfo;
    private final Vector<AppData> target;
    private final AtomicReference<Context> contextRef;

    AppDataThread(Context context, ApplicationInfo appInfo, Vector<AppData> target) {
        this.packageName = context.getPackageName();
        this.packageManager = context.getPackageManager();
        this.contextRef = new AtomicReference<>(context);

        this.settingsTitle = context.getString(R.string.title_activity_settings);
        this.appInfo = appInfo;
        this.target = target;
    }

    @Override
    public void run() {
        if (packageName.equals(appInfo.packageName)) {
            // launch settings instead of launcher
            target.add(new AppData(R.drawable.ic_settings_deep_purple_a700_24dp, settingsTitle, new Intent(contextRef.get().getApplicationContext(), SettingsActivity.class), appInfo));
        } else {
            Intent defaultLaunchIntent = packageManager.getLaunchIntentForPackage(appInfo.packageName);
            Intent fTargetIntent = tryToFindDefaultIntent(appInfo, defaultLaunchIntent, packageManager);
            target.add(new AppData(packageManager.getApplicationLabel(appInfo).toString(), fTargetIntent, appInfo));
        }
    }

    @Nullable
    private static Intent tryToFindDefaultIntent(ApplicationInfo appInfo, Intent defaultLaunchIntent, PackageManager packageManager) {
        Intent targetIntent = null;
        if (defaultLaunchIntent != null) {
            targetIntent = defaultLaunchIntent;
        } else {
            try {
                ActivityInfo[] activities = packageManager.getPackageInfo(appInfo.packageName, PackageManager.GET_ACTIVITIES).activities;
                ActivityInfo chosenActivity = null;
                if (activities != null) {
                    targetIntent = new Intent(Intent.ACTION_MAIN, null);
                    for (ActivityInfo activity : activities) {
                        if (activity.enabled && (activity.parentActivityName == null || activity.parentActivityName.isEmpty())) {
                            chosenActivity = activity;
                        }
                    }
                    if (chosenActivity != null) {
                        targetIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        final ComponentName cn = new ComponentName(appInfo.packageName, Activity.class.getName());
                        targetIntent.setComponent(cn);
                        targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "Unable to find default intent for package: '" + appInfo.packageName + "'");
            }
        }
        return targetIntent;
    }
}