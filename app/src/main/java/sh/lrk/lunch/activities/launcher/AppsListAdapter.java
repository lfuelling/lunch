package sh.lrk.lunch.activities.launcher;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import sh.lrk.lunch.R;
import sh.lrk.lunch.activities.settings.SettingsActivity;

import static sh.lrk.lunch.activities.settings.SettingsActivity.DEFAULT_TEXT_COLOR;
import static sh.lrk.lunch.activities.settings.SettingsActivity.KEY_LAUNCHER_TEXT_COLOR;

public class AppsListAdapter extends ArrayAdapter<ApplicationInfo> {

    private final SharedPreferences defaultSharedPreferences;

    private final LayoutInflater inflater;
    private final PackageManager packageManager;

    AppsListAdapter(Activity a, List<ApplicationInfo> installedApplications) {
        super(a, R.layout.layout_app_entry);
        addAll(installedApplications);
        inflater = a.getLayoutInflater();
        packageManager = a.getPackageManager();
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
    }

    @androidx.annotation.NonNull
    @Override
    public View getView(int position, @androidx.annotation.Nullable View convertView, @androidx.annotation.NonNull ViewGroup parent) {
        if (convertView != null) {
            return initUi(position, convertView);
        } else {
            return initUi(position, inflater.inflate(R.layout.layout_app_entry, parent, false));
        }
    }

    @androidx.annotation.NonNull
    private View initUi(int position, View view) {

        TextView appTitle = view.findViewById(R.id.appTitle);
        ImageView appImage = view.findViewById(R.id.appIcon);
        ApplicationInfo appInfo = AppsListAdapter.this.getItem(position);

        if (appInfo == null) {
            return view;
        }

        int color = defaultSharedPreferences.getInt(KEY_LAUNCHER_TEXT_COLOR, DEFAULT_TEXT_COLOR);
        appTitle.setTextColor(color);

        if (getContext().getPackageName().equals(appInfo.packageName)) {
            // launch settings instead of launcher
            appTitle.setText(R.string.title_activity_settings);
            appImage.setImageDrawable(getContext().getDrawable(R.drawable.ic_settings_deep_purple_a700_24dp));
            view.setOnClickListener(v -> getContext().startActivity(new Intent(getContext().getApplicationContext(), SettingsActivity.class)));
        } else {
            appTitle.setText(packageManager.getApplicationLabel(appInfo));
            appImage.setImageDrawable(packageManager.getApplicationIcon(appInfo));
            Intent defaultLaunchIntent = packageManager.getLaunchIntentForPackage(appInfo.packageName);
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
                    e.printStackTrace(); //TODO: Logging (?)
                }
            }

            final Intent fTargetIntent = targetIntent;
            view.setOnClickListener(v -> {
                try {
                    if (fTargetIntent != null) {
                        getContext().startActivity(fTargetIntent);
                    }
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "No launchable activity found!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }

}
