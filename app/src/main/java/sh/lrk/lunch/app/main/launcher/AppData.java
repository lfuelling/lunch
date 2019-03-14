package sh.lrk.lunch.app.main.launcher;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.DrawableRes;
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

    @DrawableRes
    private final int drawable;
    private final String label;
    private final Intent intent;
    private final ApplicationInfo applicationInfo;

    public AppData(@DrawableRes int drawable, String label, Intent intent, ApplicationInfo applicationInfo) {
        this.drawable = drawable;
        this.label = label;
        this.intent = intent;
        this.applicationInfo = applicationInfo;
    }

    public AppData(String label, Intent intent, ApplicationInfo appInfo) {
        this.label = label;
        this.intent = intent;
        this.applicationInfo = appInfo;
        this.drawable = -1;
    }

    @DrawableRes
    public int getDrawable() throws UseDefaultDrawableException {
        if(drawable == -1) {
            throw new UseDefaultDrawableException();
        }
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
        dest.writeInt(drawable);
    }

    private AppData(Parcel parcel) {
        label = parcel.readString();
        intent = parcel.readTypedObject(Intent.CREATOR);
        applicationInfo = parcel.readTypedObject(ApplicationInfo.CREATOR);
        drawable = parcel.readInt();
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

    class UseDefaultDrawableException extends Exception {
        public UseDefaultDrawableException() {
            super("Use the icon provided by ApplicationInfo!");
        }
    }
}