package com.android.khel247.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.khel247.R;
import com.android.khel247.model.PeopleItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Misaal on 12/12/2014.
 */
public class GameInvitesAdapter extends ArrayAdapter {

    private final Context context;
    private final List<String> invitedList;
    private List<PeopleItem> filteredList;
    private ArrayList<PeopleItem> fullList;
    private PeopleFilter filter;

    public GameInvitesAdapter(Context context, List<PeopleItem> items, List<String> invitedUsernames){
        super(context, R.layout.invite_people_list_item, items);
        this.context = context;
        this.filteredList = items;
        this.invitedList = invitedUsernames;
    }


    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public PeopleItem getItem(int index) {
        return filteredList.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View view, final ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            // inflates view using the LayoutInflater. null passed for view group to show that
            // there are no hierarchical parents?
            view = inflater.inflate(R.layout.invite_people_list_item, null);
        }

        // get components to insert into the listview row
        TextView txtName = (TextView) view.findViewById(R.id.invite_people_fullname_txt);
        TextView txtUsername = (TextView) view.findViewById(R.id.invite_people_username_txt);

        // get the item that holds the data of each member in the list
        final PeopleItem item = filteredList.get(index);

        // display fullname to the view
        txtName.setText(item.getFullName());
        final String username = item.getUsername();
        // display username
        txtUsername.setText(item.getUsername());

        // the button used to invite people to a game
        ImageButton unInviteBtn = (ImageButton) view.findViewById(R.id.invite_people_btn);
        unInviteBtn.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
        unInviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove(item); // remove from list
                invitedList.remove(item.getUsername());
            }
        });


        return view;
    }

    /**
     * Checks if the adapter already contains the same PeopleItem object as the input
     * @param item
     * @return
     */
    public boolean contains(PeopleItem item){
        for(int i = 0; i < getCount(); i++){
            PeopleItem heldItem = getItem(i);
            if(heldItem.getUsername().equals(item.getUsername())){
                return true;
            }
        }
        return false;
    }


    /**
     * returns the username of the person corresponding to the item at index position in the list
     * @param index : position of the item in the list
     * @return : person's username
     */
    public String getUsernameOfPerson(int index){
        return fullList.get(index).getUsername();
    }

    /**
     * returns the fullname of the person corresponding to the item at index position in the list
     * @param index : position of the item in the list
     * @return : fullname of the person to whom the item corresponds
     */
    public String getFullNameOfPerson(int index){
        return fullList.get(index).getFullName();
    }


    @Override
    public Filter getFilter(){
        if(filter == null)
            filter = new PeopleFilter();
        return filter;
    }



    private class PeopleFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();

            if(fullList == null ){
                fullList = new ArrayList<>(filteredList);
            }

            String searchStr = charSequence.toString();

            if(searchStr != null && searchStr.trim().length() > 0){
                List<PeopleItem> filteredList = new ArrayList<>();
                for(int i = 0; i < fullList.size(); i++){
                    if(isStringMatch(searchStr, i)){
                        filteredList.add(fullList.get(i));
                    }
                }
                result.count = filteredList.size();
                result.values = filteredList;

            }else{
                result.values = fullList;
                result.count = fullList.size();
            }

            return result;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            filteredList = (List<PeopleItem>)results.values;
            if(results.count > 0)
                notifyDataSetChanged();
            else
                notifyDataSetInvalidated();
        }


        /**
         * searches through the username and the fullname of the member at 'index' and returns the
         * @param searchStr
         * @return
         */
        private boolean isStringMatch(String searchStr, int index) {
            String username = getUsernameOfPerson(index).toLowerCase();
            String fullname = getFullNameOfPerson(index).toLowerCase();
            searchStr = searchStr.toLowerCase();
            if(fullname.contains(searchStr)) // if the searchStr matches fullname
                return true;
            else if(username.contains(searchStr)) // if the searchStr matches username
                return true;
            else
                return false;
        }

    }

}
