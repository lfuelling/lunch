package sh.lrk.lunch.activities;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.List;

import sh.lrk.lunch.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView grid = findViewById(R.id.appsList);
        List<ApplicationInfo> installedApplications = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        grid.setAdapter(new AppsListAdapter(installedApplications));
    }
}
