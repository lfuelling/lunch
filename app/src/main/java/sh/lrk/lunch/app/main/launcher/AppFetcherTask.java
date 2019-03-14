package sh.lrk.lunch.app.main.launcher;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

public class AppFetcherTask extends AsyncTask<Void, Void, List<AppData>> {

    private final PackageManager packageManager;
    private final Vector<AppData> appDataVector;
    private final boolean showAllApps;
    private final AtomicReference<Context> contextRef;
    private final Callback callback;

    public AppFetcherTask(Context context, Vector<AppData> appDataVector, boolean showAllApps, Callback callback) {
        this.packageManager = context.getPackageManager();
        this.appDataVector = appDataVector;
        this.showAllApps = showAllApps;
        this.contextRef = new AtomicReference<>(context);
        this.callback = callback;
    }

    @Override
    protected List<AppData> doInBackground(Void... voids) {
        int tasksStarted = 0;
        for (ApplicationInfo a : packageManager.getInstalledApplications(PackageManager.GET_META_DATA)) {

            if (packageManager.getLaunchIntentForPackage(a.packageName) != null) {
                new AppDataThread(contextRef.get(), a, appDataVector).start();
                tasksStarted++;
            } else if (showAllApps) {
                new AppDataThread(contextRef.get(), a, appDataVector).start();
                tasksStarted++;
            }
        }

        //noinspection StatementWithEmptyBody
        do { /* Wait for other tasks */ } while ((appDataVector.size() + 1) < tasksStarted);
        ArrayList<AppData> list = new ArrayList<>(appDataVector);
        return Collections.unmodifiableList(list);
    }

    @Override
    protected void onPostExecute(List<AppData> appData) {
        callback.call(appData);
    }

    public interface Callback {
        void call(List<AppData> appData);
    }
}
