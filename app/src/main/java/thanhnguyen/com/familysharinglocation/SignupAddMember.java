package thanhnguyen.com.familysharinglocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by THANHNGUYEN on 10/22/17.
 */
public class SignupAddMember extends Activity {

    private static EditText fullName, emailId, mobileNumber,
            password ;
    private static TextView login;
    private static Button signUpButton;
    private static CheckBox terms_conditions;
    String getFullName, getEmailId, getMobileNumber, getPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.signup_layout_newmember);
        fullName = (EditText) findViewById(R.id.fullName);
        emailId = (EditText)   findViewById(R.id.userEmailId);
        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        password = (EditText)  findViewById(R.id.password);
        signUpButton = (Button) findViewById(R.id.signUpBtn);
        login = (TextView) findViewById(R.id.already_user);
        terms_conditions = (CheckBox) findViewById(R.id.terms_conditions);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFullName = fullName.getText().toString();
                getEmailId = emailId.getText().toString();
                getMobileNumber = mobileNumber.getText().toString();
                getPassword = password.getText().toString();
                if ( getFullName.equals("") || getFullName.length() == 0
                        || getEmailId.equals("") || getEmailId.length() == 0
                        || getMobileNumber.equals("") || getMobileNumber.length() == 0
                        || getPassword.equals("") || getPassword.length() == 0 ) {

                    Toast.makeText(getBaseContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                    return;

                } else if (!AppConstants.isValidEmail(getEmailId)) {
                    Toast.makeText(getBaseContext(), "Your Email Id is Invalid", Toast.LENGTH_SHORT).show();

                    return;

                } else if(getPassword.length()<6){

                    Toast.makeText(getBaseContext(), "Password minimum of 6 chars, please check again!", Toast.LENGTH_SHORT).show();
                    return;

                }  else if(!terms_conditions.isChecked()){

                    Toast.makeText(getBaseContext(), "Please select Terms and Conditions.!", Toast.LENGTH_SHORT).show();

                    return;


                } else {


                    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(getEmailId, getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                if(mAuth.getCurrentUser().isEmailVerified()){

                                    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                  //  final String userloginname = Settings.Secure.getString(SignupAddMember.this.getContentResolver(), Settings.Secure.ANDROID_ID);

                                    rootRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild(getFullName)){

                                            Toast.makeText(getBaseContext(), "User existed, signup failed", Toast.LENGTH_SHORT).show();


                                        } else {

                                            rootRef.child(mAuth.getCurrentUser().getUid()).child(getFullName)
                                                    .setValue(new UserInfo(getFullName, getMobileNumber));
                                            startActivity(new Intent(getBaseContext(), LoginPage.class));



                                        }


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });




                                }



                            } else {

                                try {

                                    throw task.getException();

                                } catch (Exception e) {

                                    Toast.makeText(getBaseContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }



                            }







                        }
                    });



                }






            }
        });




    }
}

