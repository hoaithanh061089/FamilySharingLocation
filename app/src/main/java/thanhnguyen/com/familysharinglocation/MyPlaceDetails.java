package thanhnguyen.com.familysharinglocation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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

/**
 * Created by THANHNGUYEN on 11/26/17.
 */

public class MyPlaceDetails extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap gm;
    String address;
    Double latitude;
    Double longtitude;
    Marker mapmarker;
    Button removeitem;
    FirebaseDatabase database;
    DatabaseReference rootRef;
    FirebaseUser user;
    PlacesClassHelper placeclass;
    SharedPreferences sharedpreferences;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_placedetails_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


            Bundle bundle = new Bundle();
            bundle.putString("name", placeclass.getName());
            bundle.putString("address", placeclass.getAddress());
            bundle.putDouble("latitude", placeclass.getLatitude());
            bundle.putDouble("longtitude", placeclass.getLongtiude());

            startActivity(new Intent(getBaseContext(), AddPlaces.class).putExtras(bundle));

            finish();


        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myplace_detail);

        placeclass  = (PlacesClassHelper) getIntent().getSerializableExtra("places");
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        removeitem = (Button) findViewById(R.id.removeitem);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(placeclass.getName());
        latitude = placeclass.getLatitude();
        longtitude=placeclass.getLongtiude();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);

        user = FirebaseAuth.getInstance().getCurrentUser();

        removeitem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                MaterialDialog dialog = new MaterialDialog.Builder(MyPlaceDetails.this)
                   .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                        rootRef.child(user.getUid()).child("notificationplaces").
                                child(placeclass.getName()).removeValue();
                        startService(new Intent(getBaseContext(), LocationNotifyService.class));


                        finish();


                    }
                })
                        .title("Remove")
                        .content("Do your want to remove this place from notification list?")
                        .positiveText("Agree")
                        .negativeText("Disagree")
                        .show();








            }
        });





    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gm = googleMap;
        LatLng lalong = new LatLng(latitude, longtitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lalong, 16);
        gm.animateCamera(cameraUpdate);
        mapmarker = placeMarker(lalong, "P");
        mapmarker.setDraggable(false);
        gm.addCircle(new CircleOptions()
                .center(lalong)
                .radius(250)
                .fillColor(0x40ff0000)
                .strokeColor(Color.parseColor("#43A047")));



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
}
