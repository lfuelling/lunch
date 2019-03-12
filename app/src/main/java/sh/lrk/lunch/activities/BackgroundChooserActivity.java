package sh.lrk.lunch.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import sh.lrk.lunch.R;

public class BackgroundChooserActivity extends Activity {

    private static final String TAG = BackgroundChooserActivity.class.getCanonicalName();

    public static final int LOAD_IMAGE_RESULTS = 1;

    private Button button;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);

        button = findViewById(R.id.btnPick);
        image = findViewById(R.id.imgView);

        button.setOnClickListener(arg0 -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, LOAD_IMAGE_RESULTS);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        InputStream imageStream = null;

        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == Activity.RESULT_OK && data != null) {
            try {
                Uri pickedImage = data.getData();
                if (pickedImage != null) {
                    imageStream = getContentResolver().openInputStream(pickedImage);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    image.setImageBitmap(selectedImage);

                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                            .edit().putString("background_uri", pickedImage.toString()).apply(); //TODO: extract key to constant
                } else {
                    Log.w(TAG, "Image is null!");
                }
            }

            catch(FileNotFoundException e) {
                e.printStackTrace();
            }

            finally {
                if (imageStream != null) {
                    try {
                        imageStream.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
