package thanhnguyen.com.familysharinglocation;

import android.text.TextUtils;

/**
 * Created by FrankOdoom on 23/08/17.
 */

public class AppConstants {

    //back right button color 033809

    // Location updates intervals
    public static int UPDATE_INTERVAL = 1000*5*60; // 3 sec
    public static int FATEST_INTERVAL = 1000*5*60; // 5 sec
    public static int DISPLACEMENT = 10; // 10 meters
    public static int DAYMAXONSERVER = 999;


    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

/*//remember to check permission at run time
    public String getAccount() {
        AccountManager accountManager = AccountManager.get(ShowFamilyPositionOnMap.this);
        Account[] accounts = accountManager.getAccountsByType("com.google");

        if (accounts.length == 0) {

            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();




            return null;
        }

      //  Toast.makeText(this, accounts[0].name, Toast.LENGTH_SHORT).show();



        return accounts[0].name;
    }


    public String getidToken(){

        String accountName = getAccount();
        final String scope = "audience:server:client_id:"
                + "388634886645-58thf4k79vng9oh7d2svvi5te5o323kn.apps.googleusercontent.com";
        String idToken = null;
        try {

           idToken = GoogleAuthUtil.getToken(getBaseContext(), accountName, scope);

           rootRef.child(user.getUid()).child("idToken").setValue(idToken);


        } catch (UserRecoverableAuthException e) {

        } catch (GoogleAuthException e) {

        } catch (IOException e) {

        };

        return idToken;



    };

    public void addNotificationKey() {

        rootRef.child(user.getUid()).child("idToken").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String senderId, userEmail,registrationId, idToken;
                //project id on firebase Go to Project Settings, then Cloud Messaging tab.
                senderId = "388634886645";
                userEmail = user.getUid();
                registrationId = userloginname;

                try {

                    idToken = (String) dataSnapshot.getValue();


                    new Thread(new Runnable() {
                        @Override
                        public void run() {


                            try{



                                URL url = new URL("https://android.googleapis.com/gcm/googlenotification");
                                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                con.setDoOutput(true);

                                // HTTP request header
                                con.setRequestProperty("project_id", senderId);
                                con.setRequestProperty("Content-Type", "application/json");
                                con.setRequestProperty("Accept", "application/json");
                                con.setRequestMethod("POST");
                                con.connect();

                                // HTTP request
                                JSONObject data = new JSONObject();
                                data.put("operation", "add");
                                data.put("notification_key_name", userEmail);
                                data.put("registration_ids", new JSONArray(Arrays.asList(registrationId)));
                                data.put("id_token", idToken);

                                OutputStream os = con.getOutputStream();
                                os.write(data.toString().getBytes("UTF-menuback"));
                                os.close();

                                // Read the response into a string
                                InputStream is = con.getInputStream();
                                String responseString = new Scanner(is, "UTF-menuback").useDelimiter("\\A").next();
                                is.close();

                                // Parse the JSON string and return the notification key
                                JSONObject response = new JSONObject(responseString);

                                rootRef.child(user.getUid()).child("notification_key").setValue(response.getString("notification_key"));

                                //  return response.getString("notification_key");




                            } catch(final Exception e ){

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });



                            }







                        }
                    }).start();





                } catch(final Exception e) {






                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }
*/



}
