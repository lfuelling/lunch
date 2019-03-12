package sh.lrk.lunch.activities.launcher;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import sh.lrk.lunch.R;
import sh.lrk.lunch.activities.settings.SettingsActivity;

import static sh.lrk.lunch.activities.settings.SettingsActivity.DEFAULT_TEXT_COLOR;
import static sh.lrk.lunch.activities.settings.SettingsActivity.KEY_LAUNCHER_TEXT_COLOR;

public class AppsListAdapter extends ArrayAdapter<ApplicationInfo> {

    private static final String TAG = AppsListAdapter.class.getCanonicalName();

    private final LayoutInflater inflater;
    private final PackageManager packageManager;
    private final int appTitleColor;

    AppsListAdapter(Activity a, List<ApplicationInfo> installedApplications) {
        super(a, R.layout.layout_app_entry);
        addAll(installedApplications);
        inflater = a.getLayoutInflater();
        packageManager = a.getPackageManager();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        appTitleColor = defaultSharedPreferences.getInt(KEY_LAUNCHER_TEXT_COLOR, DEFAULT_TEXT_COLOR);
    }

    @androidx.annotation.NonNull
    @Override
    public View getView(int position, @androidx.annotation.Nullable View convertView, @androidx.annotation.NonNull ViewGroup parent) {
        if (convertView != null) {
            return initUi(position, convertView, true);
        } else {
            return initUi(position, inflater.inflate(R.layout.layout_app_entry, parent, false), false);
        }
    }

    private static class AppDataTask extends AsyncTask<ApplicationInfo, Void, AppData> {
        private final PackageManager packageManager;
        private final String packageName;
        private final Drawable settingsDrawable;
        private final String settingsTitle;
        private final AtomicReference<View> viewRef;
        private final AtomicReference<Context> contextRef;
        private final AtomicReference<TextView> appTitleRef;
        private final AtomicReference<ImageView> appImageRef;

        public AppDataTask(Context context, View parent, TextView appTitle, ImageView appImage) {
            this.packageName = context.getPackageName();
            this.packageManager = context.getPackageManager();
            this.contextRef = new AtomicReference<>(context);

            this.settingsDrawable = context.getDrawable(R.drawable.ic_settings_deep_purple_a700_24dp);
            this.settingsTitle = context.getString(R.string.title_activity_settings);
            this.viewRef = new AtomicReference<>(parent);
            this.appTitleRef = new AtomicReference<>(appTitle);
            this.appImageRef = new AtomicReference<>(appImage);
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

        @Override
        protected void onPostExecute(AppData appData) {
            super.onPostExecute(appData);
            appTitleRef.get().setText(appData.label);
            appImageRef.get().setImageDrawable(appData.drawable);
            viewRef.get().setOnClickListener(v -> contextRef.get().startActivity(appData.intent));
            viewRef.get().setOnLongClickListener(v -> {
                PopupMenu popup = new PopupMenu(contextRef.get(), v);
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.action_uninstall:
                            Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                            uninstallIntent.setData(Uri.parse("package:" + appData.applicationInfo.packageName));
                            contextRef.get().startActivity(uninstallIntent);
                            return true;
                        case R.id.action_info:
                            Intent infoIntent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            infoIntent.setData(Uri.parse("package:" + appData.applicationInfo.packageName));
                            contextRef.get().startActivity(infoIntent);
                            return true;
                        default:
                            return false;
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.app_menu, popup.getMenu());
                popup.show();
                return true;
            });
        }
    }

    @androidx.annotation.NonNull
    private View initUi(int position, View view, boolean isConvertView) {
        TextView appTitle = view.findViewById(R.id.appTitle);
        ImageView appImage = view.findViewById(R.id.appIcon);
        ApplicationInfo appInfo = AppsListAdapter.this.getItem(position);

        if (appInfo == null) {
            return view;
        }

        if (isConvertView && appTitle.getText().equals(packageManager.getApplicationLabel(appInfo))) {
            return view;
        }
        
        appTitle.setTextColor(appTitleColor);
        new AppDataTask(getContext(), view, appTitle, appImage).execute(appInfo);
        return view;
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

    private static class AppData {
        private final Drawable drawable;
        private final String label;
        private final Intent intent;
        private final ApplicationInfo applicationInfo;

        private AppData(Drawable drawable, String label, Intent intent, ApplicationInfo applicationInfo) {
            this.drawable = drawable;
            this.label = label;
            this.intent = intent;
            this.applicationInfo = applicationInfo;
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public String getLabel() {
            return label;
        }

        public Intent getIntent() {
            return intent;
        }

        public ApplicationInfo getApplicationInfo() {
            return applicationInfo;
        }
    }
}
