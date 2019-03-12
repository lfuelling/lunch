package sh.lrk.lunch.activities;

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

import static sh.lrk.lunch.activities.settings.SettingsActivity.KEY_BACKGROUND_URI;

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

        mainView = findViewById(R.id.main_view);

        mainView.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        return true;
                    case R.id.action_background:
                        startActivityForResult(new Intent(getApplicationContext(), BackgroundChooserActivity.class), REQUEST_CODE_BACKGROUND);
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

        setBackgroundIfPossible();
    }

    private void setBackgroundIfPossible() {
        String backgroundUri = defaultSharedPreferences.getString(KEY_BACKGROUND_URI, UNSET);
        if (!UNSET.equals(backgroundUri)) {
            Uri pickedImage = Uri.parse(backgroundUri);
            try (InputStream imageStream = getContentResolver().openInputStream(pickedImage)) {
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                mainView.setBackground(new BitmapDrawable(getResources(), selectedImage));
            } catch (FileNotFoundException e) {
                e.printStackTrace(); //TODO: logging
            } catch (IOException e) {
                e.printStackTrace(); //TODO: logging
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_LAUNCHER && imageButton != null) {
            imageButton.setVisibility(View.VISIBLE);
        } else if (requestCode == REQUEST_CODE_BACKGROUND && mainView != null) {
            setBackgroundIfPossible();
        }
    }
}
