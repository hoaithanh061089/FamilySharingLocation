package thanhnguyen.com.familysharinglocation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by THANHNGUYEN on 10/22/17.
 */
public class SignupActivity extends AppCompatActivity {

    private TextInputLayout fullName, emailId, mobileNumber, password, confirmPassword;
    private static TextView login;
    private static Button signUpButton;
    private static CheckBox terms_conditions;
    String getFullName, getEmailId, getMobileNumber, getPassword, getConfirmPassword;
    FirebaseAuth auth;
    LinearLayout mainlayout;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("fullName", fullName.getEditText().getText().toString());
        outState.putString("emailId", emailId.getEditText().getText().toString());
        outState.putString("mobileNumber", mobileNumber.getEditText().getText().toString());
        outState.putString("password", password.getEditText().getText().toString());
        outState.putString("confirmPassword", confirmPassword.getEditText().getText().toString());
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.signup_layout);
        mainlayout = (LinearLayout) findViewById(R.id.mainlayout);
        fullName = (TextInputLayout) findViewById(R.id.fullName);
        emailId = (TextInputLayout) findViewById(R.id.userEmailId);
        mobileNumber = (TextInputLayout) findViewById(R.id.mobileNumber);
        password = (TextInputLayout) findViewById(R.id.password);
        confirmPassword = (TextInputLayout) findViewById(R.id.confirmPassword);
        signUpButton = (Button) findViewById(R.id.signUpBtn);
        login = (TextView) findViewById(R.id.already_user);
        terms_conditions = (CheckBox) findViewById(R.id.terms_conditions);
        if(savedInstanceState!=null)
        {
            getFullName = savedInstanceState.getString("fullName");
            getEmailId = savedInstanceState.getString("emailId");
            getMobileNumber = savedInstanceState.getString("mobileNumber");
            getPassword = savedInstanceState.getString("password");
            getConfirmPassword = savedInstanceState.getString("confirmPassword");

            fullName.getEditText().setText(getFullName);
            emailId.getEditText().setText(getEmailId);
            mobileNumber.getEditText().setText(getMobileNumber);
            password.getEditText().setText(getPassword);
            confirmPassword.getEditText().setText(getConfirmPassword );
        }



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

                startActivity(new Intent(SignupActivity.this, LoginPage.class));


            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //clear empty space after clearing the error

                emailId.setErrorEnabled(false);
                password.setErrorEnabled(false);
                confirmPassword.setErrorEnabled(false);
                emailId.setError("");
                password.setError("");
                confirmPassword.setError("");

                getFullName = fullName.getEditText().getText().toString();
                getEmailId = emailId.getEditText().getText().toString();
                getMobileNumber = mobileNumber.getEditText().getText().toString();
                getPassword = password.getEditText().getText().toString();
                getConfirmPassword = confirmPassword.getEditText().getText().toString();

                if (getFullName.equals("") || getFullName.length() == 0
                        || getEmailId.equals("") || getEmailId.length() == 0
                        || getMobileNumber.equals("") || getMobileNumber.length() == 0
                        || getPassword.equals("") || getPassword.length() == 0
                        || getConfirmPassword.equals("") || getConfirmPassword.length() == 0) {

                    Snackbar sb = Snackbar.make(mainlayout, "All fields are required", Snackbar.LENGTH_SHORT);
                    View snackBarView = sb.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(SignupActivity.this, R.color.material_red_300));
                    TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.BLACK);
                    sb.show();


                    return;

                }
                if (!AppConstants.isValidEmail(getEmailId)) {

                    emailId.setErrorEnabled(true);

                    emailId.setError("Your Email Id is Invalid");


                    //Toast.makeText(getBaseContext(), "Your Email Id is Invalid", Toast.LENGTH_SHORT).show();

                }
                if (getPassword.length() < 6) {
                    password.setErrorEnabled(true);

                    password.setError("Password minimum of 6 chars, please check again!");


                }
                if (!getConfirmPassword.equals(getPassword)) {
                    confirmPassword.setErrorEnabled(true);

                    confirmPassword.setError("Both password doesn't match!");



                }
                if (!terms_conditions.isChecked()) {

                    Snackbar.make(mainlayout, "Please select Terms and Conditions.!", Snackbar.LENGTH_SHORT).show();



                } else if(

                        (AppConstants.isValidEmail(getEmailId)) &
                        (!(getPassword.length() < 6)) &
                                (getConfirmPassword.equals(getPassword)) &
                                (terms_conditions.isChecked())



                        ){


                    auth = FirebaseAuth.getInstance();
                    auth.createUserWithEmailAndPassword(getEmailId, getPassword)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                       // String userloginname = Settings.Secure.getString(SignupActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                                        rootRef.child(auth.getCurrentUser().getUid())
                                                .setValue(new UserInfo(getFullName, getMobileNumber));
                                        rootRef.child(auth.getCurrentUser().getUid()).child("setting").child("invisiblelocation").setValue(false);
                                        rootRef.child(auth.getCurrentUser().getUid()).child("setting").child("maptheme").setValue(1);
                                        rootRef.child(auth.getCurrentUser().getUid()).child("setting").child("gpsinterval").setValue(15000);
                                        rootRef.child(auth.getCurrentUser().getUid()).child("setting").child("gpsaccuracy").setValue(0);
                                        rootRef.child(auth.getCurrentUser().getUid()).child("setting").child("myplaceenable").setValue(false);
                                        rootRef.child(auth.getCurrentUser().getUid()).child("setting").child("mynotienable").setValue(true);
                                        rootRef.child(auth.getCurrentUser().getUid()).child("setting").child("firstlogin").setValue(true);
                                        rootRef.child(auth.getCurrentUser().getUid()).child("setting").child("distanceunit").setValue(0);
                                        rootRef.child(auth.getCurrentUser().getUid()).child("setting").child("firstservicerunningsetposition").setValue(false);

                                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignupActivity.this,
                                                            "Verification email sent to " + getEmailId,
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Log.e(TAG, "sendEmailVerification", task.getException());
                                                    Toast.makeText(SignupActivity.this,
                                                            "Failed to send verification email.",
                                                            Toast.LENGTH_SHORT).show();
                                                }


                                            }
                                        });


                                        // Toast.makeText(getBaseContext(), "Signup Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getBaseContext(), LoginPage.class));
                                        finish();


                                    } else {

                                        try {

                                            throw task.getException();

                                        } catch (Exception e) {

                                            Snackbar.make(mainlayout, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();


                                            //    Toast.makeText(getBaseContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                        }


                                    }


                                }
                            });


                }


            }
        });


    }



}




