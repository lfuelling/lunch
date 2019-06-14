package sh.lrk.lunch.app.settings;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment;
import com.github.danielnilsson9.colorpickerview.preference.ColorPreference;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import androidx.appcompat.app.ActionBar;
import sh.lrk.lunch.R;


public class SettingsActivity extends AppCompatPreferenceActivity implements ColorPickerDialogFragment.ColorPickerDialogListener {

    public static final String TAG = SettingsActivity.class.getCanonicalName();

    public static final String KEY_LAUNCHER_BACKGROUND = "launcher_background";
    public static final String KEY_LAUNCHER_TEXT_COLOR = "launcher_text_color";
    public static final String KEY_SHOW_ALL_APPS = "show_all_apps";
    public static final String KEY_BLACK_APPS_BTN = "black_apps_icon";
    public static final String KEY_APP_ICON_TYPE = "app_icon_type";
    public static final String KEY_SWIPE_INSTEAD_OF_PRESS = "swipe_instead_of_press";

    public static final int DEFAULT_LAUNCHER_BACKGROUND = 0x00000022;
    public static final int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;
    public static final String DEFAULT_APP_ICON_TYPE = "0";
    public static final int BACKGROUND_DIALOG_ID = 2;
    public static final int TEXT_COLOR_DIALOG_ID = 1;

    private static ColorPreference backgroundColorPreference;
    private static ColorPreference textColorPreference;
    private static AtomicReference<ColorPickerDialogFragment> backgroundColorPickerRef;
    private static AtomicReference<ColorPickerDialogFragment> textColorPickerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || AdvancedPreferenceFragment.class.getName().equals(fragmentName)
                || CustomizationPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        if (dialogId == BACKGROUND_DIALOG_ID) {
            backgroundColorPreference.saveValue(color);
        } else if (dialogId == TEXT_COLOR_DIALOG_ID) {
            textColorPreference.saveValue(color);
        } else {
            Log.w(TAG, "Unknown dialogId!");
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        if (dialogId == BACKGROUND_DIALOG_ID) {
            backgroundColorPickerRef.get().dismiss();
        } else if (dialogId == TEXT_COLOR_DIALOG_ID) {
            textColorPickerRef.get().dismiss();
        } else {
            Log.w(TAG, "Unknown dialogId!");
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class CustomizationPreferenceFragment extends PreferenceFragment {

        private SharedPreferences sharedPreferences;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_customization);
            setHasOptionsMenu(true);

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());

            buildTextColorPreference();
            buildBackgroundColorPreference();
        }

        private void buildBackgroundColorPreference() {
            backgroundColorPreference = (ColorPreference) findPreference(KEY_LAUNCHER_BACKGROUND);
            int backgroundColor = sharedPreferences.getInt(KEY_LAUNCHER_BACKGROUND, DEFAULT_LAUNCHER_BACKGROUND);
            backgroundColorPickerRef = new AtomicReference<>(ColorPickerDialogFragment.newInstance(BACKGROUND_DIALOG_ID,
                    backgroundColorPreference.getKey(),
                    getString(R.string.okay),
                    backgroundColor,
                    true));
            backgroundColorPreference.setOnShowDialogListener((title, color) ->
                    backgroundColorPickerRef.get().show(getFragmentManager(), title));
        }

        private void buildTextColorPreference() {
            textColorPreference = (ColorPreference) findPreference(KEY_LAUNCHER_TEXT_COLOR);
            int textColor = sharedPreferences.getInt(KEY_LAUNCHER_TEXT_COLOR, DEFAULT_TEXT_COLOR);
            textColorPickerRef = new AtomicReference<>(ColorPickerDialogFragment.newInstance(TEXT_COLOR_DIALOG_ID,
                    textColorPreference.getKey(),
                    getString(R.string.okay),
                    textColor,
                    true));
            textColorPreference.setOnShowDialogListener((title, color) ->
                    textColorPickerRef.get().show(getFragmentManager(), title));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AdvancedPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_advanced);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
