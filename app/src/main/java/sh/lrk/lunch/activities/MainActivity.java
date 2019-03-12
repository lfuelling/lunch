package sh.lrk.lunch.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import sh.lrk.lunch.R;
import sh.lrk.lunch.activities.launcher.LauncherActivity;

public class MainActivity extends Activity {

    public static final int REQUEST_CODE = 1; // apparently this can be any int > 0 (?)
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        imageButton = findViewById(R.id.launcherButton);
        imageButton.setOnClickListener(v -> {
            imageButton.setVisibility(View.INVISIBLE);
            startActivityForResult(new Intent(getApplicationContext(), LauncherActivity.class), REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && imageButton != null) {
            imageButton.setVisibility(View.VISIBLE);
        }
    }
}
