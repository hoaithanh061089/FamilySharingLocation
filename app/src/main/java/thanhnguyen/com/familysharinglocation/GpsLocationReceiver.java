package thanhnguyen.com.familysharinglocation;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

/**
 * Created by THANHNGUYEN on 12/23/17.
 */

public class GpsLocationReceiver extends BroadcastReceiver {

    int notification_id =1;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {


            boolean gpsActionisDoneOnce = sp.getBoolean("gpsActionisDoneOnce", false);


            if(!gpsActionisDoneOnce){

                sp.edit().putBoolean("gpsActionisDoneOnce", true).apply();

                new Handler().postDelayed(new Runnable() {
                    public void run() {

                        sp.edit().putBoolean("gpsActionisDoneOnce", false).apply();
                    }
                }, 5000);


                sendNotificationPlacesUpdate(context, "GPS location service turned off. Please turn it on to get location updated",
                        "Family Location Sharing");

            }


        } else {

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.cancel(notification_id);



        }


    }

    private void sendNotificationPlacesUpdate(Context context, String messageBody, String apptitle) {
        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "Family Location Sharing";
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.familyicon)
                        .setContentTitle(apptitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notification_id
                /* ID of notification */, notificationBuilder.build());
    }

}
