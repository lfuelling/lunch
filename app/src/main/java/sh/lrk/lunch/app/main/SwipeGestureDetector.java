package sh.lrk.lunch.app.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import static sh.lrk.lunch.app.settings.SettingsActivity.DEFAULT_MAX_SWIPE_DISTANCE;
import static sh.lrk.lunch.app.settings.SettingsActivity.DEFAULT_MIN_SWIPE_DISTANCE;
import static sh.lrk.lunch.app.settings.SettingsActivity.KEY_MAX_SWIPE_DISTANCE;
import static sh.lrk.lunch.app.settings.SettingsActivity.KEY_MIN_SWIPE_DISTANCE;

/**
 * Listener for the app drawer swipes.
 */
public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private static final String TAG = SwipeGestureDetector.class.getCanonicalName();

    // Minimal x and y axis swipe distance.
    private final int MIN_SWIPE_DISTANCE_X;
    private final int MIN_SWIPE_DISTANCE_Y;

    // Maximal x and y axis swipe distance.
    private final int MAX_SWIPE_DISTANCE_X;
    private final int MAX_SWIPE_DISTANCE_Y;

    // Source gestureResponder that display message in text view.
    private final GestureResponder gestureResponder;

    SwipeGestureDetector(@NonNull GestureResponder gestureResponder, @NonNull Context context) {
        this.gestureResponder = gestureResponder;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        MIN_SWIPE_DISTANCE_X = preferences.getInt(KEY_MIN_SWIPE_DISTANCE, DEFAULT_MIN_SWIPE_DISTANCE);
        MIN_SWIPE_DISTANCE_Y = preferences.getInt(KEY_MIN_SWIPE_DISTANCE, DEFAULT_MIN_SWIPE_DISTANCE);

        MAX_SWIPE_DISTANCE_X = preferences.getInt(KEY_MAX_SWIPE_DISTANCE, DEFAULT_MAX_SWIPE_DISTANCE);
        MAX_SWIPE_DISTANCE_Y = preferences.getInt(KEY_MAX_SWIPE_DISTANCE, DEFAULT_MAX_SWIPE_DISTANCE);
    }

    /* This method is invoked when a swipe gesture happened. */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // Get swipe delta value in x axis.
        float deltaX = e1.getX() - e2.getX();

        // Get swipe delta value in y axis.
        float deltaY = e1.getY() - e2.getY();

        // Get absolute value.
        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);

        // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
        if ((deltaXAbs >= MIN_SWIPE_DISTANCE_X) && (deltaXAbs <= MAX_SWIPE_DISTANCE_X)) {
            if (deltaX > 0) {
                Log.d(TAG, "Swipe to left");
            } else {
                Log.d(TAG, "Swipe to right");
            }
        }

        if ((deltaYAbs >= MIN_SWIPE_DISTANCE_Y) && (deltaYAbs <= MAX_SWIPE_DISTANCE_Y)) {
            if (deltaY > 0) {
                this.gestureResponder.handleUpSwipe();
            } else {
                this.gestureResponder.handleDownSwipe();
            }
        }

        return true;
    }

    // Invoked when single tap screen.
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d(TAG, "Single tap occurred.");
        return true;
    }

    // Invoked when double tap screen.
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(TAG, "Double tap occurred.");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        this.gestureResponder.handleLongPress();
    }
}
