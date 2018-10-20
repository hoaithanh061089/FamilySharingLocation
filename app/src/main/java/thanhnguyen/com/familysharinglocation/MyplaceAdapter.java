package thanhnguyen.com.familysharinglocation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by THANHNGUYEN on 11/24/17.
 */

public class MyplaceAdapter extends RecyclerView.Adapter<MyplaceAdapter.MyViewHolder> {

    private List<PlacesClassHelper> placeList;

    public MyplaceAdapter(List<PlacesClassHelper> placeList) {
        this.placeList = placeList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myplaces_list_row, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        PlacesClassHelper place = placeList.get(position);
        holder.placename.setText(place.getName());


    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView placename;

        public MyViewHolder(View view) {
            super(view);
            placename = (TextView) view.findViewById(R.id.placename);

        }
    }
}
