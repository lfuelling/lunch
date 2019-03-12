package sh.lrk.lunch.activities.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GestureDetectorCompat;
import sh.lrk.lunch.R;
import sh.lrk.lunch.activities.GestureRespondingAppCompatActivity;
import sh.lrk.lunch.activities.launcher.LauncherActivity;
import sh.lrk.lunch.activities.settings.SettingsActivity;

import static sh.lrk.lunch.activities.settings.SettingsActivity.DEFAULT_APP_ICON_TYPE;
import static sh.lrk.lunch.activities.settings.SettingsActivity.KEY_APP_ICON_TYPE;
import static sh.lrk.lunch.activities.settings.SettingsActivity.KEY_BLACK_APPS_BTN;
import static sh.lrk.lunch.activities.settings.SettingsActivity.KEY_SWIPE_INSTEAD_OF_PRESS;

public class MainActivity extends GestureRespondingAppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();

    public static final int REQUEST_CODE_LAUNCHER = 1; // apparently this can be any int > 0 (?)
    public static final int REQUEST_CODE_BACKGROUND = 2; // apparently this can be any int > 0 (?)
    public static final String UNSET = "UNSET";
    private ImageButton imageButton;
    private RelativeLayout mainView;
    private SharedPreferences defaultSharedPreferences;
    private GestureDetectorCompat gestureDetectorCompat;
    private boolean useSwipeInsteadOfPress;
    private String chosenAppIconValue;
    private boolean useBlackAppIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        updatePreferenceValues();

        imageButton = findViewById(R.id.launcherButton);
        mainView = findViewById(R.id.main_view);

        setAppIconDrawable();

        SwipeGestureDetector gestureDetector = new SwipeGestureDetector(this);
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureDetector);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updatePreferenceValues();
        setAppIconDrawable();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetectorCompat != null) {
            gestureDetectorCompat.onTouchEvent(event);
            return true;
        }
        return false;
    }

    private void showAppMenu() {
        imageButton.setVisibility(View.INVISIBLE);
        startActivityForResult(new Intent(getApplicationContext(), LauncherActivity.class), REQUEST_CODE_LAUNCHER);
    }

    private void setAppIconDrawable() {
        if (!useSwipeInsteadOfPress) {
            imageButton.setOnClickListener(v -> showAppMenu());
        }
        int appIconType = Integer.parseInt((chosenAppIconValue == null) ? DEFAULT_APP_ICON_TYPE : chosenAppIconValue);
        switch (appIconType) {
            case 2:
                if (useBlackAppIcon) {
                    imageButton.setImageDrawable(getDrawable(R.drawable.ic_expand_less_black_24dp));
                } else {
                    imageButton.setImageDrawable(getDrawable(R.drawable.ic_expand_less_white_24dp));
                }
                break;
            case 1:
                if (useBlackAppIcon) {
                    imageButton.setImageDrawable(getDrawable(R.drawable.ic_widgets_black_48dp));
                } else {
                    imageButton.setImageDrawable(getDrawable(R.drawable.ic_widgets_white_48dp));
                }
                break;
            default:
            case 0:
                if (useBlackAppIcon) {
                    imageButton.setImageDrawable(getDrawable(R.drawable.ic_apps_black_48dp));
                } else {
                    imageButton.setImageDrawable(getDrawable(R.drawable.ic_apps_white_48dp));
                }
                break;
        }
    }

    private void updatePreferenceValues() {
        useSwipeInsteadOfPress = defaultSharedPreferences.getBoolean(KEY_SWIPE_INSTEAD_OF_PRESS, true);
        chosenAppIconValue = defaultSharedPreferences.getString(KEY_APP_ICON_TYPE, DEFAULT_APP_ICON_TYPE);
        useBlackAppIcon = defaultSharedPreferences.getBoolean(KEY_BLACK_APPS_BTN, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_LAUNCHER && imageButton != null) {
            imageButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void handleDownSwipe() {
        Log.d(TAG, "Nothing to do on down swipe!");
    }

    @Override
    public void handleUpSwipe() {
        if(useSwipeInsteadOfPress) {
            showAppMenu();
        } else {
            Log.d(TAG, "Nothing to do on up swipe!");
        }
    }

    @Override
    public void handleLongPress() {
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
