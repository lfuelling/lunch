package sh.lrk.lunch.app.main.launcher;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import sh.lrk.lunch.R;
import sh.lrk.lunch.app.main.MainActivity;

import static sh.lrk.lunch.app.settings.SettingsActivity.DEFAULT_LAUNCHER_BACKGROUND;
import static sh.lrk.lunch.app.settings.SettingsActivity.KEY_BLACK_APPS_BTN;
import static sh.lrk.lunch.app.settings.SettingsActivity.KEY_LAUNCHER_BACKGROUND;
import static sh.lrk.lunch.app.settings.SettingsActivity.KEY_SHOW_ALL_APPS;

public class LauncherFragment extends Fragment {

    private static final String TAG = LauncherFragment.class.getCanonicalName();

    private SharedPreferences defaultSharedPreferences;
    private RelativeLayout launcherView;
    private final Vector<AppData> appDataVector = new Vector<>();
    private AppsListAdapter adapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        launcherView.setPadding(0, getStatusBarHeight(), 0, 0);

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());

        int color = defaultSharedPreferences.getInt(KEY_LAUNCHER_BACKGROUND, DEFAULT_LAUNCHER_BACKGROUND);
        launcherView.setBackground(new ColorDrawable(color));

        ImageView swipeDownHint = launcherView.findViewById(R.id.swipeDownHint);
        boolean useBlackAppsBtn = defaultSharedPreferences.getBoolean(KEY_BLACK_APPS_BTN, false);

        if (useBlackAppsBtn) {
            swipeDownHint.setImageDrawable(getActivity().getDrawable(R.drawable.ic_expand_more_black_48dp));
        } else {
            swipeDownHint.setImageDrawable(getActivity().getDrawable(R.drawable.ic_expand_more_white_48dp));
        }

        GridView grid = launcherView.findViewById(R.id.appsList);
        adapter = new AppsListAdapter(getActivity());
        grid.setAdapter(adapter);
        boolean showAllApps = defaultSharedPreferences.getBoolean(KEY_SHOW_ALL_APPS, false);
        new AppFetcherTask(getContext(), appDataVector, showAllApps, adapter).execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        launcherView = (RelativeLayout) inflater.inflate(R.layout.fragment_launcher, container, false);
        return launcherView;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
