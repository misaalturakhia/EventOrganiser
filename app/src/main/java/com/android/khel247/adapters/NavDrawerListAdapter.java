package com.android.khel247.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.khel247.R;
import com.android.khel247.model.NavDrawerItem;

import java.util.ArrayList;

/**
 * Created by Misaal on 25/11/2014.
 */
public class NavDrawerListAdapter extends BaseAdapter {

    private final ArrayList<NavDrawerItem> navDrawerItems;
    private Context context;

    /**
     * Constructor
     * @param context
     * @param items
     */
    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> items){
        this.context = context;
        this.navDrawerItems =items;
    }


    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int i) {
        return navDrawerItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            // inflates view using the LayoutInflater. null passed for viewgroup to show that
            // there are no hierarchical parents?
            view = inflater.inflate(R.layout.nav_drawer_list_item, null);
        }

        // get components to insert into the listview row
        ImageView imgIcon = (ImageView) view.findViewById(R.id.icon);
        TextView txtTitle = (TextView) view.findViewById(R.id.title);
        TextView txtCount = (TextView) view.findViewById(R.id.counter);

        NavDrawerItem item = navDrawerItems.get(index);
        imgIcon.setImageResource(item.getIcon());
        txtTitle.setText(item.getTitle());

        if(item.getCounterVisibility()){
            txtCount.setText(item.getCount());
        }else
            txtCount.setVisibility(View.GONE);

        return view;
    }
}
