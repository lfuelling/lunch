package sh.lrk.lunch.activities.launcher;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import sh.lrk.lunch.R;

import static sh.lrk.lunch.activities.settings.SettingsActivity.DEFAULT_LAUNCHER_BACKGROUND;
import static sh.lrk.lunch.activities.settings.SettingsActivity.KEY_LAUNCHER_BACKGROUND;
import static sh.lrk.lunch.activities.settings.SettingsActivity.KEY_SHOW_ALL_APPS;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = LauncherActivity.class.getCanonicalName();
    private SharedPreferences defaultSharedPreferences;
    private GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int color = defaultSharedPreferences.getInt(KEY_LAUNCHER_BACKGROUND, DEFAULT_LAUNCHER_BACKGROUND);
        getWindow().setBackgroundDrawable(new ColorDrawable(color));

        grid = findViewById(R.id.appsList);
        grid.setAdapter(new AppsListAdapter(LauncherActivity.this, getLaunchableApplications()));
    }

    private List<ApplicationInfo> getLaunchableApplications() {
        List<ApplicationInfo> installedApplications = new ArrayList<>();

        for (ApplicationInfo a : getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA)) {
            boolean showAllApps = defaultSharedPreferences.getBoolean(KEY_SHOW_ALL_APPS, false);
            if (getPackageManager().getLaunchIntentForPackage(a.packageName) != null) {
                installedApplications.add(a);
            } else if(showAllApps) {
                installedApplications.add(a);
            } else {
                Log.d(TAG, "Skipping addition of: '" + a.packageName + "'");
            }
        }

        return installedApplications;
    }
}
