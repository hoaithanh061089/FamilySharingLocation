package thanhnguyen.com.familysharinglocation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by THANHNGUYEN on 12/3/17.
 */

public class MyGroupMemberAdapter extends RecyclerView.Adapter<MyGroupMemberAdapter.MyViewHolder> {

    private List<String> memberList;

    public MyGroupMemberAdapter(List<String> memberList) {
        this.memberList = memberList;
    }


    @Override
    public MyGroupMemberAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mygroupmember_list_row, parent, false);

        return new MyGroupMemberAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyGroupMemberAdapter.MyViewHolder holder, int position) {

       String member_name = memberList.get(position);
       holder.membername.setText(member_name);



    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView membername;

        public MyViewHolder(View view) {
            super(view);
            membername = (TextView) view.findViewById(R.id.membername);


        }
    }
}

