package thanhnguyen.com.familysharinglocation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.SwitchPreferenceCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by THANHNGUYEN on 12/7/17.
 */

public class AppSettingPreference extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FamilySharingApplicationClass.getDefaultTracker(this);
        setContentView(R.layout.appsettingpreference);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //add a fragment
        SettingsFragment myFragment = new SettingsFragment();
        fragmentTransaction.add(R.id.settinglayout, myFragment);
        fragmentTransaction.commit();


    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private DatabaseReference rootRef;
        private FirebaseUser user;

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            // Load the Preferences from the XML file
            addPreferencesFromResource(R.xml.app_preferences);

            rootRef = FirebaseDatabase.getInstance().getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            final ListPreference maptheme = (ListPreference) findPreference("map_theme");
            maptheme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    rootRef.child(user.getUid()).child("setting").child("maptheme").setValue(newValue);

                   // listPreference.setValue(newValue.toString());


                    return false;
                }
            });

            final ListPreference gps_pre = (ListPreference) findPreference("gps_list");
            gps_pre.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    rootRef.child(user.getUid()).child("setting").child("gpsinterval").setValue(newValue);

                    return false;
                }
            });


            final SwitchPreferenceCompat invisible_location = (SwitchPreferenceCompat) findPreference("invisible_location");
            invisible_location.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    rootRef.child(user.getUid()).child("setting").child("invisiblelocation").setValue(newValue);


                    return true;

                    //return false dont work
                    //for ref : https://stackoverflow.com/questions/28565538/switchpreference-not-changing-state-after-setting-onpreferencechangelistener
                }
            });
            final SwitchPreferenceCompat myplace_enable = (SwitchPreferenceCompat) findPreference("myplace_enable");
            myplace_enable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    rootRef.child(user.getUid()).child("setting").child("myplaceenable").setValue(newValue);


                    return true;

                    //return false dont work
                    //for ref : https://stackoverflow.com/questions/28565538/switchpreference-not-changing-state-after-setting-onpreferencechangelistener
                }
            });
            final SwitchPreferenceCompat mynoti_enable = (SwitchPreferenceCompat) findPreference("mynoti_enable");
            mynoti_enable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    rootRef.child(user.getUid()).child("setting").child("mynotienable").setValue(newValue);


                    return true;

                    //return false dont work
                    //for ref : https://stackoverflow.com/questions/28565538/switchpreference-not-changing-state-after-setting-onpreferencechangelistener
                }
            });

            final ListPreference distance_unit = (ListPreference) findPreference("distance_unit");
            distance_unit.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    rootRef.child(user.getUid()).child("setting").child("distanceunit").setValue(newValue);

                    return false;
                }
            });
            final ListPreference gps_accuracy = (ListPreference) findPreference("gps_accuracy");
            gps_accuracy.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    rootRef.child(user.getUid()).child("setting").child("gpsaccuracy").setValue(newValue);


                    return false;
                }
            });

            final PreferenceScreen accountsetup = (PreferenceScreen) findPreference("account_setting");
            accountsetup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {


                        startActivity(new Intent(getContext(), AccountSetup.class));



                    return false;
                }
            });

            rootRef.child(user.getUid()).child("setting").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot p : dataSnapshot.getChildren()){

                        String key = p.getKey();

                        String value = p.getValue().toString();
                        switch (key){

                            case "distanceunit":

                                distance_unit.setValue(value.toString());

                                break;

                            case "gpsaccuracy":

                                gps_accuracy.setValue(value.toString());

                                break;

                            case "gpsinterval":

                                gps_pre.setValue(value.toString());

                                break;

                            case "maptheme":

                                maptheme.setValue(value.toString());

                                break;
                            case "invisiblelocation":

                                invisible_location.setChecked(Boolean.valueOf(value));

                                break;
                            case "myplaceenable":

                                myplace_enable.setChecked(Boolean.valueOf(value));

                                break;
                            case "mynotienable":

                                mynoti_enable.setChecked(Boolean.valueOf(value));

                                break;



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