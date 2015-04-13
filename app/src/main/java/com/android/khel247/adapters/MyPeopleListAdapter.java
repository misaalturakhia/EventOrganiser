package com.android.khel247.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.khel247.R;
import com.android.khel247.asynctasks.membertasks.PinContactTask;
import com.android.khel247.asynctasks.membertasks.UnpinContactTask;
import com.android.khel247.model.PeopleItem;
import com.android.khel247.model.Token;
import com.khel247.backend.memberEndpoint.model.WrappedBoolean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Misaal on 03/12/2014.
 */
public class MyPeopleListAdapter extends ArrayAdapter {

    private static final String LOG_TAG = MyPeopleListAdapter.class.getSimpleName();
    private final Context context;
    private boolean isInviteOnly = false;
    private List<PeopleItem> fullList;
    private List<PeopleItem> filteredList;
    private final Token authToken;
    private boolean pinned;
    private Filter filter;


    /** Constructor
     *
     * @param context
     * @param token
     * @param items
     */
    public MyPeopleListAdapter(Context context,Token token, List<PeopleItem> items){
        this(context, token, items, false);
    }


    /** Constructor
     *
     * @param context
     * @param token
     * @param items
     * @param isInviteOnlyView
     */
    public MyPeopleListAdapter(Context context, Token token, List<PeopleItem> items, boolean isInviteOnlyView){
        super(context, R.layout.people_list_item, items);
        this.context = context;
        this.authToken = token;
        this.filteredList = items;
        this.isInviteOnly = isInviteOnlyView;
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
            view = inflater.inflate(R.layout.people_list_item, null);
        }

        // get components to insert into the listview row
        final TextView txtName = (TextView) view.findViewById(R.id.people_fullname_txt);
        final TextView txtUsername = (TextView) view.findViewById(R.id.people_username_txt);

        // get the item that holds the data of each member in the list
        final PeopleItem item = filteredList.get(index);

        String username = item.getUsername();
        // a flag that identifies if the member has been pinned by the user
        boolean pinned = item.isPinned();

        // display fullname to the view
        txtName.setText(item.getFullName());
        // display username
        txtUsername.setText(username);

        final ImageButton pinBtn = (ImageButton) view.findViewById(R.id.people_pin_btn);

        boolean isOwnUsername = authToken.getUsername().equals(username);

        if(isInviteOnly || isOwnUsername){
            pinBtn.setVisibility(View.GONE);
        }else{
            setPinButtonIcon(pinBtn, pinned);


            pinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean pinned = item.isPinned();
                    if(pinned){// unpin the member from the contact list of the user
                        pinned = !unPinBtnAction(pinBtn, item);
                    }else {// pin the member to the contact list of the user
                        pinned = pinBtnAction(pinBtn, item.getUsername());
                    }
                    // change the button icon that shows the present status of the contact
                    setPinButtonIcon(pinBtn, pinned);
                }
            });
        }

        return view;
    }


    /**
     * returns the username of the person corresponding to the item at index position in the list
     * @param index : position of the item in the list
     * @return : person's username
     */
    public String getUsernameOfPerson(int index){
        return filteredList.get(index).getUsername();
    }

    /**
     * returns the fullname of the person corresponding to the item at index position in the list
     * @param index : position of the item in the list
     * @return : fullname of the person to whom the item corresponds
     */
    public String getFullNameOfPerson(int index){
        return fullList.get(index).getFullName();
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
     * Regulates the imagebutton icon based on whether the user is pinned or not
     * @param button
     * @param pinned
     */
    private void setPinButtonIcon(ImageButton button, boolean pinned) {
        if(pinned){ // show UNPIN image which tell the user tht he can unpin the contact
            button.setImageResource(R.drawable.minus_36);
        }else // show the PIN image
            button.setImageResource(R.drawable.plus_36);
    }

    /**
     * Pin contact to user's people
     * @param view
     */
    private boolean pinBtnAction(ImageButton button, String username) {
        PinContactTask task = new PinContactTask(context, authToken, button);
        task.execute(username);
        return getBooleanResultFromTask(task);
    }


    /**
     * gets the wrapped boolean result from the PinContactTask or UnpinContactTask and returns the
     * result boolean value
     * @param task : a task that returns WrappedBoolean
     * @return
     */
    private boolean getBooleanResultFromTask(AsyncTask task) {
        boolean success = false;
        try {
            WrappedBoolean resultObj = (WrappedBoolean)task.get();
            if(resultObj != null){
                success = resultObj.getResult();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * unpin contact from user's people
     * @param view
     */
    private boolean unPinBtnAction(ImageButton button, PeopleItem item) {
        UnpinContactTask task = new UnpinContactTask(context, authToken, button);
        task.execute(item.getUsername());
        boolean result = getBooleanResultFromTask(task);
//        if(result){
//            remove(item);
//        }
        return result;
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
