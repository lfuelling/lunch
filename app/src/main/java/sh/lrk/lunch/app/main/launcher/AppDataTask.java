package sh.lrk.lunch.app.main.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import sh.lrk.lunch.R;
import sh.lrk.lunch.app.settings.SettingsActivity;

public class AppDataTask extends AsyncTask<ApplicationInfo, Void, AppData> {
    private static final String TAG = AppDataTask.class.getCanonicalName();

    private final PackageManager packageManager;
    private final String packageName;
    private final Drawable settingsDrawable;
    private final String settingsTitle;
    private final AtomicReference<Context> contextRef;

    AppDataTask(Context context) {
        this.packageName = context.getPackageName();
        this.packageManager = context.getPackageManager();
        this.contextRef = new AtomicReference<>(context);

        this.settingsDrawable = context.getDrawable(R.drawable.ic_settings_deep_purple_a700_24dp);
        this.settingsTitle = context.getString(R.string.title_activity_settings);
    }

    @Override
    protected AppData doInBackground(ApplicationInfo... applicationInfos) {
        ApplicationInfo appInfo = applicationInfos[0];
        if (packageName.equals(appInfo.packageName)) {
            // launch settings instead of launcher
            return new AppData(settingsDrawable, settingsTitle, new Intent(contextRef.get().getApplicationContext(), SettingsActivity.class), appInfo);
        } else {
            Intent defaultLaunchIntent = packageManager.getLaunchIntentForPackage(appInfo.packageName);
            Intent fTargetIntent = tryToFindDefaultIntent(appInfo, defaultLaunchIntent, packageManager);
            return new AppData(packageManager.getApplicationIcon(appInfo), packageManager.getApplicationLabel(appInfo).toString(), fTargetIntent, appInfo);
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