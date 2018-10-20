package thanhnguyen.com.familysharinglocation;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by THANHNGUYEN on 11/16/17.
 */

public class FamilySharingApplicationClass extends MultiDexApplication {

    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
    synchronized public static Tracker getDefaultTracker(Context context) {
        sAnalytics = GoogleAnalytics.getInstance(context);
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }
};

