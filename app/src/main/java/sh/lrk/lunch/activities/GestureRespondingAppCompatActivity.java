package sh.lrk.lunch.activities;

import androidx.appcompat.app.AppCompatActivity;

public abstract class GestureRespondingAppCompatActivity extends AppCompatActivity {
    public abstract void handleDownSwipe();
    public abstract void handleUpSwipe();
    public abstract void handleLongPress();
}
