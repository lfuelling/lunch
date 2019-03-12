package sh.lrk.lunch.activities.launcher;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import sh.lrk.lunch.R;

public class LauncherActivity extends AppCompatActivity {

    private SharedPreferences defaultSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int color = defaultSharedPreferences.getInt("launcher_background", 0x00000022);
        getWindow().setBackgroundDrawable(new ColorDrawable(color));

        GridView grid = findViewById(R.id.appsList);

        grid.setAdapter(new AppsListAdapter(LauncherActivity.this, getLaunchableApplications()));
    }

    private List<ApplicationInfo> getLaunchableApplications() {
        List<ApplicationInfo> installedApplications = new ArrayList<>();

        for (ApplicationInfo a : getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA)) {
            if (getPackageManager().getLaunchIntentForPackage(a.packageName) != null) {
                installedApplications.add(a);
            }
        }

        return installedApplications;
    }
}
