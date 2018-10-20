package thanhnguyen.com.familysharinglocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by THANHNGUYEN on 12/16/17.
 */

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

            if(user!=null){


                DatabaseReference db = rootRef.child(user.getUid()).child("setting").child("islocationservicerunning");
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        db.removeEventListener(this);

                        if(dataSnapshot.exists()){

                            boolean islocationservicerunning = (boolean) dataSnapshot.getValue();

                            if(islocationservicerunning){

                                context.startService(new Intent(context, LocationNotifyService.class));

                            }


                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }




    }
}
