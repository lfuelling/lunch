package sh.lrk.lunch.app.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GestureDetectorCompat;
import sh.lrk.lunch.R;
import sh.lrk.lunch.app.main.home.HomeScreenFragment;
import sh.lrk.lunch.app.main.launcher.LauncherFragment;
import sh.lrk.lunch.app.settings.SettingsActivity;

import static sh.lrk.lunch.app.settings.SettingsActivity.KEY_SWIPE_INSTEAD_OF_PRESS;

public class MainActivity extends AppCompatActivity implements GestureResponder {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private GestureDetectorCompat gestureDetectorCompat;
    private HomeScreenFragment homeScreenFragment;
    private LauncherFragment launcherFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeGestureDetector gestureDetector = new SwipeGestureDetector(this);
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureDetector);
        homeScreenFragment = new HomeScreenFragment();
        launcherFragment = new LauncherFragment();
        fragmentManager = getFragmentManager();

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.frame_container, homeScreenFragment);
        ft.commit();
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
            replaceFragment(launcherFragment);
        } else {
            Log.d(TAG, "Nothing to do on up swipe!");
        }
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
                        startActivity(new Intent(Intent.ACTION_SET_WALLPAPER));
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
        replaceFragment(launcherFragment);
    }
}
