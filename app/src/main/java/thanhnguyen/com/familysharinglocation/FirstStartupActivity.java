package thanhnguyen.com.familysharinglocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import za.co.riggaroo.materialhelptutorial.TutorialItem;
import za.co.riggaroo.materialhelptutorial.tutorial.MaterialTutorialActivity;

/**
 * Created by THANHNGUYEN on 12/14/17.
 */

public class FirstStartupActivity  extends Activity{

    SharedPreferences sp;
    int REQUEST_CODE_INTRO=10;
    int color = R.color.material_blue_400;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        if(sp.getBoolean("firststartup", true)){

            loadTutorial();


        } else {

            startActivity(new Intent(this, LoginPage.class));
            finish();

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {

            sp.edit().putBoolean("firststartup", false).apply();
            startActivity(new Intent(this, LoginPage.class));
            finish();


        }
    }

    public void loadTutorial() {
        Intent mainAct = new Intent(this, MaterialTutorialActivity.class);
        mainAct.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, getTutorialItems(this));
        startActivityForResult(mainAct, REQUEST_CODE_INTRO);

    }
    private ArrayList<TutorialItem> getTutorialItems(Context context) {

        TutorialItem tutorialItem1 = new TutorialItem(
                "Real time location",
                "View real time location of your group members on a private family map.",
                color,
                R.drawable.slide1,
                R.drawable.slide1);
        TutorialItem tutorialItem2 = new TutorialItem(
                "Location history",
                "View location history of your group member with no limited time.",
                color,
                R.drawable.slide2,
                R.drawable.slide2);
        TutorialItem tutorialItem3 = new TutorialItem(
                "Real time weather condition",
                "View real time weather condition of your group members with high precise.",
                color,
                R.drawable.slide3,
                R.drawable.slide3);
        TutorialItem tutorialItem4 = new TutorialItem(
                "SOS message",
                "Send instant message to all your group members.",
                color,
                R.drawable.slide4,
                R.drawable.slide4);



        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        tutorialItems.add(tutorialItem4);


        return tutorialItems;
    }


}
