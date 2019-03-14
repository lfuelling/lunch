package sh.lrk.lunch.app.main.launcher;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppData implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public AppData createFromParcel(Parcel in) {
            return new AppData(in);
        }

        public AppData[] newArray(int size) {
            return new AppData[size];
        }
    };

    private final Bitmap drawable;
    private final String label;
    private final Intent intent;
    private final ApplicationInfo applicationInfo;

    public AppData(Bitmap drawable, String label, Intent intent, ApplicationInfo applicationInfo) {
        this.drawable = drawable;
        this.label = label;
        this.intent = intent;
        this.applicationInfo = applicationInfo;
    }

    public Bitmap getDrawable() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeTypedObject(intent, flags);
        dest.writeTypedObject(applicationInfo, flags);
        dest.writeTypedObject(drawable, flags);
    }

    private AppData(Parcel parcel) {
        label = parcel.readString();
        intent = parcel.readTypedObject(Intent.CREATOR);
        applicationInfo = parcel.readTypedObject(ApplicationInfo.CREATOR);
        drawable = parcel.readTypedObject(Bitmap.CREATOR);
    }

    @NonNull
    @Override
    public String toString() {
        return "AppData: [packageName='" + applicationInfo.packageName + "', label='" + label + "']";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof AppData) {
            return applicationInfo.packageName.equals(((AppData) obj).applicationInfo.packageName);
        }
        return false;
    }
}