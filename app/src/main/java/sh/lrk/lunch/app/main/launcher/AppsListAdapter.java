package sh.lrk.lunch.app.main.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import java.util.ArrayList;

import sh.lrk.lunch.R;
import sh.lrk.lunch.app.main.MainActivity;

import static sh.lrk.lunch.app.settings.SettingsActivity.DEFAULT_TEXT_COLOR;
import static sh.lrk.lunch.app.settings.SettingsActivity.DEFAULT_TEXT_SIZE;
import static sh.lrk.lunch.app.settings.SettingsActivity.KEY_LAUNCHER_TEXT_COLOR;
import static sh.lrk.lunch.app.settings.SettingsActivity.KEY_LAUNCHER_TEXT_SIZE;

public class AppsListAdapter extends ArrayAdapter<AppData> {

    private static final String TAG = AppsListAdapter.class.getCanonicalName();

    private final LayoutInflater inflater;
    private final MainActivity activity;
    private final int appTitleColor;
    private final int appTitleSize;

    AppsListAdapter(MainActivity a) {
        super(a, R.layout.layout_app_entry, new ArrayList<>());
        inflater = a.getLayoutInflater();
        activity = a;
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        appTitleColor = defaultSharedPreferences.getInt(KEY_LAUNCHER_TEXT_COLOR, DEFAULT_TEXT_COLOR);
        String textSize = defaultSharedPreferences.getString(KEY_LAUNCHER_TEXT_SIZE, DEFAULT_TEXT_SIZE);
        if (textSize == null) {
            textSize = "2";
        }
        appTitleSize = Integer.parseInt(textSize);
    }

    @androidx.annotation.NonNull
    @Override
    public View getView(int position, @androidx.annotation.Nullable View convertView, @androidx.annotation.NonNull ViewGroup parent) {
        if (convertView != null) {
            return initUi(position, convertView, true);
        } else {
            return initUi(position, inflater.inflate(R.layout.layout_app_entry, parent, false), false);
        }
    }


    @androidx.annotation.NonNull
    private View initUi(int position, View view, boolean isConvertView) {

        TextView appTitle = view.findViewById(R.id.appTitle);
        ImageView appImage = view.findViewById(R.id.appIcon);
        AppData appData = AppsListAdapter.this.getItem(position);

        if (appData == null) {
            return view;
        }

        if (isConvertView && appTitle.getText().equals(appData.getLabel())) {
            return view;
        }

        switch (appTitleSize) {
            case 0: //small
                appTitle.setTextSize(12);
                break;
            case 2: //large
                appTitle.setTextSize(16);
                break;
            case 3: //huge
                appTitle.setTextSize(18);
                break;
            case 1: //medium
            default:
                appTitle.setTextSize(14);
                break;
        }

        appTitle.setTextColor(appTitleColor);

        appTitle.setText(appData.getLabel());
        appImage.setImageBitmap(appData.getDrawable());
        view.setOnClickListener(v -> {
            activity.hideAppDrawer();
            getContext().startActivity(appData.getIntent());
        });
        view.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_uninstall:
                        Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                        uninstallIntent.setData(Uri.parse("package:" + appData.getApplicationInfo().packageName));
                        getContext().startActivity(uninstallIntent);
                        return true;
                    case R.id.action_info:
                        Intent infoIntent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        infoIntent.setData(Uri.parse("package:" + appData.getApplicationInfo().packageName));
                        getContext().startActivity(infoIntent);
                        return true;
                    default:
                        return false;
                }
            });
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.app_menu, popup.getMenu());
            popup.show();
            return true;
        });

        return view;
    }


}
