package thanhnguyen.com.familysharinglocation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/**
 * Created by THANHNGUYEN on 11/24/17.
 */

public class MyPlaces  extends Activity {

    SimpleDraweeView draweeview;
    RecyclerView recyclerView;
    MyplaceAdapter mAdapter;
    List<PlacesClassHelper> placeList = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference rootRef;
    FirebaseUser user;
    double la, lon;
    GeoFire geoFire;
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FamilySharingApplicationClass.getDefaultTracker(this);
        Fresco.initialize(this);
        setContentView(R.layout.myplaces);
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref =rootRef.child(user.getUid()).child("notificationplaces");
        geoFire = new GeoFire(ref);
        draweeview = (SimpleDraweeView) findViewById(R.id.draweeview);
        draweeview.setImageResource(R.drawable.house_bg);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mAdapter = new MyplaceAdapter(placeList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getBaseContext(),
                recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                PlacesClassHelper placeclass = placeList.get(position);

                startActivity(new Intent(getBaseContext(), MyPlaceDetails.class).putExtra("places", placeclass));


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }){




        });




        /*rootRef.child(user.getUid()).child(userloginname).child("notificationplaces").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String placename = snapshot.getKey();
                    if(!snapshot.child("latitude").exists() ||
                            !snapshot.child("longtitude").exists()   ){


                        break;
                    }


                    double la = (double) snapshot.child("latitude").getValue();
                    double lon = (double) snapshot.child("longtitude").getValue();
                    String add = (String) snapshot.child("address").getValue();
                    PlacesClassHelper a = new PlacesClassHelper(placename, add, la, lon);
                    placeList.add(a);


                }

                mAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(getBaseContext(), AddPlaces.class);
                startActivity(intent);
            }
        });

        new MaterialShowcaseView.Builder(this)
                .setTarget(fab)
                .setDismissText("GOT IT")
                .setContentText("Click this button to create new notification place. Whenever you leave or arrive this place, new notification will be sent to all members belong to your groups.")
                .singleUse("myplace_id") // provide a unique ID used to ensure it is only shown once
                .show();


    }

    @Override
    protected void onPause() {


        super.onPause();
    }

    @Override
    protected void onResume() {
        placeList.clear();
        mAdapter.notifyDataSetChanged();

        final  DatabaseReference database = rootRef.child(user.getUid()).child("notificationplaces");
        final ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                database.removeEventListener(this);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    final String placename = snapshot.getKey();
                    final String add = (String) snapshot.child("address").getValue();

                    geoFire.getLocation(placename, new LocationCallback() {
                        @Override
                        public void onLocationResult(String key, GeoLocation location) {
                            if (location != null) {

                            la = location.latitude;
                            lon = location.longitude;
                            PlacesClassHelper a = new PlacesClassHelper(placename, add, la, lon);
                            placeList.add(a);
                            mAdapter.notifyDataSetChanged();


                            }
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
        };
        database.addValueEventListener(event);
        super.onResume();
    }
}
