package thanhnguyen.com.familysharinglocation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.text.InputType;
import android.view.View;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

/**
 * Created by THANHNGUYEN on 12/10/17.
 */

public class AccountSetup extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appsettingpreference);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //add a fragment
        AccountFragment myFragment = new AccountFragment();
        fragmentTransaction.add(R.id.settinglayout, myFragment);
        fragmentTransaction.commit();


    }

    public static class  AccountFragment extends PreferenceFragmentCompat {
        private DatabaseReference rootRef;
        private FirebaseUser user;
        ProgressBar progressbar;

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            // Load the Preferences from the XML file
            addPreferencesFromResource(R.xml.accountsetup_preferences);

            progressbar= (ProgressBar) getActivity().findViewById(R.id.processbar);


            rootRef = FirebaseDatabase.getInstance().getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();

            PreferenceScreen change_username = (PreferenceScreen) findPreference("change_username");
            change_username.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    DatabaseReference ref = rootRef.child(user.getUid()).child("username");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            ref.removeEventListener(this);

                            new MaterialDialog.Builder(getActivity())
                                    .content("Change my account usename")
                                    .inputType(InputType.TYPE_CLASS_TEXT)
                                    .input(dataSnapshot.getValue().toString(), "", new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog dialog, CharSequence input) {

                                            if(!input.toString().isEmpty()){

                                                rootRef.child(user.getUid()).child("username").setValue(input.toString());

                                            }

                                        }
                                    }).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                    return false;
                }
            });
            PreferenceScreen change_phone = (PreferenceScreen) findPreference("change_phone");
            change_phone.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    DatabaseReference ref = rootRef.child(user.getUid()).child("usermobilenumber");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            ref.removeEventListener(this);

                            new MaterialDialog.Builder(getActivity())
                                    .content("Change my phone number")
                                    .inputType(InputType.TYPE_CLASS_PHONE)
                                    .input(dataSnapshot.getValue().toString(), "", new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog dialog, CharSequence input) {

                                            if(!input.toString().isEmpty()){

                                                rootRef.child(user.getUid()).child("usermobilenumber").setValue(input.toString());

                                            }

                                        }
                                    }).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                    return false;
                }
            });




            PreferenceScreen delele_account = (PreferenceScreen) findPreference("delele_account");
            delele_account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    MaterialDialog dialog = new MaterialDialog.Builder(AccountFragment.this.getActivity())
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    dialog.dismiss();
                                }
                            })
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    View view = dialog.getCustomView();
                                    TextInputLayout login_emailid = (TextInputLayout) view.findViewById(R.id.login_emailid);
                                    TextInputLayout login_password = (TextInputLayout) view.findViewById(R.id.login_password);

                                    login_emailid.setErrorEnabled(false);
                                    login_password.setErrorEnabled(false);
                                    login_emailid.setError("");
                                    login_password.setError("");


                                    if(login_emailid.getEditText().getText().toString().isEmpty() ||
                                            login_password.getEditText().getText().toString().isEmpty()){

                                        if(login_emailid.getEditText().getText().toString().isEmpty()){

                                            login_emailid.setErrorEnabled(true);

                                            login_emailid.setError("Your Email is required");

                                        }
                                        if(login_password.getEditText().getText().toString().isEmpty()){

                                            login_password.setErrorEnabled(true);

                                            login_password.setError("Your password is required");


                                        }


                                    } else {


                                        if(!login_emailid.getEditText().getText().toString()
                                            .equalsIgnoreCase(user.getEmail())){

                                            login_emailid.setErrorEnabled(true);

                                            login_emailid.setError("Your email doesn't match this user email");

                                            return;



                                        }

                                        progressbar.setVisibility(View.VISIBLE);

                                        AuthCredential credential = EmailAuthProvider
                                                .getCredential(login_emailid.getEditText().getText().toString(),
                                                        login_password.getEditText().getText().toString());

                                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){

                                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if(task.isSuccessful()){


                                                                getActivity().stopService(new Intent(getActivity(),  LocationNotifyService.class));

                                                                rootRef.child(user.getUid()).removeValue();

                                                                ActivityCompat.finishAffinity(getActivity());

                                                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                                                intent.addCategory(Intent.CATEGORY_HOME);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                startActivity(intent);


                                                            }

                                                        }
                                                    });




                                                } else {


                                                    login_emailid.setErrorEnabled(true);

                                                    login_emailid.setError("Login failed, try again");

                                                    progressbar.setVisibility(View.INVISIBLE);


                                                }





                                            }
                                        });






                                    }






                                }
                            })
                            .title("Delete account. Log in again!")
                            .customView(R.layout.deleteaccount_custom_dialog, true)
                            .positiveText("Agree")
                            .negativeText("Disagree")
                            .autoDismiss(false)
                            .show();



                    return false;
                }
            });


            PreferenceScreen change_userpass = (PreferenceScreen) findPreference("change_userpass");
            change_userpass.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    MaterialDialog dialog = new MaterialDialog.Builder(AccountFragment.this.getActivity())
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    dialog.dismiss();
                                }
                            })
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    View view = dialog.getCustomView();
                                    TextInputLayout login_emailid = (TextInputLayout) view.findViewById(R.id.login_emailid);
                                    TextInputLayout oldpass = (TextInputLayout) view.findViewById(R.id.oldpass);
                                    TextInputLayout newpass = (TextInputLayout) view.findViewById(R.id.newpass);
                                    TextInputLayout newpass_again = (TextInputLayout) view.findViewById(R.id.newpass_again);

                                    oldpass.setErrorEnabled(false);
                                    newpass.setErrorEnabled(false);
                                    newpass_again.setErrorEnabled(false);
                                    login_emailid.setErrorEnabled(false);
                                    oldpass.setError("");
                                    newpass.setError("");
                                    newpass_again.setError("");
                                    login_emailid.setError("");


                                    if(oldpass.getEditText().getText().toString().isEmpty() ||
                                            newpass.getEditText().getText().toString().isEmpty() ||
                                            newpass_again.getEditText().getText().toString().isEmpty() ||
                                            login_emailid.getEditText().getText().toString().isEmpty()

                                            ){

                                        if(login_emailid.getEditText().getText().toString().isEmpty()){

                                            login_emailid.setErrorEnabled(true);

                                            login_emailid.setError("Your email is required");

                                        }

                                        if(oldpass.getEditText().getText().toString().isEmpty()){

                                            oldpass.setErrorEnabled(true);

                                            oldpass.setError("Old password is required");

                                        }
                                        if(newpass.getEditText().getText().toString().isEmpty()){

                                            newpass.setErrorEnabled(true);

                                            newpass.setError("New  password is required");


                                        }
                                        if(newpass_again.getEditText().getText().toString().isEmpty()){

                                            newpass_again.setErrorEnabled(true);

                                            newpass_again.setError("Retype some password is required");

                                        }


                                    } else {


                                        if(!newpass.getEditText().getText().toString().
                                                equals(newpass_again.getEditText().getText().toString())){

                                            newpass.setErrorEnabled(true);

                                            newpass.setError("Your two new  password doesn' match");

                                        } else {

                                            if (newpass.getEditText().getText().toString().length() < 6) {
                                                newpass.setErrorEnabled(true);
                                                newpass.setError("Password minimum of 6 chars, please check again!");
                                                return;

                                            }

                                                progressbar.setVisibility(View.VISIBLE);
                                                AuthCredential credential = EmailAuthProvider
                                                    .getCredential(login_emailid.getEditText().getText().toString(),
                                                            oldpass.getEditText().getText().toString());

                                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful()){

                                                        user.updatePassword(newpass.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                if(task.isSuccessful()){

                                                                    MDToast mdToast = MDToast.makeText(getContext(), "Update password successfully, please log in app again!", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);

                                                                    mdToast.show();

                                                                    FirebaseAuth.getInstance().signOut();

                                                                    ActivityCompat.finishAffinity(getActivity());

                                                                    Intent intent = new Intent(getContext(), LoginPage.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(intent);


                                                                } else {

                                                                    login_emailid.setErrorEnabled(true);

                                                                    login_emailid.setError("Error occured, try again!");


                                                                }


                                                            }
                                                        });

                                                    } else {


                                                        login_emailid.setErrorEnabled(true);

                                                        login_emailid.setError("Login failed, try again");
                                                        progressbar.setVisibility(View.INVISIBLE);


                                                    }





                                                }
                                            });



                                        }


                                    }


                                }
                            })
                            .title("Change password. Type your old and new pass!")
                            .customView(R.layout.changepass_custom_dialog, true)
                            .positiveText("Agree")
                            .negativeText("Disagree")
                            .autoDismiss(false)
                            .show();



                    return false;
                }
            });






        }
    }
}
