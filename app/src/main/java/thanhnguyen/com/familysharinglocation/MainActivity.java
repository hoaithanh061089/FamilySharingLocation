package thanhnguyen.com.familysharinglocation;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {

    private FirebaseAuth mAuth;
    DatabaseReference rootRef,demoRef;
    EditText editText;
    Button submit,fetch;
    TextView demoValue;
    String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (user != null) {

          //  MainActivity.this.startActivity(new Intent(getBaseContext(), Map.class));


   } else {

            mAuth.createUserWithEmailAndPassword("d@gmail.com", "dddddddd")
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseDatabase.getInstance().getReference().
                                    child(user.getUid()).child(android_id).child("name").setValue("Mr Thanh");



                        }
                    });


        }


        /*editText = (EditText) findViewById(R.id.etValue);
        demoValue = (TextView) findViewById(R.id.tvValue);
        submit = (Button) findViewById(R.id.btnSubmit);
        fetch = (Button) findViewById(R.id.btnFetch);
        //database reference pointing to root of database
        rootRef = FirebaseDatabase.getInstance().getReference();
        //database reference pointing to demo node
        demoRef = rootRef.child("demo");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = editText.getEditableText().toString();
                //push creates a unique id in database
                demoRef.child("value").setValue(value);
                GeoFire geoFire = new GeoFire(demoRef);
                geoFire.setLocation("firebase-hq", new GeoLocation(37.7853889, -122.4056973));
            }
        });
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootRef.child("demo").child("value").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        demoValue.setText(value);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });*/
    }

}


