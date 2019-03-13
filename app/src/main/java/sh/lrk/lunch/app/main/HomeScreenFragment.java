package sh.lrk.lunch.app.main;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import sh.lrk.lunch.R;

import static sh.lrk.lunch.app.settings.SettingsActivity.DEFAULT_APP_ICON_TYPE;
import static sh.lrk.lunch.app.settings.SettingsActivity.KEY_APP_ICON_TYPE;
import static sh.lrk.lunch.app.settings.SettingsActivity.KEY_BLACK_APPS_BTN;
import static sh.lrk.lunch.app.settings.SettingsActivity.KEY_SWIPE_INSTEAD_OF_PRESS;

public class HomeScreenFragment extends Fragment {

    private ImageButton imageButton;
    private SharedPreferences defaultSharedPreferences;
    private boolean useSwipeInsteadOfPress;
    private String chosenAppIconValue;
    private boolean useBlackAppIcon;
    private RelativeLayout mainView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = (RelativeLayout) inflater.inflate(R.layout.fragment_main, container, false);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        imageButton = mainView.findViewById(R.id.launcherButton);
        updatePreferenceValues();
        setAppIconDrawable();
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferenceValues();
        setAppIconDrawable();
    }

    @Nullable
    public RelativeLayout getMainView() {
        return mainView;
    }

    private void setAppIconDrawable() {
        if (getActivity() == null || getContext() == null) {
            throw new IllegalStateException("Activity or Context is null!");
        }
        if (!useSwipeInsteadOfPress) {
            imageButton.setOnClickListener(v -> ((MainActivity) getActivity()).showAppDrawer());
        }

        int appIconType = Integer.parseInt((chosenAppIconValue == null) ? DEFAULT_APP_ICON_TYPE : chosenAppIconValue);

        if (getActivity() == null || getContext() == null) {
            throw new IllegalStateException("Activity or Context is null!");
        }
        switch (appIconType) {
            case 0:
                if (useBlackAppIcon) {
                    imageButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_expand_less_black_24dp));
                } else {
                    imageButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_expand_less_white_24dp));
                }
                break;
            case 2:
                if (useBlackAppIcon) {
                    imageButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_widgets_black_48dp));
                } else {
                    imageButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_widgets_white_48dp));
                }
                break;
            default:
            case 1:
                if (useBlackAppIcon) {
                    imageButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_apps_black_48dp));
                } else {
                    imageButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_apps_white_48dp));
                }
                break;
        }
    }

    private void updatePreferenceValues() {
        useSwipeInsteadOfPress = defaultSharedPreferences.getBoolean(KEY_SWIPE_INSTEAD_OF_PRESS, true);
        chosenAppIconValue = defaultSharedPreferences.getString(KEY_APP_ICON_TYPE, DEFAULT_APP_ICON_TYPE);
        useBlackAppIcon = defaultSharedPreferences.getBoolean(KEY_BLACK_APPS_BTN, false);
    }
}
