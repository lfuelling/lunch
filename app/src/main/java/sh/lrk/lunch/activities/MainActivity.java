package sh.lrk.lunch.activities;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import sh.lrk.lunch.R;
import sh.lrk.lunch.activities.launcher.LauncherActivity;
import sh.lrk.lunch.activities.settings.SettingsActivity;

import static sh.lrk.lunch.activities.settings.SettingsActivity.DEFAULT_APP_ICON_TYPE;
import static sh.lrk.lunch.activities.settings.SettingsActivity.KEY_APP_ICON_TYPE;
import static sh.lrk.lunch.activities.settings.SettingsActivity.KEY_BACKGROUND_URI;
import static sh.lrk.lunch.activities.settings.SettingsActivity.KEY_BLACK_APPS_BTN;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_LAUNCHER = 1; // apparently this can be any int > 0 (?)
    public static final int REQUEST_CODE_BACKGROUND = 2; // apparently this can be any int > 0 (?)
    public static final String UNSET = "UNSET";
    private ImageButton imageButton;
    private RelativeLayout mainView;
    private SharedPreferences defaultSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        imageButton = findViewById(R.id.launcherButton);
        imageButton.setOnClickListener(v -> {
            imageButton.setVisibility(View.INVISIBLE);
            startActivityForResult(new Intent(getApplicationContext(), LauncherActivity.class), REQUEST_CODE_LAUNCHER);
        });

        setAppIconDrawable();

        mainView = findViewById(R.id.main_view);

        mainView.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
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
            return true;
        });
    }

    private void setAppIconDrawable() {
        String chosenAppIconValue = defaultSharedPreferences.getString(KEY_APP_ICON_TYPE, DEFAULT_APP_ICON_TYPE);
        int appIconType = Integer.parseInt((chosenAppIconValue == null) ? DEFAULT_APP_ICON_TYPE : chosenAppIconValue);
        boolean useBlackAppIcon = defaultSharedPreferences.getBoolean(KEY_BLACK_APPS_BTN, false);

        switch (appIconType) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_LAUNCHER && imageButton != null) {
            imageButton.setVisibility(View.VISIBLE);
        }
    }
}
