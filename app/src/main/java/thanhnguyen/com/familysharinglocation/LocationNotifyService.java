package thanhnguyen.com.familysharinglocation;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LocationNotifyService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    public static Location mCurrentLocation;
    String username;
    DatabaseReference rootRef, userLocationRef;
    FirebaseUser user;
    long date_count_onfirebase;
    ValueEventListener deleteitemlistenerroot, deleteitemlistener;
    public static int UPDATE_INTERVAL = 30000;
    public static int FATEST_INTERVAL = 30000;
    SharedPreferences sharedpreferences;
    int gpsinteval;
    List<PlacesClassHelper> placeList = new ArrayList<>();
    GeoFire geoFire_currentlocation;
    DatabaseReference last_ref;
   // GeoQuery geoQueryNotiUpdate;
   List<Date> dates= new ArrayList<Date>();
   int gpsaccuracy = LocationRequest.PRIORITY_HIGH_ACCURACY;
   boolean firstservicerunningsetposition = false;
   Location mLastLocation;

    @Override
    public void onCreate() {
        if(rootRef==null){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            rootRef= database.getReference();

        }

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        user = FirebaseAuth.getInstance().getCurrentUser();

        last_ref =rootRef.child(user.getUid()).child("lastlocation");
        geoFire_currentlocation = new GeoFire(last_ref);
        rootRef.child(user.getUid()).child("setting").child("islocationservicerunning").setValue(true);

        //Firebase apps automatically handle temporary network interruptions. Cached data is available while offline and Firebase resends any writes when network connectivity is restored.


        DatabaseReference db_service = rootRef.child(user.getUid()).child("setting").child("firstservicerunningsetposition");
        db_service.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                db_service.removeEventListener(this);

                if(dataSnapshot.exists()){

                    firstservicerunningsetposition= (boolean) dataSnapshot.getValue();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });




        rootRef.child(user.getUid()).child("setting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot p : dataSnapshot.getChildren()){

                    String key = p.getKey();

                    String value = p.getValue().toString();
                    switch (key){

                        case "gpsaccuracy":

                            int gpsvalue = Integer.parseInt(value);

                            if(gpsvalue==0){

                                gpsaccuracy =LocationRequest.PRIORITY_HIGH_ACCURACY;


                            } else if(gpsvalue==1){

                                gpsaccuracy =LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

                            } else {

                                gpsaccuracy =LocationRequest.PRIORITY_LOW_POWER;


                            }


                            break;

                        case "gpsinterval":

                            UPDATE_INTERVAL = FATEST_INTERVAL = Integer.parseInt(value);

                            break;


                    }


                    }

                if(mGoogleApiClient!=null){

                    if(mGoogleApiClient.isConnected()){

                        stopLocationUpdates();

                        //connect again;

                        locationSetup();


                    }


                } else {

                    locationSetup();


                }


                    }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

        //if number of days saved on the server exeeds the limit, delete the last one day

        deleteitemlistener = rootRef.child(user.getUid()).child("location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dates.clear();

                if(dataSnapshot.getChildrenCount() > AppConstants.DAYMAXONSERVER){


                    for(DataSnapshot p : dataSnapshot.getChildren()){

                        String childKey = p.getKey();

                        try {
                            dates.add(new SimpleDateFormat("dd-MM-yyyy").parse(childKey));



                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                       // rootRef.child(user.getUid()).child(userloginname).child("location").child(childKey).removeValue();


                    }

                    Collections.sort(dates);

                    String removedate = new SimpleDateFormat("dd-MM-yyyy ").format(dates.get(0));
                    rootRef.child(user.getUid()).child("location").child(removedate).removeValue();



                }






            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //show error dialog if GoolglePlayServices not available
       // locationSetup();

        final DatabaseReference database = rootRef.child(user.getUid()).child("notificationplaces");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                database.removeEventListener(this);
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    final String placename = snapshot.getKey();
                    final String address = (String) snapshot.child("address").getValue();
                    DatabaseReference ref =rootRef.child(user.getUid()).child("notificationplaces");
                    final GeoFire geoFire = new GeoFire(ref);
                    geoFire.getLocation(placename, new LocationCallback() {
                        @Override
                        public void onLocationResult(final String notificationkey, GeoLocation location) {

                            final GeoQuery geoQueryNotiUpdate = geoFire_currentlocation.queryAtLocation(location, 0.3);
                            geoQueryNotiUpdate.addGeoQueryEventListener(new GeoQueryEventListener() {
                                @Override
                                public void onKeyEntered(String key, GeoLocation location) {


                                    if(!snapshot.child("available").exists()){

                                        return;

                                    }

                                    boolean isavailable = (boolean) snapshot.child("available").getValue();

                                    if(isavailable==false){


                                        rootRef.child(user.getUid()).child("notificationplaces").
                                                child(placename).child("available").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                DatabaseReference ref = rootRef.child(user.getUid()).child("setting").child("myplaceenable");
                                                ref.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                                        if(dataSnapshot.getValue()==null){

                                                            return;
                                                        }

                                                        boolean value = (boolean) dataSnapshot.getValue();
                                                        if(value){

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

                                                                                    pushMessage(groupid, username + " has arrived " + notificationkey, address);

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

                                                        ref.removeEventListener(this);



                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });







                                            }
                                        });


                                    }


                                }

                                @Override
                                public void onKeyExited(String key) {

                                    if(!snapshot.child("available").exists()){

                                        return;

                                    }

                                    boolean isavailable = (boolean) snapshot.child("available").getValue();

                                    if(isavailable){



                                        rootRef.child(user.getUid()).child("notificationplaces").
                                                child(placename).child("available").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                             //   pushMessage(username + " has left " + notificationkey, address);

                                                DatabaseReference ref = rootRef.child(user.getUid()).child("setting").child("myplaceenable");
                                                ref.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                                        if(dataSnapshot.getValue()==null){

                                                            return;
                                                        }

                                                        boolean value = (boolean) dataSnapshot.getValue();
                                                        if(value){


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

                                                                                    pushMessage(groupid,username + " has left " + notificationkey, address);


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

                                                        ref.removeEventListener(this);



                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });



                                            }
                                        });


                                    }


                                };

                                @Override
                                public void onKeyMoved(String key, GeoLocation location) {

                                }

                                @Override
                                public void onGeoQueryReady() {

                                }

                                @Override
                                public void onGeoQueryError(DatabaseError error) {

                                }
                            });











                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                   /* double la = (double) snapshot.child("latitude").getValue();
                    double lon = (double) snapshot.child("longtitude").getValue();
                    String add = (String) snapshot.child("address").getValue();
                    PlacesClassHelper a = new PlacesClassHelper(placename, add, la, lon);
                    placeList.add(a);*/


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rootRef.child(user.getUid()).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                username = (String) dataSnapshot.getValue();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);


        if (isAvailable == ConnectionResult.SUCCESS) {

            return true;
        }
        return false;
    }


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


        } else {

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, (LocationListener) this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        String currentime = DateFormat.getTimeInstance().format(new Date());
        String date = new SimpleDateFormat("dd-MM-yyyy ").format(Calendar.getInstance().getTime());
        int speed=0;

        if(location.hasSpeed()){

            speed= (int) (location.getSpeed()*3600)/1000;

        } else {

            if(mLastLocation!=null){


                double elapsedTime = (location.getTime() - mLastLocation.getTime()) / 1000.0; // Convert milliseconds to seconds

                int calculatedSpeed = (int) (mLastLocation.distanceTo(location) / elapsedTime);

                speed = (int) (calculatedSpeed*3600)/1000;


            }


           /* int currentspeed = (int) (Math.sqrt(
                                Math.pow(location.getLongitude() - mLastLocation.getLongitude(), 2)
                                        + Math.pow(location.getLatitude() - mLastLocation.getLatitude(), 2)
                        ) / ((location.getTime() - mLastLocation.getTime())/ 1000.0));
*/


        }

        mLastLocation=location;

        if(speed==0){


            if(firstservicerunningsetposition==false){


                rootRef.child(user.getUid()).child("location").child(date).child(currentime).child("speed").setValue(speed);
                rootRef.child(user.getUid()).child("location").child(date).child(currentime).child("latitude").setValue(location.getLatitude());
                rootRef.child(user.getUid()).child("location").child(date).child(currentime).child("longtitude").setValue(location.getLongitude());
                rootRef.child(user.getUid()).child("setting").child("firstservicerunningsetposition").setValue(true);
                geoFire_currentlocation.setLocation("location", new GeoLocation(location.getLatitude(), location.getLongitude()));
                rootRef.child(user.getUid()).child("lastlocation").child("lastupdate").setValue(date + " " + currentime );
                firstservicerunningsetposition = true;


            } else {

                return;

            }


        }


        rootRef.child(user.getUid()).child("location").child(date).child(currentime).child("speed").setValue(speed);
        rootRef.child(user.getUid()).child("location").child(date).child(currentime).child("latitude").setValue(location.getLatitude());
        rootRef.child(user.getUid()).child("location").child(date).child(currentime).child("longtitude").setValue(location.getLongitude());
        geoFire_currentlocation.setLocation("location", new GeoLocation(location.getLatitude(), location.getLongitude()));
        rootRef.child(user.getUid()).child("lastlocation").child("lastupdate").setValue(date + " " + currentime );






    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        rootRef.child(user.getUid()).child("setting").child("islocationservicerunning").setValue(false);
        stopLocationUpdates();


    }

    protected void stopLocationUpdates() {

        if(mGoogleApiClient!=null){

            if(mGoogleApiClient.isConnected()){
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, (LocationListener) this);
                mGoogleApiClient.disconnect();

            }
        }


    }
    public void pushMessage(final String topic, final String message, final String detailaddress ) {

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
                            jData.put("usernameid", user.getUid());
                            jData.put("detailaddress", detailaddress);
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

    public void locationSetup(){

        if (isGooglePlayServicesAvailable()) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FATEST_INTERVAL);
            mLocationRequest.setPriority(gpsaccuracy);
            mLocationRequest.setSmallestDisplacement(50);
            //mLocationRequest.setSmallestDisplacement(10.0f);  /* min dist for location change, here it is 10 meter */
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mGoogleApiClient.connect();
        }



    };


}

