package thanhnguyen.com.familysharinglocation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.location.Address;
import android.location.Geocoder;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
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
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.List;
import java.util.Locale;

/**
 * Created by THANHNGUYEN on 11/24/17.
 */

public class AddPlaces extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap gm;
    TextInputLayout address, name_input;
    GoogleApiClient mGoogleApiClient;
    Marker mapmarker;
    FirebaseDatabase database;
    DatabaseReference rootRef;
    FirebaseUser user;
    GeoFire geoFire;
    SharedPreferences sharedpreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_addplaces_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(name_input.getEditText().getText().toString().isEmpty() ||
                address.getEditText().getText().toString().isEmpty() ||
                mapmarker==null){

            if(name_input.getEditText().getText().toString().isEmpty()){

                name_input.setErrorEnabled(true);
                name_input.setError("Please fill your name");


            }
            if(address.getEditText().getText().toString().isEmpty()){

                address.setErrorEnabled(true);
                address.setError("Please fill your address");


            }
            if(mapmarker==null){
                address.setErrorEnabled(true);
                address.setError("Please identify your address marker on the map");

            }

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    name_input.setErrorEnabled(false);
                    address.setErrorEnabled(false);
                    name_input.setError("");
                    name_input.setError("");

                }
            }, 3000);

            return false;


        } else {

            String name = name_input.getEditText().getText().toString();
            String add = address.getEditText().getText().toString();

            DatabaseReference ref =rootRef.child(user.getUid()).child("notificationplaces");
            geoFire = new GeoFire(ref);

            if(getIntent().getExtras()==null){


                geoFire.setLocation(name, new GeoLocation(mapmarker.getPosition().latitude, mapmarker.getPosition().longitude));
                ref.child(name).child("address").setValue(add);
                ref.child(name).child("available").setValue(false);

                startService(new Intent(getBaseContext(), LocationNotifyService.class));


                /*rootRef.child(user.getUid()).child(userloginname).child("notificationplaces").child(name).child("address").setValue(add);
                rootRef.child(user.getUid()).child(userloginname).child("notificationplaces").child(name).child("latitude").setValue(mapmarker.getPosition().latitude);
                rootRef.child(user.getUid()).child(userloginname).child("notificationplaces").child(name).child("longtitude").setValue(mapmarker.getPosition().longitude);
*/




            } else {

                String oldname = getIntent().getExtras().getString("name");

                if(name==oldname){

                    geoFire.setLocation(name, new GeoLocation(mapmarker.getPosition().latitude, mapmarker.getPosition().longitude));
                    ref.child(name).child("address").setValue(add);
                    ref.child(name).child("available").setValue(false);

                    startService(new Intent(getBaseContext(), LocationNotifyService.class));


                   /* rootRef.child(user.getUid()).child(userloginname).child("notificationplaces").child(name).child("address").setValue(add);
                    rootRef.child(user.getUid()).child(userloginname).child("notificationplaces").child(name).child("latitude").setValue(mapmarker.getPosition().latitude);
                    rootRef.child(user.getUid()).child(userloginname).child("notificationplaces").child(name).child("longtitude").setValue(mapmarker.getPosition().longitude);
*/

                } else {

                    rootRef.child(user.getUid()).child("notificationplaces").child(oldname).removeValue();
                    //remove all value from old name;


                    geoFire.setLocation(name, new GeoLocation(mapmarker.getPosition().latitude, mapmarker.getPosition().longitude));
                    ref.child(name).child("address").setValue(add);
                    ref.child(name).child("available").setValue(false);

                    startService(new Intent(getBaseContext(), LocationNotifyService.class));




                    /*rootRef.child(user.getUid()).child(userloginname).child("notificationplaces").child(name).child("address").setValue(add);
                    rootRef.child(user.getUid()).child(userloginname).child("notificationplaces").child(name).child("latitude").setValue(mapmarker.getPosition().latitude);
                    rootRef.child(user.getUid()).child(userloginname).child("notificationplaces").child(name).child("longtitude").setValue(mapmarker.getPosition().longitude);
*/

                }



            }

            finish();
            return true;
        }



    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addplaces_layout);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Add my places");
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        name_input= (TextInputLayout) findViewById(R.id.name_input);
        address = (TextInputLayout) findViewById(R.id.address);
        address.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus) {

                    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                        .build(AddPlaces.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }


                }

            }
        });




        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gm = googleMap;
        Bundle extras = getIntent().getExtras();
        if(extras!= null){

            String name = extras.getString("name");
            String address_t = extras.getString("address");
            Double latitude = extras.getDouble("latitude");
            Double longtitude = extras.getDouble("longtitude");
            name_input.getEditText().setText(name);
            address.getEditText().setText(address_t);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longtitude), 15);
            gm.animateCamera(cameraUpdate);
            mapmarker = placeMarker(new LatLng(latitude, longtitude), "P");
            mapmarker.setDraggable(true);
            gm.addCircle(new CircleOptions()
                    .center(new LatLng(latitude, longtitude))
                    .radius(250)
                    .fillColor(0x40ff0000)
                    .strokeColor(Color.parseColor("#43A047")));


        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            MDToast mdToast = MDToast.makeText(getBaseContext(), "App need location permissions to work properly. Please turn it on. ", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);

            mdToast.show();

              return;
        }

        gm.setMyLocationEnabled(true);

        gm.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                gm.clear();
                mapmarker = placeMarker(latLng, "P");
                mapmarker.setDraggable(true);
                gm.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(250)
                        .fillColor(0x40ff0000)
                        .strokeColor(Color.parseColor("#43A047")));

                getAddressFromLocation(mapmarker.getPosition().latitude,
                        mapmarker.getPosition().longitude, AddPlaces.this, new GeocoderHandler());


            }
        });




        gm.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {



            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                mapmarker = marker;

                getAddressFromLocation(marker.getPosition().latitude,
                        marker.getPosition().longitude, AddPlaces.this, new GeocoderHandler());



            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                address.getEditText().setText(place.getAddress());

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15);
                gm.animateCamera(cameraUpdate);
                mapmarker = placeMarker(place.getLatLng(), "P");
                mapmarker.setDraggable(true);
                gm.addCircle(new CircleOptions()
                        .center(place.getLatLng())
                        .radius(250)
                        .fillColor(0x40ff0000)
                        .strokeColor(Color.parseColor("#43A047")));


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {


                Status status = PlaceAutocomplete.getStatus(this, data);


            } else if (resultCode == RESULT_CANCELED) {



            }
        }
    }

    private Marker placeMarker(LatLng point, String name){

        // set for small marker
        int radius = 180;
        int stroke = 10;
        float verticalAnchor = 0.944f;


        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap((int) radius, (int) radius + 20, conf);
        Canvas canvas = new Canvas(bmp);


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
        path.moveTo(radius / 2, radius + 15);
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
        return gm.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromBitmap(bmp)).anchor(0.5f, verticalAnchor));
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
        paint.setTextSize(60);
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
    public static void getAddressFromLocation(final double LATITUDE, final double LONGITUDE,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override public void run() {

                String strAdd = null;
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                    if (addresses != null) {
                        Address returnedAddress = addresses.get(0);
                        StringBuilder strReturnedAddress = new StringBuilder("");

                        for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                        }
                        strAdd = strReturnedAddress.toString();

                    } else {

                        strAdd = "No address found!";

                    }

                    if(strAdd.isEmpty() || strAdd==null ){

                        strAdd = "No address found!";

                    }
                } catch (Exception e) {

                    strAdd = e.getMessage();


                } finally {
                    Message msg = Message.obtain();
                    msg.setTarget(handler);
                    if (strAdd != null) {
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", strAdd);
                        msg.setData(bundle);
                    } else
                        msg.what = 0;
                    msg.sendToTarget();
                }
            }
        };
        thread.start();
    }
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String result;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    result = bundle.getString("address");
                    address.getEditText().setText(result);

                    break;
                default:
                    result = null;
                    address.getEditText().setText("fff");
            }






        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        rootRef.child(user.getUid()).child("setting").child("maptheme").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()==null){

                    return;
                }

                String value = dataSnapshot.getValue().toString();

                if(gm!=null) {

                    switch (value){

                        case "0":

                            gm.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(),R.raw.normal_json));

                            break;

                        case "1":

                            gm.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(),R.raw.retro_json));


                            break;

                        case "2":

                            gm.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(),R.raw.night_json));


                            break;

                        case "3":

                            gm.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(),R.raw.aubergine_json));


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
