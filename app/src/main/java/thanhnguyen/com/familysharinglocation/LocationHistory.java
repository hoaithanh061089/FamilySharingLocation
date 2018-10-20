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
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationHistory extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String userid;
    DatabaseReference rootRef;
    FirebaseUser user;
    LatLng camerastarter;
    int date_count_onfirebase;
    ImageView previousbutton;
    ImageView nextbutton;
    int day_show_on_map;
    TextView datetv, distancetv, timetv, speedtv;
    Bitmap smallflagmarker;
    LinearLayout mainlayout;


    List<LocationHistoryClassHelper> locationlist_one;
    List<LocationHistoryClassHelper> locationlist_two;
    List<LocationHistoryClassHelper> locationlist_three;
    ValueEventListener firstshowuplistener;
    ValueEventListener previouseventlistener;
    ValueEventListener nexteventlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);
        MobileAds.initialize(this, "ca-app-pub-7878202527387725~8210738184");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
      //  mAdView.loadAd(adRequest);
        mainlayout = (LinearLayout) findViewById(R.id.mainlayout);
        previousbutton = (ImageView) findViewById(R.id.previousbutton);
        nextbutton = (ImageView) findViewById(R.id.nextbutton);
        datetv = (TextView) findViewById(R.id.datetv);
        distancetv = (TextView) findViewById(R.id.distancetv);
        timetv = (TextView) findViewById(R.id.timetv);
        speedtv= (TextView) findViewById(R.id.speedtv);
        locationlist_one = new ArrayList<LocationHistoryClassHelper>();
        locationlist_two= new ArrayList<LocationHistoryClassHelper>();
        locationlist_three=new ArrayList<LocationHistoryClassHelper>();

        userid = LocationHistory.this.getIntent().getStringExtra("userid");
        user = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        int height = 60;
        int width = 60;
        BitmapDrawable bitmapflagmarker=(BitmapDrawable)getResources().getDrawable(R.drawable.flagmarker);
        Bitmap b=bitmapflagmarker.getBitmap();
        smallflagmarker = Bitmap.createScaledBitmap(b, width, height, false);
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                    LocationHistoryClassHelper markerhelper = (LocationHistoryClassHelper) marker.getTag();
                    if(markerhelper==null){
                        return false;
                    }

                    timetv.setText(markerhelper.getTime());


                 rootRef.child(userid).child("setting").child("distanceunit").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        int value = Integer.parseInt(dataSnapshot.getValue().toString());
                        if(value == 0 ){

                            speedtv.setText(String.valueOf(markerhelper.getSpeed()) + " " + "km/h");

                        } else {


                            speedtv.setText(String.valueOf(convertKmsToMiles(markerhelper.getSpeed()))  + " " + "miles/h");
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });











                getAddressFromLocation(markerhelper.getLatitude(), markerhelper.getLongtitude(), LocationHistory.this, new GeocoderHandler());

                return false;
            }
        });


        firstshowuplistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                date_count_onfirebase=(int) dataSnapshot.getChildrenCount();
                if(date_count_onfirebase==1){

                    previousbutton.setEnabled(false);
                }

                day_show_on_map = date_count_onfirebase;

                int i=1;

                PolylineOptions polyline = new PolylineOptions();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    if(i==day_show_on_map){

                        String date = (String) snapshot.getKey();
                        datetv.setText("Date:"+ " "+ date);


                        int j=0;
                        int k=0;

                        for(DataSnapshot snapshot_date : dataSnapshot.child(date).getChildren()){
                            String time = snapshot_date.getKey();
                            Double longtitude = (Double) dataSnapshot.child(date).child(time).child("longtitude").getValue();
                            Double latitude = (Double) dataSnapshot.child(date).child(time).child("latitude").getValue();
                            Long userspeed = ((Long) dataSnapshot.child(date).child(time).child("speed").getValue());
                            int speed = userspeed.intValue();
                            polyline.add(new LatLng(latitude, longtitude)) ;

                            j++;

                            if(j % 2 != 0){

                                continue;
                            }

                            if(j % 30==0){

                                k++;

                                Marker usermarker = placeMarker(new LatLng(latitude, longtitude), String.valueOf(k));
                                usermarker.setTag(new LocationHistoryClassHelper(date, time, latitude, longtitude, speed));

                            } else {

                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latitude,longtitude ))
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallflagmarker)));
                                marker.setTag(new LocationHistoryClassHelper(date, time, latitude, longtitude, speed));


                            }











                        }



                    }



                    i++;

                }


                double pathdistance = round((SphericalUtil.computeLength(polyline.getPoints())/1000), 2);

                rootRef.child(userid).child("setting").child("distanceunit").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        int value = Integer.parseInt(dataSnapshot.getValue().toString());
                        if(value == 0 ){

                            distancetv.setText(String.valueOf(pathdistance)  + " " + "km");

                        } else {


                            distancetv.setText(String.valueOf(convertKmsToMiles(pathdistance))  + " " + "miles");
                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                int height = 110;
                int width = 110;
                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.redflag);
                Bitmap b=bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


                BitmapDrawable bitmapdraw_blue=(BitmapDrawable)getResources().getDrawable(R.drawable.blueflag);
                Bitmap bl=bitmapdraw_blue.getBitmap();
                Bitmap smallMarker_blue = Bitmap.createScaledBitmap(bl, width, height, false);


                mMap.addMarker(new MarkerOptions()
                        .position(polyline.getPoints().get(0))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

                mMap.addMarker(new MarkerOptions()
                        .position(polyline.getPoints().get(polyline.getPoints().size()-1))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker_blue)));


                mMap.addPolyline(polyline);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(polyline.getPoints().get(0), 15));



                rootRef.child(userid).child("location").removeEventListener(firstshowuplistener);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        rootRef.child(userid).child("location").addValueEventListener(firstshowuplistener);

        //daate on first shown so disable next button

        nextbutton.setEnabled(false);
        nextbutton.setFocusable(false);
        nextbutton.setBackground(getResources().getDrawable(R.drawable.rightbutton));
        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();

                if(!previousbutton.isEnabled()){

                    previousbutton.setEnabled(true);
                    previousbutton.setBackground(getResources().getDrawable(R.drawable.backbutton_green));

                }

                day_show_on_map = day_show_on_map+1;

                nexteventlistener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        int i=1;

                        PolylineOptions polyline = new PolylineOptions();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            if(i==day_show_on_map){

                                String date = (String) snapshot.getKey();
                                datetv.setText("Date:"+ " "+ date);

                                int j=0;
                                int k=0;

                                for(DataSnapshot snapshot_date : dataSnapshot.child(date).getChildren()){
                                    String time = snapshot_date.getKey();
                                    Double longtitude = (Double) dataSnapshot.child(date).child(time).child("longtitude").getValue();
                                    Double latitude = (Double) dataSnapshot.child(date).child(time).child("latitude").getValue();
                                    Long userspeed = ((Long) dataSnapshot.child(date).child(time).child("speed").getValue());
                                    int speed = userspeed.intValue();
                                    polyline.add(new LatLng(latitude, longtitude)) ;

                                    j++;

                                    if(j % 2 != 0){

                                        continue;
                                    }

                                    if(j % 30==0){

                                        k++;

                                        Marker usermarker = placeMarker(new LatLng(latitude, longtitude), String.valueOf(k));
                                        usermarker.setTag(new LocationHistoryClassHelper(date, time, latitude, longtitude, speed));

                                    } else {

                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(latitude,longtitude ))
                                                .icon(BitmapDescriptorFactory.fromBitmap(smallflagmarker)));
                                        marker.setTag(new LocationHistoryClassHelper(date, time, latitude, longtitude, speed));


                                    }




                                }



                            }



                            i++;

                        }
                        double pathdistance = round((SphericalUtil.computeLength(polyline.getPoints())/1000), 2);
                        rootRef.child(userid).child("setting").child("distanceunit").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                int value = Integer.parseInt(dataSnapshot.getValue().toString());
                                if(value == 0 ){

                                    distancetv.setText(String.valueOf(pathdistance)  + " " + "km");

                                } else {


                                    distancetv.setText(String.valueOf(convertKmsToMiles(pathdistance))  + " " + "miles");
                                }



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        int height = 110;
                        int width = 110;
                        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.redflag);
                        Bitmap b=bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


                        BitmapDrawable bitmapdraw_blue=(BitmapDrawable)getResources().getDrawable(R.drawable.blueflag);
                        Bitmap bl=bitmapdraw_blue.getBitmap();
                        Bitmap smallMarker_blue = Bitmap.createScaledBitmap(bl, width, height, false);


                        mMap.addMarker(new MarkerOptions()
                                .position(polyline.getPoints().get(0))
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

                        mMap.addMarker(new MarkerOptions()
                                .position(polyline.getPoints().get(polyline.getPoints().size()-1))
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker_blue)));


                        mMap.addPolyline(polyline);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(polyline.getPoints().get(0), 15));
                        rootRef.child(userid).child("location").removeEventListener(nexteventlistener);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                rootRef.child(userid).child("location").addValueEventListener(nexteventlistener);
                if(day_show_on_map == date_count_onfirebase){

                    nextbutton.setEnabled(false);
                    nextbutton.setBackground(getResources().getDrawable(R.drawable.rightbutton));


                }



            }
        });

        previousbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();

                nextbutton.setEnabled(true);
                nextbutton.setBackground(getResources().getDrawable(R.drawable.rightbutton_green));


                day_show_on_map = day_show_on_map-1;

                previouseventlistener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        int i=1;

                        PolylineOptions polyline = new PolylineOptions();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            if(i==day_show_on_map){

                                String date = (String) snapshot.getKey();
                                datetv.setText("Date:"+ " "+ date);
                                int j=0;
                                int k=0;

                                for(DataSnapshot snapshot_date : dataSnapshot.child(date).getChildren()){
                                    String time = snapshot_date.getKey();
                                    Double longtitude = (Double) dataSnapshot.child(date).child(time).child("longtitude").getValue();
                                    Double latitude = (Double) dataSnapshot.child(date).child(time).child("latitude").getValue();
                                    Long userspeed = ((Long) dataSnapshot.child(date).child(time).child("speed").getValue());
                                    int speed = userspeed.intValue();
                                    polyline.add(new LatLng(latitude, longtitude)) ;

                                    j++;

                                    if(j % 2 != 0){

                                        continue;
                                    }

                                    if(j % 30==0){

                                        k++;

                                        Marker usermarker = placeMarker(new LatLng(latitude, longtitude), String.valueOf(k));
                                        usermarker.setTag(new LocationHistoryClassHelper(date, time, latitude, longtitude, speed));

                                    } else {

                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(latitude,longtitude ))
                                                .icon(BitmapDescriptorFactory.fromBitmap(smallflagmarker)));
                                        marker.setTag(new LocationHistoryClassHelper(date, time, latitude, longtitude, speed));


                                    }




                                }





                            }



                            i++;

                        }

                        polyline.clickable(true);
                        double pathdistance = round((SphericalUtil.computeLength(polyline.getPoints())/1000), 2);
                        rootRef.child(userid).child("setting").child("distanceunit").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                int value = Integer.parseInt(dataSnapshot.getValue().toString());
                                if(value == 0 ){

                                    distancetv.setText(String.valueOf(pathdistance)  + " " + "km");

                                } else {


                                    distancetv.setText(String.valueOf(convertKmsToMiles(pathdistance))  + " " + "miles");
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                        int height = 110;
                        int width = 110;
                        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.redflag);
                        Bitmap b=bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


                        BitmapDrawable bitmapdraw_blue=(BitmapDrawable)getResources().getDrawable(R.drawable.blueflag);
                        Bitmap bl=bitmapdraw_blue.getBitmap();
                        Bitmap smallMarker_blue = Bitmap.createScaledBitmap(bl, width, height, false);


                        mMap.addMarker(new MarkerOptions()
                                .position(polyline.getPoints().get(0))
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

                        mMap.addMarker(new MarkerOptions()
                                .position(polyline.getPoints().get(polyline.getPoints().size()-1))
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker_blue)));


                        mMap.addPolyline(polyline);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(polyline.getPoints().get(0), 15));
                        rootRef.child(userid).child("location").removeEventListener(previouseventlistener);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                rootRef.child(userid).child("location").addValueEventListener(previouseventlistener);
                if(day_show_on_map==1){

                    previousbutton.setEnabled(false);
                    previousbutton.setBackground(getResources().getDrawable(R.drawable.backbutton));


                }



            }
        });




/*

        date_one_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(LocationHistory.this, String.valueOf(locationlist_one.size()), Toast.LENGTH_SHORT).show();



                mMap.clear();

                PolylineOptions polyline = new PolylineOptions();

                for(int i=0; i < locationlist_one.size(); i++){
                    LocationHistoryClassHelper location_history = locationlist_one.get(i);
                    polyline.add(new LatLng(location_history.getLatitude(), location_history.getLongtitude()));
                    if(i==0){

                        camerastarter = new LatLng(location_history.getLatitude(), location_history.getLongtitude());
                    }


                };


                mMap.addPolyline(polyline);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camerastarter, 15));





            }
        });

        date_two_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();


                PolylineOptions polyline = new PolylineOptions();


                Toast.makeText(LocationHistory.this, String.valueOf(locationlist_two.size()), Toast.LENGTH_SHORT).show();

                for(int i=0; i < locationlist_two.size(); i++){
                    LocationHistoryClassHelper location_history = locationlist_two.get(i);
                    polyline.add(new LatLng(location_history.getLatitude(), location_history.getLongtitude()));
                    if(i==0){

                        camerastarter = new LatLng(location_history.getLatitude(), location_history.getLongtitude());
                    }


                };


                mMap.addPolyline(polyline);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camerastarter, 15));




            }
        });
*/

          /*rootRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                date_count_onfirebase = dataSnapshot.child(userloginname).child("location").getChildrenCount();

                Toast.makeText(getBaseContext(), String.valueOf(date_count_onfirebase), Toast.LENGTH_SHORT).show();

                int i=1;

                for(DataSnapshot snapshot : dataSnapshot.child(userloginname).child("location").getChildren()){

                  String date = (String) snapshot.getKey();

                  for(DataSnapshot snapshot_date : dataSnapshot.child(userloginname).child("location").child(date).getChildren()){

                      String time = snapshot_date.getKey();
                      Double longtitude = (Double) dataSnapshot.child(userloginname).child("location").child(date).child(time).child("longtitude").getValue();
                      Double latitude = (Double) dataSnapshot.child(userloginname).child("location").child(date).child(time).child("latitude").getValue();
                      LocationHistoryClassHelper item = new LocationHistoryClassHelper(date, time, latitude, longtitude );

                      if(i==1){

                          locationlist_one.add(item);


                      } else if(i==2){

                          locationlist_two.add(item);


                      } else {

                          locationlist_three.add(item);


                      }






                  }


                    i++;


                }







            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

*/



    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
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
    private Marker placeMarker(LatLng point, String name){

        // set for small marker
        int radius = 150;
        int stroke = 7;
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

    public double convertKmsToMiles(double kms){
        double miles = round(0.621371 * kms,2);
        return miles;
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
                    return;
            }
            Snackbar sb = Snackbar.make(mainlayout, result, Snackbar.LENGTH_SHORT);
            View snackBarView = sb.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(LocationHistory.this, R.color.material_green_900));
            TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            sb.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FamilySharingApplicationClass.getDefaultTracker(this);
        rootRef.child(userid).child("setting").child("maptheme").addValueEventListener(new ValueEventListener() {
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
