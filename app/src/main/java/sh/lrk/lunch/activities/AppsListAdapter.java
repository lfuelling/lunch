package sh.lrk.lunch.activities;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import sh.lrk.lunch.R;

public class AppsListAdapter extends ArrayAdapter<ApplicationInfo> {

    private final LayoutInflater inflater;

    AppsListAdapter(Activity a, List<ApplicationInfo> installedApplications) {
        super(a.getApplicationContext(), R.layout.layout_app_entry, installedApplications);
        inflater = a.getLayoutInflater();
    }

    @androidx.annotation.NonNull
    @Override
    public View getView(int position, @androidx.annotation.Nullable View convertView, @androidx.annotation.NonNull ViewGroup parent) {
        if(convertView != null) {
            return initUi(convertView);
        } else {
            return initUi(inflater.inflate(R.layout.layout_app_entry, parent));
        }
    }

    @androidx.annotation.NonNull
    private View initUi(View view) {
        return view; // TODO implement: init view
    }

}
