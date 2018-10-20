package thanhnguyen.com.familysharinglocation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

/**
 * Created by THANHNGUYEN on 10/22/17.
 */
public class LoginPage extends AppCompatActivity {

    private static TextInputLayout emailid, password;
    private static Button loginButton;
    private static TextView forgotPassword, signUp, signUpNewMember;
    private static CheckBox show_hide_password;
    private FirebaseAuth mAuth;
    RelativeLayout mainlayout;
    MaterialDialog login_dialog;
    String IdToken = "388634886645-qlhfmmnl3t3829fcn3vs45aj03rk3555.apps.googleusercontent.com";
    GoogleSignInOptions gso;
    Button sign_in_button;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN=0;
    int REQUEST_CODE_INTRO=10;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("emailId", emailid.getEditText().getText().toString());
        outState.putString("password", password.getEditText().getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mainlayout = (RelativeLayout) findViewById(R.id.mainlayout);

        mAuth = FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser() != null) {


            if(mAuth.getCurrentUser().isEmailVerified()) {
               // startService(new Intent(getBaseContext(), LocationNotifyService.class));
                startActivity(new Intent(getBaseContext(), ShowFamilyPositionOnMap.class));
                finish();


            }
        }





            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(IdToken)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            sign_in_button = (Button) findViewById(R.id.sign_in_button);
            emailid = (TextInputLayout) findViewById(R.id.login_emailid);
            password = (TextInputLayout) findViewById(R.id.login_password);
            loginButton = (Button) findViewById(R.id.loginBtn);
            forgotPassword = (TextView) findViewById(R.id.forgot_password);
            signUp = (TextView) findViewById(R.id.createAccount);
            show_hide_password = (CheckBox) findViewById(R.id.show_hide_password);

            if (savedInstanceState != null) {
                String emaild_text = savedInstanceState.getString("emailId");
                String password_text = savedInstanceState.getString("password");
                String username_text = savedInstanceState.getString("username");
                emailid.getEditText().setText(emaild_text);
                password.getEditText().setText(password_text);

            }

            sign_in_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    login_dialog = new MaterialDialog.Builder(LoginPage.this)
                            .title("Sign in process")
                            .content("Please wait ...")
                            .progress(true, 0)
                            .show();

                }
            });
            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(getBaseContext(), ForgetPasswordActivity.class));
                }
            });
            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finish();

                    startActivity(new Intent(getBaseContext(), SignupActivity.class));
                }
            });

            show_hide_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        show_hide_password.setText(R.string.hide_pwd);
                        password.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
                        password.getEditText().setTransformationMethod(HideReturnsTransformationMethod
                                .getInstance());

                    } else {

                        show_hide_password.setText(R.string.show_pwd);// change
                        // checkbox
                        // text

                        password.getEditText().setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        password.getEditText().setTransformationMethod(PasswordTransformationMethod
                                .getInstance());// hide password
                    }
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    emailid.setErrorEnabled(false);
                    password.setErrorEnabled(false);
                    emailid.setError("");
                    password.setError("");

                    if (emailid.getEditText().getText().toString().isEmpty() ||
                            password.getEditText().getText().toString().isEmpty()


                            ) {

                        Snackbar sb = Snackbar.make(mainlayout, "All fields are required", Snackbar.LENGTH_SHORT);
                        View snackBarView = sb.getView();
                        snackBarView.setBackgroundColor(ContextCompat.getColor(LoginPage.this, R.color.material_green_900));
                        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        sb.show();

                        return;
                    }

                    if (!AppConstants.isValidEmail(emailid.getEditText().getText().toString())) {
                        emailid.setErrorEnabled(true);
                        emailid.setError("Your Email Id is Invalid");
                    }

                    if (password.getEditText().getText().toString().length() < 6) {
                        password.setErrorEnabled(true);
                        password.setError("Password minimum of 6 chars, please check again!");


                    } else if (AppConstants.isValidEmail(emailid.getEditText().getText().toString())

                            & !(password.getEditText().getText().toString().length() < 6)) {


                        login_dialog = new MaterialDialog.Builder(LoginPage.this)
                                .title("Sign in process")
                                .content("Please wait ...")
                                .progress(true, 0)
                                .show();


                        mAuth.signInWithEmailAndPassword(emailid.getEditText().getText().toString(), password.getEditText().getText().toString()).
                                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        login_dialog.dismiss();

                                        if (task.isSuccessful()) {

                                            if (mAuth.getCurrentUser().isEmailVerified()) {

                                                startActivity(new Intent(getBaseContext(), ShowFamilyPositionOnMap.class));
                                                finish();



                                            /*FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            final DatabaseReference rootdatabase= database.getReference().child(mAuth.getCurrentUser().getUid());
                                            ValueEventListener valuelistener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    for(DataSnapshot p : dataSnapshot.getChildren()){


                                                        String usernametext = p.getKey();

                                                        if(usernametext.equalsIgnoreCase(username.getEditText().getText().toString())){


                                                            preferences.edit().putString("username", usernametext).apply();

                                                            FirebaseMessaging.getInstance().subscribeToTopic(mAuth.getCurrentUser().getUid());

                                                            startActivity(new Intent(getBaseContext(), ShowFamilyPositionOnMap.class));

                                                            finish();

                                                            break;


                                                        }



                                                    }


                                                    rootdatabase.removeEventListener(this);

                                                    Snackbar sb = Snackbar.make(mainlayout, "No user found, please sign up first!", Snackbar.LENGTH_SHORT);
                                                    View snackBarView = sb.getView();
                                                    snackBarView.setBackgroundColor(ContextCompat.getColor(LoginPage.this, R.color.material_green_900));
                                                    TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                                                    textView.setTextColor(Color.WHITE);
                                                    sb.show();









                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            };



                                            rootdatabase.addValueEventListener(valuelistener);
*/

                                                //subscribeToTopic


                                            } else {

                                                Snackbar sb = Snackbar.make(mainlayout, "Check verification sent to your email", Snackbar.LENGTH_SHORT);
                                                View snackBarView = sb.getView();
                                                snackBarView.setBackgroundColor(ContextCompat.getColor(LoginPage.this, R.color.material_green_900));
                                                TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                                                textView.setTextColor(Color.WHITE);
                                                sb.show();


                                                ///note to delete later


                                              //  startActivity(new Intent(getBaseContext(), ShowFamilyPositionOnMap.class));
                                               // finish();


                                            }


                                        } else {

                                            try {

                                                throw task.getException();

                                            } catch (Exception e) {

                                                Snackbar sb = Snackbar.make(mainlayout, task.getException().getMessage(), Snackbar.LENGTH_SHORT);
                                                View snackBarView = sb.getView();
                                                snackBarView.setBackgroundColor(ContextCompat.getColor(LoginPage.this, R.color.material_green_900));
                                                TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                                                textView.setTextColor(Color.WHITE);
                                                sb.show();

                                            }


                                        }


                                    }
                                });


                    }


                }
            });




    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);





            } catch (ApiException e) {

                login_dialog.dismiss();
                MDToast mdToast = MDToast.makeText(getBaseContext(), "Sign in failed, please try again!", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);

                mdToast.show();

            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference ref = rootRef.child(user.getUid());
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    ref.removeEventListener(this);

                                    if(dataSnapshot.exists()){

                                        startActivity(new Intent(getBaseContext(), ShowFamilyPositionOnMap.class));
                                        finish();


                                    } else {

                                        rootRef.child(user.getUid())
                                                .setValue(new UserInfo(acct.getDisplayName(), ""));
                                        rootRef.child(user.getUid()).child("setting").child("invisiblelocation").setValue(false);
                                        rootRef.child(user.getUid()).child("setting").child("maptheme").setValue(1);
                                        rootRef.child(user.getUid()).child("setting").child("gpsinterval").setValue(15000);
                                        rootRef.child(user.getUid()).child("setting").child("gpsaccuracy").setValue(0);
                                        rootRef.child(user.getUid()).child("setting").child("myplaceenable").setValue(false);
                                        rootRef.child(user.getUid()).child("setting").child("mynotienable").setValue(true);
                                        rootRef.child(user.getUid()).child("setting").child("firstlogin").setValue(true);
                                        rootRef.child(user.getUid()).child("setting").child("distanceunit").setValue(0);
                                        rootRef.child(user.getUid()).child("setting").child("firstservicerunningsetposition").setValue(false);


                                       // startService(new Intent(getBaseContext(), LocationNotifyService.class));
                                        startActivity(new Intent(getBaseContext(), ShowFamilyPositionOnMap.class));
                                        finish();



                                    }



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });









                        } else {
                            // If sign in fails, display a message to the user.
                            login_dialog.dismiss();

                            MDToast mdToast = MDToast.makeText(getBaseContext(), "Sign in failed, please try again!", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);

                            mdToast.show();


                        }

                        // ...
                    }
                });
    }

    public void appCreate( ) {



    }



}
