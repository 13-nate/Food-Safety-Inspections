package ca.sfu.cmpt276projectaluminium;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ca.sfu.cmpt276projectaluminium.R;

public class ListAdapter extends BaseExpandableListAdapter {
    private ArrayList<String> r_breif;
    private ArrayList<ArrayList<String>> r_info;
    private Context context;

    public ListAdapter(ArrayList<String> r_breif, ArrayList<ArrayList<String>> r_info, Context context) {
        this.r_breif = r_breif;
        this.r_info = r_info;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return r_breif.size();
    }

    @Override
    public int getChildrenCount(int index) {
        return r_info.get(index).size();
    }

    @Override
    public String getGroup(int index) {
        return r_breif.get(index);
    }

    @Override
    public String getChild(int index, int sub_index) {
        return r_info.get(index).get(sub_index);
    }

    @Override
    public long getGroupId(int index) {
        return index;
    }

    @Override
    public long getChildId(int index, int sub_index) {
        return sub_index;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ViewHolderGroup groupHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.activity_restaurant_detail, parent, false); //restaurantview can be changed in to the name of ur main list
            groupHolder = new ViewHolderGroup();
            groupHolder.group_name = (TextView) convertView.findViewById(R.id.textView5);//can be replaces by id of group
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (ViewHolderGroup) convertView.getTag();
        }
        groupHolder.group_name.setText(r_breif.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderItem itemHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.restaurants_view, parent, false);
            itemHolder = new ViewHolderItem();
            // itemHolder.img_icon = (ImageView) convertView.findViewById(R.id.hazardIcon);
            itemHolder.child_name = (TextView) convertView.findViewById(R.id.critical);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ViewHolderItem) convertView.getTag();
        }
        itemHolder.child_name.setText(r_info.get(groupPosition).get(childPosition));
        return convertView;
    }


    private static class ViewHolderGroup {
        private TextView group_name;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private static class ViewHolderItem {
        //private ImageView img_icon;
        private TextView child_name;
    }
}






