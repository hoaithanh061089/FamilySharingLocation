package thanhnguyen.com.familysharinglocation;

import android.content.Context;
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
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by THANHNGUYEN on 12/10/17.
 */

public class MyPlaceMember extends AppCompatActivity implements OnMapReadyCallback {
    DatabaseReference rootRef;
    FirebaseUser user;
    GoogleMap mMap;
    TextView lastupdate, address;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mygroupmember);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("membername"));
        rootRef = FirebaseDatabase.getInstance().getReference();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        lastupdate = (TextView) findViewById(R.id.lastupdate);
        address = (TextView) findViewById(R.id.address);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        rootRef.child(getIntent().getStringExtra("memberid")).child("lastlocation").child("lastupdate")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            lastupdate.setText(dataSnapshot.getValue().toString());


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        GeoFire geoFire = new GeoFire(rootRef.child(getIntent().getStringExtra("memberid")).child("lastlocation"));
        geoFire.getLocation("location", new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {

                if(location==null){

                    MDToast mdToast = MDToast.makeText(getBaseContext(), "No location found for this user.", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);

                    mdToast.show();

                    finish();

                    return;

                }


                Marker marker = placeMarker(new LatLng(location.latitude, location.longitude), getIntent().getStringExtra("membername"));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude, location.longitude), 15);
                googleMap.animateCamera(cameraUpdate);
                getAddressFromLocation(location.latitude, location.longitude, getBaseContext(), new GeocoderHandler());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private Marker placeMarker(LatLng point, String name){

        // set for small marker
        int radius = 230;
        int stroke = 15;
        float verticalAnchor = 0.944f;


        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap((int) radius, (int) radius + 25, conf);
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
                } catch (IOException e) {

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
                    break;
                default:
                    result = null;
            }

            if(result!=null){


                if(!result.isEmpty()){

                    address.setText(result);
                    address.setVisibility(View.VISIBLE);


                }
            }



        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        rootRef.child(getIntent().getStringExtra("memberid")).child("setting").child("maptheme").addValueEventListener(new ValueEventListener() {
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

}
