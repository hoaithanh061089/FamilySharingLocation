package thanhnguyen.com.familysharinglocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.gc.materialdesign.views.ButtonFlat;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class ShowFamilyPositionOnMap extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int REQUEST_LOCATION = 0;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;
    private static final String TAG = "";
    private GoogleMap mMap;
    private int markerCount =0;
    DatabaseReference ref, groupmember_ref, group_ref;
    GeoFire geoFire;
    DatabaseReference rootRef, userLocationRef;
    FirebaseUser user;
    String username, markerusername;
    TextView lastupdate, username_header;
    Marker mymarker;
    int familymember_count;
    private GoogleApiClient client;
    boolean showMapFirstTime = true;
    // Declare a variable for the cluster manager.
    ClusterManager<MyItem> mClusterManager;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    SlidingUpPanelLayout sliding_layout;
    ImageView locationhistory, weatherview, sharelocation, satelitemapview, callnow;
    Double weatherlatitude;
    Double weatherlongtitude;
    String userid;;
    SharedPreferences sharedpreferences;
    Spinner spinner_nav;
    ArrayList<String> groupnamelist;
    ArrayList<String> groupidlist;
    ValueEventListener  groupmember_event, group_event;
    private HashMap<String, Marker> mHashMap = new HashMap<String, Marker>();
    private HashMap<DatabaseReference, ValueEventListener> mListenerMap = new HashMap<DatabaseReference, ValueEventListener>();
    Tracker mTracker;
    String IdToken = "388634886645-qlhfmmnl3t3829fcn3vs45aj03rk3555.apps.googleusercontent.com";
    GoogleSignInOptions gso;
    SignInButton sign_in_button;
    GoogleSignInClient mGoogleSignInClient;

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //start service at first startup
        //startService(new Intent(this, LocationNotifyService.class));
        FamilySharingApplicationClass.getDefaultTracker(this);
        MobileAds.initialize(this, "ca-app-pub-7878202527387725~8210738184");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
       // mAdView.loadAd(adRequest);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(ShowFamilyPositionOnMap.this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();


        DatabaseReference settings = rootRef.child(user.getUid()).child("setting");
        settings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                settings.removeEventListener(this);

                for(DataSnapshot p : dataSnapshot.getChildren()){

                    String key = p.getKey();
                    final String groupid = getRandomString(6);

                    switch (key){

                        case "firstlogin":

                            boolean value = (boolean) p.getValue();

                                if(value){


                                    rootRef.child(user.getUid()).child("setting").child("firstlogin").setValue(false);


                                    DatabaseReference dtb = rootRef.child(user.getUid()).child("username");
                                    dtb.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            dtb.removeEventListener(this);
                                            String username = (String) dataSnapshot.getValue();
                                            rootRef.child("group").child(groupid).child("groupname").setValue(username + "'s default group");
                                            rootRef.child("group").child(groupid).child("adminuser").setValue(user.getUid());
                                            rootRef.child("group").child(groupid).child("members").child(user.getUid()).child("membername").setValue(username);
                                            rootRef.child(user.getUid()).child("mygroup").child(groupid).child("groupname").setValue(username + "'s default group");
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                }



                            break;



                    }



                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        rootRef.child(user.getUid()).child("mygroup").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    final String groupid = snapshot.getKey();

                    final DatabaseReference ref =  rootRef.child("group").child(groupid);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.child("groupname").exists()){

                                FirebaseMessaging.getInstance().subscribeToTopic(groupid);


                            } else {

                                FirebaseMessaging.getInstance().unsubscribeFromTopic(groupid);

                            }

                            ref.removeEventListener(this);



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });









                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //subribe to all my group topics:








        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });

        groupnamelist = new ArrayList<String>();
        groupidlist = new ArrayList<String>();

        groupnamelist.add("My groups");
        groupidlist.add("My groups");

        spinner_nav = (Spinner) findViewById(R.id.spinner_nav);
        spinner_nav.post(new Runnable() {
            @Override
            public void run() {

                new MaterialShowcaseView.Builder(ShowFamilyPositionOnMap.this)
                        .setTarget(spinner_nav)
                        .setDismissText("GOT IT")
                        .setContentText("Click to choose group from your groups list! ")
                        .singleUse("spinner_nav_id4") // provide a unique ID used to ensure it is only shown once
                        .show();


            }
        });

        final CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(
                getApplicationContext(), groupnamelist);
        spinner_nav.setAdapter(spinAdapter);
        spinner_nav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                if(position==0){

                    return;


                } else {


                    rootRef.child(user.getUid()).child("setting").child("defaultgroup").setValue(groupnamelist.get(position));


                    if(group_ref!=null && group_event!=null) {

                        group_ref.removeEventListener(group_event);

                    }
                    for (java.util.Map.Entry<DatabaseReference, ValueEventListener> entry : mListenerMap.entrySet()) {
                        DatabaseReference ref = entry.getKey();
                        ValueEventListener listener = entry.getValue();
                        if(ref!=null && listener!=null){

                            ref.removeEventListener(listener);

                        }

                    }


                     String groupid = groupidlist.get(position);
                     group_ref = rootRef.child("group").child(groupid).child("members");
                     group_event= new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(!(mMap ==null)){

                                mMap.clear();

                            }


                            for(DataSnapshot p : dataSnapshot.getChildren()) {

                                final String memberid = p.getKey();

                                DatabaseReference member_reference= rootRef.child(memberid);
                                ValueEventListener member_value =   (new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {

                                        if(!dataSnapshot.exists()){

                                            return;

                                        }

                                        member_reference.child("setting").child("invisiblelocation").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot invisiblelocation) {

                                                if(!memberid.equals(user.getUid())){


                                                    if(invisiblelocation.getValue().toString().equals("false")){

                                                        GeoFire geoFire = new GeoFire(rootRef.child(memberid).child("lastlocation"));

                                                        geoFire.getLocation("location", new LocationCallback() {
                                                            @Override
                                                            public void onLocationResult(String key, GeoLocation location) {

                                                                if(location==null){

                                                                    return;
                                                                }

                                                                Marker mMarker = (Marker) mHashMap.get(memberid);


                                                                if(mMarker!=null) {

                                                                    mMarker.remove();
                                                                }

                                                                double la = location.latitude;
                                                                double longti = location.longitude;

                                                                String lastupdate = (String) dataSnapshot.child("lastlocation").child("lastupdate").getValue();
                                                                String name = (String) dataSnapshot.child("username").getValue();
                                                                Marker usermarker = placeMarker(new LatLng(la, longti), name);
                                                                usermarker.setTag(new WeatherClassMarkerHelper(name, la, longti, lastupdate, memberid));

                                                                mHashMap.put(memberid, usermarker );


                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });


                                                    } else {

                                                        Marker mMarker = (Marker) mHashMap.get(memberid);


                                                        if(mMarker!=null) {

                                                            mMarker.remove();
                                                        }



                                                    }






                                                } else {

                                                    GeoFire geoFire = new GeoFire(rootRef.child(memberid).child("lastlocation"));

                                                    geoFire.getLocation("location", new LocationCallback() {
                                                        @Override
                                                        public void onLocationResult(String key, GeoLocation location) {

                                                            if(location==null){

                                                                return;
                                                            }

                                                            Marker mMarker = (Marker) mHashMap.get(memberid);


                                                            if(mMarker!=null) {

                                                                mMarker.remove();
                                                            }

                                                            double la = location.latitude;
                                                            double longti = location.longitude;

                                                            String lastupdate = (String) dataSnapshot.child("lastlocation").child("lastupdate").getValue();
                                                            String name = (String) dataSnapshot.child("username").getValue();

                                                            if(lastupdate==null || name == null){

                                                                return;
                                                            }

                                                            Marker usermarker = placeMarker(new LatLng(la, longti), name);
                                                            usermarker.setTag(new WeatherClassMarkerHelper(name, la, longti, lastupdate, memberid));

                                                            mHashMap.put(memberid, usermarker );


                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });



                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });



                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                                member_reference.addValueEventListener(member_value);
                                mListenerMap.put(member_reference, member_value);

                            }




                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                     group_ref.addValueEventListener(group_event);



                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //keep setting path synchronise
        rootRef.child(user.getUid()).child("setting").keepSynced(true);
        rootRef.child(user.getUid()).child("mygroup").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                groupnamelist.clear();
                groupidlist.clear();
                groupnamelist.add("My groups");
                groupidlist.add("My groups");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    final String groupid = snapshot.getKey();
                    final String groupname = (String) snapshot.child("groupname").getValue();
                    groupnamelist.add(groupname);
                    groupidlist.add(groupid);
                    spinAdapter.notifyDataSetChanged();


                }

                DatabaseReference defaultgroup_re = rootRef.child(user.getUid()).child("setting").child("defaultgroup");
                defaultgroup_re.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        defaultgroup_re.removeEventListener(this);

                        if(dataSnapshot.getValue()==null){

                            return;
                        }

                        String groupname_value = dataSnapshot.getValue().toString();

                        boolean hasvalueinlist = stringContainsItemFromList(groupname_value, groupnamelist);
                        if(hasvalueinlist){

                            int postion = groupnamelist.indexOf(groupname_value);
                            spinner_nav.setSelection(postion);

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                // spinner_nav.setSelection(1);
                //set group by code, default : 1

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //subscribeToTopic

        //FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());


        //Check If Google Services Is Available
        if (getServicesAvailable()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();

        } else {

            MDToast mdToast = MDToast.makeText(getBaseContext(), "Check your google play service", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();
            finish();

        }

        //Create The MapView Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        View headerLayout = navigationView.getHeaderView(0);
        username_header = (TextView) headerLayout.findViewById(R.id.username_header);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                mDrawerLayout.closeDrawers();
                int id = item.getItemId();
                switch (id) {

                    case R.id.navigation_item_1:

                        new MaterialDialog.Builder(ShowFamilyPositionOnMap.this)
                                .content("Send message to other members in my group!")
                                .inputType(InputType.TYPE_CLASS_TEXT)
                                .input("Message", "", new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(MaterialDialog dialog, CharSequence input) {

                                        if(input.toString().isEmpty()){


                                            MDToast mdToast = MDToast.makeText(getBaseContext(), "Message must not be empty", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING);

                                            mdToast.show();

                                            return;

                                        }

                                        DatabaseReference group_ref = rootRef.child(user.getUid()).child("mygroup");
                                        group_ref.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                group_ref.removeEventListener(this);

                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                    final String groupid = snapshot.getKey();

                                                    final DatabaseReference ref =  rootRef.child("group").child(groupid);
                                                    ref.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            if(dataSnapshot.child("groupname").exists()){

                                                                pushMessage(groupid, input.toString());


                                                            }

                                                            ref.removeEventListener(this);


                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });



                                                }


                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                }).show();


                        break;

                    case R.id.navigation_item_2:

                          startActivity(new Intent(getBaseContext(), MyPlaces.class));

                        break;

                    case R.id.navigation_item_3:

                        startActivity(new Intent(getBaseContext(), MyGroup.class));


                        break;
                    case R.id.navigation_item_4:

                        shareContent("Share app with:", "Installing this app to share your real-time location with others.");


                        break;

                    case R.id.navigation_item_rate:

                        try {
                            Uri uri = Uri.parse("market://details?id=" + getPackageName());
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id="
                                            + getPackageName())));
                        }


                        break;

                    case R.id.navigation_item_6:

                        MaterialDialog dialog = new MaterialDialog.Builder(ShowFamilyPositionOnMap.this)
                           .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                if(isMyServiceRunning(LocationNotifyService.class)){

                                    stopService(new Intent(getBaseContext(), LocationNotifyService.class));

                                }
                                FirebaseAuth.getInstance().signOut();
                                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestIdToken(IdToken)
                                        .requestEmail()
                                        .build();
                                mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
                                mGoogleSignInClient.signOut();
                                finish();


                            }
                        })
                                .title("Sign out")
                                .content("Do your want to sign out your account?")
                                .positiveText("Agree")
                                .negativeText("Disagree")
                                .show();



                        break;

                    case R.id.navigation_item_5:

                        startActivity(new Intent(getBaseContext(), AppSettingPreference.class));




/*                        new MaterialDialog.Builder(ShowFamilyPositionOnMap.this)
                                .title("GPS interval")
                                .items(R.array.GPS)
                                .itemsCallbackSingleChoice(sharedpreferences.getInt("gpsintevalposition", 0), new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        if(which==0){

                                            sharedpreferences.edit().putInt("gpsinteval", 15000).commit();
                                            sharedpreferences.edit().putInt("gpsintevalposition", 0).commit();

                                        } else if(which==1){

                                            sharedpreferences.edit().putInt("gpsinteval", 30000).commit();
                                            sharedpreferences.edit().putInt("gpsintevalposition", 1).commit();


                                        } else if(which==2){

                                            sharedpreferences.edit().putInt("gpsinteval", 60000).commit();
                                            sharedpreferences.edit().putInt("gpsintevalposition", 2).commit();



                                        } else if(which==3){

                                            sharedpreferences.edit().putInt("gpsinteval", 180000).commit();
                                            sharedpreferences.edit().putInt("gpsintevalposition", 3).commit();



                                        } else if(which==4){

                                            sharedpreferences.edit().putInt("gpsinteval", 300000).commit();
                                            sharedpreferences.edit().putInt("gpsintevalposition", 4).commit();



                                        } else if(which==5){

                                            sharedpreferences.edit().putInt("gpsinteval", 600000).commit();
                                            sharedpreferences.edit().putInt("gpsintevalposition", 5).commit();


                                        }
                                        else if(which==6){

                                            sharedpreferences.edit().putInt("gpsinteval", 900000).commit();
                                            sharedpreferences.edit().putInt("gpsintevalposition", 6).commit();


                                        }else if(which==7){

                                            sharedpreferences.edit().putInt("gpsinteval", 1200000).commit();
                                            sharedpreferences.edit().putInt("gpsintevalposition", 7).commit();

                                        }

                                        stopService(new Intent(ShowFamilyPositionOnMap.this, LocationNotifyService.class));
                                        startService(new Intent(ShowFamilyPositionOnMap.this, LocationNotifyService.class));


                                        return true;
                                    }
                                }).show();*/

                        break;











                }





                return false;
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        sliding_layout.setTouchEnabled(false);

        callnow = (ImageView) findViewById(R.id.callnow);
        callnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rootRef.child(userid).child("usermobilenumber").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            String number = dataSnapshot.getValue().toString();
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null)));

                        }




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });







            }
        });


        sharelocation = (ImageView) findViewById(R.id.locationsharing);
        sharelocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String googlemaplink = "https://www.google.com/maps/?q=" + String.valueOf(weatherlatitude) + "," + String.valueOf(weatherlongtitude);
                    String content = "Open this link to get lastest location of " + markerusername + ": " + googlemaplink;
                    shareContent("Share location with:", content);

            }
        });



        locationhistory = (ImageView) findViewById(R.id.locationhistory);
        locationhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent intent = new Intent(ShowFamilyPositionOnMap.this, LocationHistory.class).putExtra("userid", userid);

                startActivity(intent);

            }
        });

        lastupdate = (TextView) findViewById(R.id.lastupdate);
        weatherview = (ImageView) findViewById(R.id.weatherview);
        weatherview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ShowFamilyPositionOnMap.this, WeatherActivity.class);
                intent.putExtra("weatherlatitude", String.valueOf(weatherlatitude));
                intent.putExtra("weatherlongtitude", String.valueOf(weatherlongtitude));

                startActivity(intent);




            }
        });



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                lastupdate.setVisibility(View.INVISIBLE);
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                WeatherClassMarkerHelper helper = (WeatherClassMarkerHelper) marker.getTag();
                userid = helper.getUserid();
                markerusername = helper.getUsername();
                weatherlatitude = helper.getLatitude();
                weatherlongtitude = helper.getLongtitude();
                lastupdate.setVisibility(View.VISIBLE);
                lastupdate.setText("Last update on: " + helper.getLastupdate());
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

                return false;
            }
        });

        satelitemapview = (ImageView) findViewById(R.id.satelitemapview);
        satelitemapview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mMap.getMapType()==GoogleMap.MAP_TYPE_NORMAL) {

                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                    satelitemapview.setAlpha(1f);

                } else if(mMap.getMapType()==GoogleMap.MAP_TYPE_SATELLITE){

                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    satelitemapview.setAlpha(0.7f);

                }


            }
        });


        rootRef.child(user.getUid()).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                username = (String) dataSnapshot.getValue();
                username_header.setText(username);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        /*rootRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!(mMap ==null)){

                    mMap.clear();

                }


                familymember_count = (int) dataSnapshot.getChildrenCount();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    final String username_login = String.valueOf(snapshot.getKey());

                    DatabaseReference last_ref =rootRef.child(user.getUid()).child(username_login).child("lastlocation");
                    GeoFire geoFire = new GeoFire(last_ref);
                    geoFire.getLocation("location", new LocationCallback() {

                        @Override
                        public void onLocationResult(String key, GeoLocation location) {

                            if(location==null){

                                return;
                            }

                            double la = location.latitude;
                            double longti = location.longitude;

                            String lastupdate = (String) snapshot.child("lastlocation").child("lastupdate").getValue();
                            String name = (String) snapshot.child("username").getValue();
                            Marker usermarker = placeMarker(new LatLng(la, longti), name);
                            usermarker.setTag(new WeatherClassMarkerHelper(username_login, la, longti, lastupdate));


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });











                   *//* if(!(snapshot.child("lastlocation").child("latitude").exists())){

                        return;
                    }


                    double la = (double) snapshot.child("lastlocation").child("latitude").getValue();


                    if(!(snapshot.child("lastlocation").child("longtitude").exists())){

                        return;
                    }
                    double longti = (double) snapshot.child("lastlocation").child("longtitude").getValue();



                    String lastupdate = (String) snapshot.child("lastlocation").child("lastupdate").getValue();

                    String name = (String) snapshot.child("username").getValue();
*//**//*

                    IconGenerator iconFactory = new IconGenerator(ShowFamilyPositionOnMap.this);
                    iconFactory.setStyle(IconGenerator.STYLE_GREEN);
                    Marker usermarker = addIcon(iconFactory, name, new LatLng(la, longti));

*//**//*

                    Marker usermarker = placeMarker(new LatLng(la, longti), name);
                    usermarker.setTag(new WeatherClassMarkerHelper(androidid_user, la, longti, lastupdate));

*//*

                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

             if ((ActivityCompat.checkSelfPermission(ShowFamilyPositionOnMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission (ShowFamilyPositionOnMap.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))

        {

            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(ShowFamilyPositionOnMap.this,PERMISSIONS, PERMISSION_ALL );


        } else {


                 mMap.setMyLocationEnabled(true);
        }



    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case 1:{

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    mMap.setMyLocationEnabled(true);


                } else {

                    MDToast mdToast = MDToast.makeText(getBaseContext(), "App need location permissions to work properly. Location service stops now.", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                    mdToast.show();

                    stopService(new Intent(getBaseContext(), LocationNotifyService.class));

                    return;

                }



            }






        }



    }

    // Add A Map Pointer To The MAp
   /* public void addMarker(GoogleMap googleMap, double lat, double lon) {

        if (markerCount == 1) {
            animateMarker(mLastLocation, mymarker);
        } else if (markerCount == 0) {
            //Set Custom BitMap for Pointer
            int height = 130;
            int width = 78;
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.icon_car);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            mMap = googleMap;
            LatLng latlong = new LatLng(lat, lon);
            mymarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin3))
                    .icon(BitmapDescriptorFactory.fromBitmap((smallMarker))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));

            //Set Marker Count to 1 after first marker is created
            markerCount = 1;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            //mMap.setMyLocationEnabled(true);
            startLocationUpdates();
        }
    }

*/
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
    }


    public boolean getServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);


        if (isAvailable == ConnectionResult.SUCCESS) {

            return true;
        } else if (api.isUserResolvableError(isAvailable)) {

            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        }

        return false;
    }


    /**
     * LOCATION LISTENER EVENTS
     */

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
//
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isMyServiceRunning(LocationNotifyService.class)){

            startService(new Intent(getBaseContext(), LocationNotifyService.class));

        }


        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {

            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.map),
                    "GPS isn' enabled. Turn GPS on to get location update!", Snackbar.LENGTH_LONG);
            mySnackbar.setActionTextColor(Color.RED);
            mySnackbar.setAction("Turn GPS on", new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                }
            });



            mySnackbar.show();





        }


        FamilySharingApplicationClass.getDefaultTracker(this);
        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        rootRef.child(user.getUid()).child("setting").child("maptheme").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()==null){

                    return;
                }

                String value = dataSnapshot.getValue().toString();

                if(mMap!=null) {

                    switch (value){

                        case "0":

                            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(),R.raw.normal_json));

                            break;

                        case "1":

                            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(),R.raw.retro_json));


                            break;

                        case "2":

                            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(),R.raw.night_json));


                            break;

                        case "3":

                            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(),R.raw.aubergine_json));


                            break;


                    }




                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        stopLocationUpdates();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //Method to display the location on UI
    /*private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {


            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();

                //Add pointer to the map at location
                addMarker(mMap, latitude, longitude);


            } else {

                Toast.makeText(this, "Couldn't get the location. Make sure location is enabled on the device",
                        Toast.LENGTH_SHORT).show();

                startLocationUpdates();
            }

        }
    }
*/

    // Creating google api client object
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    //Creating location request object
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(AppConstants.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(AppConstants.FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(AppConstants.DISPLACEMENT);
    }


    //Starting the location updates
    protected void startLocationUpdates() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(ShowFamilyPositionOnMap.this,PERMISSIONS, PERMISSION_ALL );

        } else {

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, (LocationListener) this);
        }
    }

    //Stopping location updates
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, (LocationListener) this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
       // displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    public void onLocationChanged(Location location) {

         mLastLocation = location;

        if(showMapFirstTime = true) {
            showMapFirstTime=false;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
            mMap.animateCamera(cameraUpdate);


        }



    }


    /*public static void animateMarker(final Location destination, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements ShowFamilyPositionOnMap.LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }


    protected Marker createMarker(double latitude, double longitude, String title, int iconResID) {

        if(mMap==null){

            return null;
        }

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(iconResID)));
    }



    private Marker addIcon(IconGenerator iconFactory, CharSequence text, LatLng position) {

        if(mMap==null){

            return null;
        }
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        return mMap.addMarker(markerOptions);
    }
*/


    private Marker placeMarker(LatLng point, String name){

        // set for small marker
        int radius = 230;
        int stroke = 15;
        float verticalAnchor = 0.944f;


        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap((int) radius, (int) radius + 25, conf);
        Canvas  canvas = new Canvas(bmp);


       // Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.blueflag);

        // creates a centered bitmap of the desired size

        Bitmap bitmap = createImage((int) radius - stroke, (int) radius - stroke, Color.parseColor("#00BFA5"), name);


        bitmap = ThumbnailUtils.extractThumbnail(bitmap, (int) radius - stroke, (int) radius - stroke, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);



        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

        // the triangle laid under the circle
        int pointedness = 40;
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(radius / 2, radius + 20);
        path.lineTo(radius / 2 + pointedness, radius - 10);
        path.lineTo(radius / 2 - pointedness, radius - 10);
        canvas.drawPath(path, paint);

        // gray circle background
        RectF rect = new RectF(0, 0, radius, radius);
        canvas.drawRoundRect(rect, radius / 2, radius / 2, paint);

        // circle photo
        paint.setShader(shader);
        rect = new RectF(stroke, stroke, radius - stroke, radius - stroke);
        canvas.drawRoundRect(rect, 140, 140, paint);

        // add the marker
        return mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromBitmap(bmp)).anchor(0.5f, verticalAnchor));
    }

    public Bitmap createImage(int width, int height, int color, String name) {
        //Paint mTextPaint = new Paint();





        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint2 = new Paint();
        paint2.setColor(color);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint2);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        paint.setTextScaleX(1);

        int mTextWidth, mTextHeight;
        Rect textBounds = new Rect();
        paint.getTextBounds(name, 0, name.length(), textBounds);
        mTextWidth = (int) paint.measureText(name);
        mTextHeight = textBounds.height();

        canvas.drawText(name, width/2 - (mTextWidth / 2f) + 2.5f,
                height/2 + (mTextHeight / 2f), paint);
        return bitmap;
    }

    public void pushMessage(final String topic, final String message ) {

        rootRef.child("API_KEY").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final String API_KEY = (String) dataSnapshot.getValue();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try{

                                    final String topics = "/topics/" + topic;
                                    JSONObject jGcmData = new JSONObject();
                                    JSONObject jData = new JSONObject();
                                    jGcmData.put("to", topics);
                                    jData.put("message", message);
                                    jData.put("username", username);
                                    jData.put("usernameid", user.getUid());
                                    jGcmData.put("data", jData);
                                    URL url = new URL("https://android.googleapis.com/gcm/send");
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    conn.setRequestProperty("Authorization", "key="+ API_KEY);
                                    conn.setRequestProperty("Content-Type", "application/json");
                                    conn.setRequestMethod("POST");
                                    conn.setDoOutput(true);
                                    OutputStream outputStream = conn.getOutputStream();
                                    outputStream.write(jGcmData.toString().getBytes());
                                    conn.getInputStream();




                                }catch (final Exception e){

                                   /* runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            lastupdate.setText(e.toString());

                                        }
                                    });*/


                                }



                            }
                        }).start();



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


    public void shareContent(final String title, String content){

        final Dialog infodialog = new Dialog(ShowFamilyPositionOnMap.this);
        infodialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        infodialog.setContentView(R.layout.shareappcustomlistview);
        infodialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        infodialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((TextView) infodialog.findViewById(R.id.shareapptv)).setText(title);
        final ListView sharelistview  = (ListView) infodialog.findViewById(R.id.shareappcustomlistview);

        ArrayList listitem  = new ArrayList<ShareAppCustomItem> ();


        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        PackageManager pm = getBaseContext().getPackageManager();
        List<ResolveInfo> resInfos = pm.queryIntentActivities(intent, 0);

        for (ResolveInfo resInfo : resInfos) {
            String packageClassName = resInfo.activityInfo.packageName;
            String className = resInfo.activityInfo.name;
            Drawable icon = resInfo.loadIcon(pm);
            CharSequence label = resInfo.loadLabel(pm);
            listitem.add(new ShareAppCustomItem((String) label, icon,  packageClassName, className));


        }
        ShareAppCustomAdapter shareadapter = new ShareAppCustomAdapter(ShowFamilyPositionOnMap.this,  listitem);
        sharelistview.setAdapter(shareadapter);

        com.gc.materialdesign.views.ButtonFlat cancelbutton = (ButtonFlat) infodialog.findViewById(R.id.cancelbutton);
        cancelbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                infodialog.dismiss();


            }
        });
        sharelistview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                ShareAppCustomItem singleitem = (ShareAppCustomItem) sharelistview.getItemAtPosition(arg2);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.setComponent(new ComponentName(singleitem.getPackageClassName(), singleitem.getClassName()));
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        content);

                startActivity(shareIntent);
                infodialog.dismiss();


            }


        });

        infodialog.show();

    };

    public static boolean stringContainsItemFromList(String inputStr, ArrayList<String> items)
    {
        for(int i =0; i < items.size(); i++)
        {
            if(inputStr.contains(items.get(i)));
            {
                return true;
            }
        }
        return false;
    }
    private static String getRandomString(int sizeOfRandomString)
    {

        String ALLOWED_CHARACTERS ="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}






class MyItem implements ClusterItem {
    private final LatLng mPosition;

    public MyItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }




}
