package thanhnguyen.com.familysharinglocation;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child(user.getUid()).child("setting").child("mynotienable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean value = (boolean) dataSnapshot.getValue();
                if(value){

                    // Check if message contains a notification payload.

                    if(remoteMessage.getNotification()!=null){


                    }

                    // Check if message contains a data payload.

                    if (remoteMessage.getData().size() > 0) {


                        if(remoteMessage.getData().containsKey("message") &&
                                remoteMessage.getData().containsKey("username")    ) {

                            String usernameid= remoteMessage.getData().get("usernameid");

                            if(usernameid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                                return;
                            }

                            sendNotification(remoteMessage.getData().get("message"),
                                    remoteMessage.getData().get("username"));
                        } else if(remoteMessage.getData().containsKey("message") &&
                                remoteMessage.getData().containsKey("detailaddress")){


                            String usernameid= remoteMessage.getData().get("usernameid");

                            if(usernameid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                                return;
                            }


                            sendNotificationPlacesUpdate(remoteMessage.getData().get("message"),
                                    remoteMessage.getData().get("detailaddress"));

                        }



                    };



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    private void sendNotification(String messageBody, String username) {
        Intent intent = new Intent(this, LoginPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.app_name);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId )
                        .setSmallIcon(R.drawable.familyicon)
                        .setContentTitle("Message from " + username)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(generateRandom()
                /* ID of notification */, notificationBuilder.build());
    }
    private void sendNotificationPlacesUpdate(String messageBody, String address) {
        Intent intent = new Intent(this, LoginPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.app_name);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.familyicon)
                        .setContentTitle(messageBody)
                        .setContentText(address)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(generateRandom()
                /* ID of notification */, notificationBuilder.build());
    }



    public int generateRandom(){
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }
}