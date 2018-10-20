package thanhnguyen.com.familysharinglocation;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ExpandableListAdapter;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by THANHNGUYEN on 12/16/17.
 */

public class HelpActivity extends ExpandableListActivity {
    ExpandableListAdapter mAdapterView;
    android.widget.ExpandableListView expandableListView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.helplayout);
        List<Map<String, String>> groupListItem = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childListItem = new ArrayList<List<Map<String, String>>>();

	/* ******************** Group item 1 ********************* */
        Map<String, String> group1 = new HashMap<String, String>();
        groupListItem.add(group1);
        group1.put("parentItem", "What' family location sharing app?");

        List<Map<String, String>> children1 = new ArrayList<Map<String, String>>();
        Map<String, String> childrenitem1 = new HashMap<String, String>();
        children1.add(childrenitem1);
        childrenitem1.put("childItem", "This free app lets family members track one another in real time. Likewise, the app can be used to quickly broadcast your location in an emergency situation");
        childListItem.add(children1);

	/* ******************** Group Item 2  ********************* */
        Map<String, String> childrenitem6 = new HashMap<String, String>();
        groupListItem.add(childrenitem6);
        childrenitem6.put("parentItem", "Android Expandable ListView");
        List<Map<String, String>> children2 = new ArrayList<Map<String, String>>();

        Map<String, String> childrenitem7 = new HashMap<String, String>();
        children2.add(childrenitem7);
        childrenitem7.put("childItem", "Android ListView");

        Map<String, String> childrenitem8 = new HashMap<String, String>();
        children2.add(childrenitem8);
        childrenitem8.put("childItem", "Expandable ListView");

        Map<String, String> child8 = new HashMap<String, String>();
        children2.add(child8);
        child8.put("childItem", "ListView Example");
        childListItem.add(children2);


        	/* ******************** Group Item 3  ********************* */
        Map<String, String> childrenitem9 = new HashMap<String, String>();
        groupListItem.add(childrenitem9);
        childrenitem9.put("parentItem", "Expandable ListView Tutorial");
        List<Map<String, String>> children3 = new ArrayList<Map<String, String>>();

        Map<String, String> childrenitem10 = new HashMap<String, String>();
        children3.add(childrenitem10);
        childrenitem10.put("childItem", "Android ListView");

        Map<String, String> childrenitem11 = new HashMap<String, String>();
        children3.add(childrenitem11);
        childrenitem11.put("childItem", "Expandable ListView");

        Map<String, String> childrenitem12 = new HashMap<String, String>();
        children3.add(childrenitem12);
        childrenitem12.put("childItem", "ListView Example");
        childListItem.add(children3);

        mAdapterView = new SimpleExpandableListAdapter(
                this,
                groupListItem,
                android.R.layout.simple_expandable_list_item_1,
                new String[]{"parentItem"},
                new int[]{android.R.id.text1, android.R.id.text2},
                childListItem,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{"childItem"},
                new int[]{android.R.id.text1}
        );
        setListAdapter(mAdapterView);
    }
}
