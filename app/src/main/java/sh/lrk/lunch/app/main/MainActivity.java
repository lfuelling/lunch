package sh.lrk.lunch.app.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GestureDetectorCompat;
import sh.lrk.lunch.R;
import sh.lrk.lunch.app.main.home.HomeScreenFragment;
import sh.lrk.lunch.app.main.launcher.AppData;
import sh.lrk.lunch.app.main.launcher.AppFetcherTask;
import sh.lrk.lunch.app.main.launcher.LauncherFragment;
import sh.lrk.lunch.app.settings.SettingsActivity;

import static sh.lrk.lunch.app.main.launcher.LauncherFragment.KEY_APP_VECTOR;
import static sh.lrk.lunch.app.settings.SettingsActivity.KEY_SHOW_ALL_APPS;
import static sh.lrk.lunch.app.settings.SettingsActivity.KEY_SWIPE_INSTEAD_OF_PRESS;

public class MainActivity extends AppCompatActivity implements GestureResponder {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private GestureDetectorCompat gestureDetectorCompat;
    private HomeScreenFragment homeScreenFragment;
    private LauncherFragment launcherFragment;
    private FragmentManager fragmentManager;
    private final Vector<AppData> appDataVector = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeGestureDetector gestureDetector = new SwipeGestureDetector(this, getApplicationContext());
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureDetector);
        homeScreenFragment = new HomeScreenFragment();
        fragmentManager = getFragmentManager();

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.frame_container, homeScreenFragment);
        ft.commit();

        Snackbar loadingAppsSnackbar = Snackbar.make(findViewById(R.id.main_container), R.string.loading_applications, Snackbar.LENGTH_INDEFINITE);
        loadingAppsSnackbar.show();

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean showAllApps = defaultSharedPreferences.getBoolean(KEY_SHOW_ALL_APPS, false);
        new AppFetcherTask(getApplicationContext(), appDataVector, showAllApps, a -> {
            Bundle args = new Bundle();
            args.putParcelableArrayList(KEY_APP_VECTOR, new ArrayList<>(a));
            launcherFragment = new LauncherFragment();
            launcherFragment.setArguments(args);
            loadingAppsSnackbar.dismiss();
        }).execute();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.commit();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetectorCompat != null) {
            gestureDetectorCompat.onTouchEvent(event);
            return true;
        }
        return false;
    }

    @Override
    public void handleDownSwipe() {
        replaceFragment(homeScreenFragment);
    }

    @Override
    public void handleUpSwipe() {
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(KEY_SWIPE_INSTEAD_OF_PRESS, true)) {
            showAppDrawer();
        } else {
            Log.d(TAG, "Nothing to do on up swipe!");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            replaceFragment(homeScreenFragment);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void handleLongPress() {
        RelativeLayout mainView = homeScreenFragment.getMainView();
        if (mainView != null) {
            PopupMenu popup = new PopupMenu(this, mainView);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        return true;
                    case R.id.action_background:
                        startActivity(Intent.createChooser(new Intent(Intent.ACTION_SET_WALLPAPER), getString(R.string.choose_wallpaper)));
                        return true;
                    default:
                        return false;
                }
            });
            popup.setGravity(Gravity.START);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.main_menu, popup.getMenu());
            popup.show();
        }
    }

    public void showAppDrawer() {
        if (homeScreenFragment != null) {
            replaceFragment(launcherFragment);
        } else {
            Log.d(TAG, "Launcher not fully initialized!");
        }
    }
}
