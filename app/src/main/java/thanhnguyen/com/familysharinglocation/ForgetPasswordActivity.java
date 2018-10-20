package thanhnguyen.com.familysharinglocation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by THANHNGUYEN on 10/22/17.
 */
public class ForgetPasswordActivity extends Activity {

    LinearLayout mainlayout;
    EditText registered_emailid;
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("registered_emailid", registered_emailid.getText().toString());
        super.onSaveInstanceState(outState);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.forgotpassword_layout);
        registered_emailid = (EditText) findViewById(R.id.registered_emailid);
        mainlayout = (LinearLayout) findViewById(R.id.mainlayout);
        Button forgot_button = (Button) findViewById(R.id.forgot_button);

        if(savedInstanceState!=null)
        {
            String emaild_text = savedInstanceState.getString("registered_emailid");
            registered_emailid.setText(emaild_text);

        }

        forgot_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                if(registered_emailid.getText().toString().isEmpty())
                {

                    Snackbar sb = Snackbar.make(mainlayout, "Please fill your email!", Snackbar.LENGTH_SHORT);
                    View snackBarView = sb.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(ForgetPasswordActivity.this, R.color.material_green_700));
                    TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    sb.show();

                    return;

                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(registered_emailid.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Snackbar sb = Snackbar.make(mainlayout, "Email sent, please check your email inbox!", Snackbar.LENGTH_SHORT);
                                    View snackBarView = sb.getView();
                                    snackBarView.setBackgroundColor(ContextCompat.getColor(ForgetPasswordActivity.this, R.color.material_green_700));
                                    TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                                    textView.setTextColor(Color.BLACK);
                                    sb.show();
                                    startActivity(new Intent(getBaseContext(), LoginPage.class));

                                } else {

                                    try {

                                        throw task.getException();

                                    } catch (Exception e) {

                                        Snackbar sb = Snackbar.make(mainlayout, task.getException().getMessage(), Snackbar.LENGTH_SHORT);
                                        View snackBarView = sb.getView();
                                        snackBarView.setBackgroundColor(ContextCompat.getColor(ForgetPasswordActivity.this, R.color.material_green_700));
                                        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                                        textView.setTextColor(Color.BLACK);
                                        sb.show();

                                    }


                                }
                            }
                        });


            }
        });




    }

}
