package sh.lrk.lunch.app.main.launcher;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class AppData {
    private final Drawable drawable;
    private final String label;
    private final Intent intent;
    private final ApplicationInfo applicationInfo;

    public AppData(Drawable drawable, String label, Intent intent, ApplicationInfo applicationInfo) {
        this.drawable = drawable;
        this.label = label;
        this.intent = intent;
        this.applicationInfo = applicationInfo;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public String getLabel() {
        return label;
    }

    public Intent getIntent() {
        return intent;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }
}