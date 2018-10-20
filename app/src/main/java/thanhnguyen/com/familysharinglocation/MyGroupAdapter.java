package thanhnguyen.com.familysharinglocation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

/**
 * Created by THANHNGUYEN on 12/3/17.
 */

public class MyGroupAdapter extends RecyclerView.Adapter<MyGroupAdapter.MyViewHolder> {

    private List<GroupClassHelper> groupList;
    ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT

    public MyGroupAdapter(List<GroupClassHelper> groupList) {
        this.groupList = groupList;
    }

    @Override
    public MyGroupAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mygroup_list_row, parent, false);

        return new MyGroupAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyGroupAdapter.MyViewHolder holder, int position) {
        GroupClassHelper place = groupList.get(position);
        char groupname = ((String) place.getName()).charAt(0);
        
       // generate random color
        int color1 = generator.getRandomColor();
        TextDrawable drawable2 = TextDrawable.builder()
                .buildRound(String.valueOf(groupname).toUpperCase(), color1);

        holder.groupname.setText(place.getName());
        holder.groupid.setText(place.getId());
        holder.image.setImageDrawable(drawable2);

    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView groupname;
        public TextView groupid;
        public ImageView image;


        public MyViewHolder(View view) {
            super(view);
            groupname = (TextView) view.findViewById(R.id.groupname);
            groupid = (TextView) view.findViewById(R.id.groupid);
            image = (ImageView) view.findViewById(R.id.image);

        }
    }
}

