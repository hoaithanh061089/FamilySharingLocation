package thanhnguyen.com.familysharinglocation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/**
 * Created by THANHNGUYEN on 12/3/17.
 */

public class MyGroup extends Activity {

    SimpleDraweeView draweeview;
    DatabaseReference group_ref;
    FirebaseUser user;
    RecyclerView recyclerView;
    MyGroupAdapter mAdapter;
    List<GroupClassHelper> groupList = new ArrayList<>();
    FloatingActionsMenu multiple_actions;
    SharedPreferences sp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FamilySharingApplicationClass.getDefaultTracker(this);
        Fresco.initialize(this);
        setContentView(R.layout.mygroup);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        draweeview = (SimpleDraweeView) findViewById(R.id.draweeview);
        draweeview.setImageResource(R.drawable.house_bg);
        user = FirebaseAuth.getInstance().getCurrentUser();
        group_ref = FirebaseDatabase.getInstance().getReference();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mAdapter = new MyGroupAdapter(groupList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getBaseContext(),
                recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                GroupClassHelper groupclass = groupList.get(position);

                startActivity(new Intent(getBaseContext(), MyGroupDetails.class).putExtra("groups", groupclass));


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }){




        });


        multiple_actions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);


        FloatingActionButton fab_addtogroup = (FloatingActionButton) findViewById(R.id.addtogroup);
        fab_addtogroup.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                multiple_actions.collapse();

               boolean showaddgroup_showcase= sp.getBoolean("showaddgroup_showcase", false);
               if(!showaddgroup_showcase) {

                   MaterialShowcaseView sc = new MaterialShowcaseView.Builder(MyGroup.this)
                           .setTarget(fab_addtogroup)
                           .setDismissText("GOT IT")
                           .setContentText("Enter group ID which you got from your family members to join group. You could leave this group anytime you want.")
                           .show();
                   sc.addShowcaseListener(new IShowcaseListener() {
                       @Override
                       public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                       }

                       @Override
                       public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {

                           sp.edit().putBoolean("showaddgroup_showcase", true).apply();

                           new MaterialDialog.Builder(MyGroup.this)
                                   .content("Add my account to an exsisting group!")
                                   .inputType(InputType.TYPE_CLASS_TEXT)
                                   .input("Group name", "", new MaterialDialog.InputCallback() {
                                       @Override
                                       public void onInput(MaterialDialog dialog, final CharSequence input) {


                                           final DatabaseReference dtb= group_ref.child("group");
                                           dtb.addValueEventListener(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(final DataSnapshot groupdataSnapshot) {

                                                   dtb.removeEventListener(this);

                                                   if(!groupdataSnapshot.hasChild(input.toString())){

                                                       MDToast.makeText(getBaseContext(), "No group found, please check again!",
                                                               MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();


                                                   } else {


                                                       final DatabaseReference dtb_child =  group_ref.child(user.getUid()).child("username");
                                                       dtb_child.addValueEventListener(new ValueEventListener() {
                                                           @Override
                                                           public void onDataChange(DataSnapshot dataSnapshot) {

                                                               dtb_child.removeEventListener(this);
                                                               String username = (String) dataSnapshot.getValue();
                                                               group_ref.child("group").child(input.toString()).child("members").child(user.getUid()).child("membername").setValue(username);
                                                               group_ref.child(user.getUid()).child("mygroup").child(input.toString()).child("groupname").
                                                                       setValue(groupdataSnapshot.child(input.toString()).child("groupname").getValue());

                                                           }

                                                           @Override
                                                           public void onCancelled(DatabaseError databaseError) {

                                                           }
                                                       });






                                                   };


                                                   MyGroup.this.onResume();


                                               }

                                               @Override
                                               public void onCancelled(DatabaseError databaseError) {

                                               }
                                           });










                                           ///


                                       }
                                   }).show();





                       }
                   });



               } else {

                   new MaterialDialog.Builder(MyGroup.this)
                           .content("Add my account to an exsisting group!")
                           .inputType(InputType.TYPE_CLASS_TEXT)
                           .input("Group ID", "", new MaterialDialog.InputCallback() {
                               @Override
                               public void onInput(MaterialDialog dialog, final CharSequence input) {


                                   final DatabaseReference dtb= group_ref.child("group");
                                   dtb.addValueEventListener(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(final DataSnapshot groupdataSnapshot) {

                                           dtb.removeEventListener(this);

                                           if(!groupdataSnapshot.hasChild(input.toString())){

                                               MDToast.makeText(getBaseContext(), "No group found, please check again!",
                                                       MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();


                                           } else {


                                               final DatabaseReference dtb_child =  group_ref.child(user.getUid()).child("username");
                                               dtb_child.addValueEventListener(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(DataSnapshot dataSnapshot) {

                                                       dtb_child.removeEventListener(this);
                                                       String username = (String) dataSnapshot.getValue();
                                                       group_ref.child("group").child(input.toString()).child("members").child(user.getUid()).child("membername").setValue(username);
                                                       group_ref.child(user.getUid()).child("mygroup").child(input.toString()).child("groupname").
                                                               setValue(groupdataSnapshot.child(input.toString()).child("groupname").getValue());

                                                       MyGroup.this.onResume();

                                                   }

                                                   @Override
                                                   public void onCancelled(DatabaseError databaseError) {

                                                   }
                                               });






                                           };





                                       }

                                       @Override
                                       public void onCancelled(DatabaseError databaseError) {

                                       }
                                   });










                                   ///


                               }
                           }).show();


               }




            }
        });





        FloatingActionButton fab_newgroup = (FloatingActionButton) findViewById(R.id.newgroup);
        fab_newgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                multiple_actions.collapse();

                boolean newgroup_showcase= sp.getBoolean("newgroup_showcase", false);
                if(!newgroup_showcase) {


                    MaterialShowcaseView sc = new MaterialShowcaseView.Builder(MyGroup.this)
                            .setTarget(fab_addtogroup)
                            .setDismissText("GOT IT")
                            .setContentText("Create new group. Send new created group ID to your family members to join this group.")
                            .show();
                    sc.addShowcaseListener(new IShowcaseListener() {
                        @Override
                        public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                        }

                        @Override
                        public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {

                            sp.edit().putBoolean("newgroup_showcase", true).apply();

                            new MaterialDialog.Builder(MyGroup.this)
                                    .content("New group name")
                                    .inputType(InputType.TYPE_CLASS_TEXT)
                                    .input("Group name", "", new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog dialog, final CharSequence input) {

                                            if(input.toString().isEmpty()){

                                                return;
                                            }

                                            final String groupid = getRandomString(6);

                                            final DatabaseReference dtb = group_ref.child(user.getUid()).child("username");
                                            dtb.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    dtb.removeEventListener(this);
                                                    String username = (String) dataSnapshot.getValue();
                                                    group_ref.child("group").child(groupid).child("groupname").setValue(input.toString());
                                                    group_ref.child("group").child(groupid).child("adminuser").setValue(user.getUid());
                                                    group_ref.child("group").child(groupid).child("members").child(user.getUid()).child("membername").setValue(username);
                                                    group_ref.child(user.getUid()).child("mygroup").child(groupid).child("groupname").setValue(input.toString());







                                                    MyGroup.this.onResume();

                                                    new MaterialDialog.Builder(MyGroup.this)
                                                            .title("New group created")
                                                            .content("Group ID for your new group is " + groupid +
                                                                    ". Let other people know that group ID to join your group.")
                                                            .positiveText("GOT IT")
                                                            .show();

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });




                                            ///




                                        }
                                    }).show();





                        }
                    });


                } else {

                    new MaterialDialog.Builder(MyGroup.this)
                            .content("New group name")
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .input("Group name", "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, final CharSequence input) {

                                    if(input.toString().isEmpty()){

                                        return;
                                    }

                                    final String groupid = getRandomString(6);

                                    final DatabaseReference dtb = group_ref.child(user.getUid()).child("username");
                                    dtb.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            dtb.removeEventListener(this);
                                            String username = (String) dataSnapshot.getValue();
                                            group_ref.child("group").child(groupid).child("groupname").setValue(input.toString());
                                            group_ref.child("group").child(groupid).child("adminuser").setValue(user.getUid());
                                            group_ref.child("group").child(groupid).child("members").child(user.getUid()).child("membername").setValue(username);
                                            group_ref.child(user.getUid()).child("mygroup").child(groupid).child("groupname").setValue(input.toString());







                                            MyGroup.this.onResume();

                                            new MaterialDialog.Builder(MyGroup.this)
                                                    .title("New group created")
                                                    .content("Group ID for your new group is " + groupid +
                                                            ". Let other people know that group ID to join your group.")
                                                    .positiveText("GOT IT")
                                                    .show();

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });




                                    ///




                                }
                            }).show();


                }









            }
        });


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
    protected void onResume() {
        groupList.clear();
        mAdapter.notifyDataSetChanged();
        final  DatabaseReference database = group_ref.child(user.getUid()).child("mygroup");
        final ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                database.removeEventListener(this);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    final String groupid = snapshot.getKey();
                    final String groupname = (String) snapshot.child("groupname").getValue();

                    final DatabaseReference ref =  group_ref.child("group").child(groupid);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.child("groupname").exists()){

                                GroupClassHelper a = new GroupClassHelper(groupname, groupid);
                                groupList.add(a);
                                mAdapter.notifyDataSetChanged();

                            } else {

                                group_ref.child(user.getUid()).child("mygroup").child(groupid).removeValue();



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
        };
        database.addValueEventListener(event);

        super.onResume();


    }
}
