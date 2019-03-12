package sh.lrk.lunch.activities.launcher;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.core.view.GestureDetectorCompat;
import sh.lrk.lunch.R;
import sh.lrk.lunch.activities.GestureRespondingAppCompatActivity;
import sh.lrk.lunch.activities.main.SwipeGestureDetector;

import static sh.lrk.lunch.activities.settings.SettingsActivity.DEFAULT_LAUNCHER_BACKGROUND;
import static sh.lrk.lunch.activities.settings.SettingsActivity.KEY_BLACK_APPS_BTN;
import static sh.lrk.lunch.activities.settings.SettingsActivity.KEY_LAUNCHER_BACKGROUND;
import static sh.lrk.lunch.activities.settings.SettingsActivity.KEY_SHOW_ALL_APPS;

public class LauncherActivity extends GestureRespondingAppCompatActivity {

    private static final String TAG = LauncherActivity.class.getCanonicalName();
    private SharedPreferences defaultSharedPreferences;
    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        RelativeLayout launcherView = findViewById(R.id.launcher_view);
        launcherView.setPadding(0, getStatusBarHeight(), 0, 0);

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int color = defaultSharedPreferences.getInt(KEY_LAUNCHER_BACKGROUND, DEFAULT_LAUNCHER_BACKGROUND);
        getWindow().setBackgroundDrawable(new ColorDrawable(color));

        SwipeGestureDetector gestureDetector = new SwipeGestureDetector(this);
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureDetector);

        ImageView swipeDownHint = findViewById(R.id.swipeDownHint);
        boolean useBlackAppsBtn = defaultSharedPreferences.getBoolean(KEY_BLACK_APPS_BTN, false);

        if(useBlackAppsBtn) {
            swipeDownHint.setImageDrawable(getDrawable(R.drawable.ic_expand_more_black_24dp));
        } else {
            swipeDownHint.setImageDrawable(getDrawable(R.drawable.ic_expand_more_white_24dp));
        }

        GridView grid = findViewById(R.id.appsList);
        grid.setAdapter(new AppsListAdapter(LauncherActivity.this, getLaunchableApplications()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetectorCompat != null) {
            gestureDetectorCompat.onTouchEvent(event);
            return true;
        }
        return false;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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

    @Override
    public void handleDownSwipe() {
        finish();
    }

    @Override
    public void handleUpSwipe() {
        Log.d(TAG, "Nothing to do on up swipe!");
    }

    @Override
    public void handleLongPress() {
        Log.d(TAG, "Nothing to do on long press!");
    }
}
