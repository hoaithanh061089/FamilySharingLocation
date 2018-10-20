package thanhnguyen.com.familysharinglocation;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonFlat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by THANHNGUYEN on 11/26/17.
 */

public class MyGroupDetails extends AppCompatActivity {
    String address;
    Button removeitem;
    FirebaseDatabase database;
    DatabaseReference rootRef;
    FirebaseUser user;
    GroupClassHelper groupclass;
    SharedPreferences sharedpreferences;
    RecyclerView recyclerView;
    MyGroupMemberAdapter mAdapter;
    List<String> memberList = new ArrayList<>();
    List<String> memberListId = new ArrayList<>();
    String adminuser;
    String removecontent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_groupdetails_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        new MaterialDialog.Builder(MyGroupDetails.this)
                .title("Group ID : " + groupclass.getId())
                .content("Share this group ID to let other people join your group.")
                .positiveText("SHARE").onPositive(new MaterialDialog.SingleButtonCallback() {
                   @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                       final Dialog infodialog = new Dialog(MyGroupDetails.this);
                       infodialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                       infodialog.setContentView(R.layout.shareappcustomlistview);
                       infodialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                       infodialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                       ((TextView) infodialog.findViewById(R.id.shareapptv)).setText("Share Group ID");
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
                       ShareAppCustomAdapter shareadapter = new ShareAppCustomAdapter(MyGroupDetails.this,  listitem);
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
                                       "Group ID : " + groupclass.getId() + ". Add this group ID to join my group.");

                               startActivity(shareIntent);
                               infodialog.dismiss();


                           }


                       });










                       infodialog.show();










                  }
             })
                .show();






        return true;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mygroup_detail);

        groupclass = (GroupClassHelper) getIntent().getSerializableExtra("groups");
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        removeitem = (Button) findViewById(R.id.removeitem);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(groupclass.getName());
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mAdapter = new MyGroupMemberAdapter(memberList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getBaseContext(),
                recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                String memberid = memberListId.get(position);
                String membername = memberList.get(position);
                startActivity(new Intent(getBaseContext(), MyPlaceMember.class).
                        putExtra("memberid",memberid ).
                        putExtra("membername", membername));


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }){




        });




        user = FirebaseAuth.getInstance().getCurrentUser();

        rootRef.child("group").child(groupclass.getId()).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot p : dataSnapshot.getChildren()) {

                    String membername = (String) p.child("membername").getValue();
                    memberList.add(membername);
                    memberListId.add(p.getKey());


                }

                mAdapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        final DatabaseReference database = rootRef.child("group").child(groupclass.getId()).child("adminuser");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                database.removeEventListener(this);
                adminuser = (String) dataSnapshot.getValue();
                if(adminuser.equals(user.getUid())){

                    removecontent = "Do your want to delete this group? If you delete other group members will remove this group too.";

                } else {

                    removecontent = "Do your want to get out of this group?";
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        removeitem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                MaterialDialog dialog = new MaterialDialog.Builder(MyGroupDetails.this)
                   .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if(adminuser.equals(user.getUid())){


                            FirebaseMessaging.getInstance().unsubscribeFromTopic(groupclass.getId());

                            //  rootRef.child(user.getUid()).child("mygroup").child(groupclass.getId()).removeValue();

                           final DatabaseReference database =  rootRef.child("group").child(groupclass.getId()).child("members");
                           database.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    database.removeEventListener(this);
                                    for(DataSnapshot p : dataSnapshot.getChildren()) {

                                        String memberid = (String) p.getKey();
                                        rootRef.child(memberid).child("mygroup").child(groupclass.getId()).removeValue();

                                    }

                                    rootRef.child("group").child(groupclass.getId()).removeValue();
                                    finish();
                                    return;



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        } else {


                            rootRef.child(user.getUid()).child("mygroup").
                                    child(groupclass.getId()).removeValue();
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(groupclass.getId());

                            rootRef.child("group").child(groupclass.getId()).child("members").child(user.getUid()).removeValue();

                            finish();


                        }


                    }
                })
                        .title("Remove")
                        .content(removecontent)
                        .positiveText("Agree")
                        .negativeText("Disagree")
                        .show();



            }
        });





    }


}
