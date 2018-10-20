package thanhnguyen.com.familysharinglocation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ShareAppCustomAdapter extends BaseAdapter {
	
	Context context;
	ArrayList<ShareAppCustomItem> listitem;
	
	public ShareAppCustomAdapter(Context context, ArrayList<ShareAppCustomItem> listitem){
		
		this.context = context;
		this.listitem = listitem;
	}

	@Override
	public int getCount() {
			
		return listitem.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listitem.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder;
        if(convertView==null){

            LayoutInflater layoutinflaer = LayoutInflater.from(context);
            convertView = layoutinflaer.inflate(R.layout.shareappcustomlayout, null);
            viewholder = new ViewHolder();
            viewholder.appshareicon = (ImageView) convertView.findViewById(R.id.appshareicon);
            viewholder.appsharename = (TextView) convertView.findViewById(R.id.appsharename);
            convertView.setTag(viewholder);


        } else {

            viewholder = (ViewHolder) convertView.getTag();

        }

        ShareAppCustomItem singleitem = listitem.get(position);
        viewholder.appsharename.setText(singleitem.getName());
        viewholder.appshareicon.setImageDrawable(singleitem.getIcon());
		
		return convertView;
	}

	class ViewHolder {

        ImageView appshareicon;
        TextView appsharename;

	}

}
