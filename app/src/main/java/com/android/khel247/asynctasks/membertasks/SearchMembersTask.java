package com.android.khel247.asynctasks.membertasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

import com.android.khel247.adapters.MyPeopleListAdapter;
import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.model.PeopleItem;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.memberEndpoint.MemberEndpoint;
import com.khel247.backend.memberEndpoint.model.MemberResults;
import com.khel247.backend.memberEndpoint.model.MemberWidget;
import com.khel247.backend.memberEndpoint.model.WrappedBoolean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Calls the memberEndpoint api method searchMember() and gets back a list of members that match the user's input string
 * Created by Misaal on 07/12/2014.
 */
public class SearchMembersTask extends AsyncTask<String, Void, List<PeopleItem>> {

    private final Context context;
    private final MyPeopleListAdapter mAdapter;
    private String ownerUsername;
    private MemberEndpoint memberEndpoint;

    public SearchMembersTask(Context context, String username, MyPeopleListAdapter adapter){
        this.context = context;
        this.ownerUsername = username;
        this.mAdapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(context)){
            cancel(true);
            return;
        }
    }


    @Override
    protected void onCancelled(List<PeopleItem> resultList) {
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }

    @Override
    protected List<PeopleItem> doInBackground(String... strings) {
        if (isCancelled()) // checks if the task has been cancelled. If yes, the execution never goes
            return null;   //  to onPostExecute

        if(strings == null || strings.length < 1 || strings[0].isEmpty()){
            return null; // invalid search query
        }
        String searchStr = strings[0];
        boolean isEmail = analyzeSearchString(searchStr);


        memberEndpoint = EndpointUtil.getMemberEndpointService();
        MemberResults searchResults = null;
//
//        try {
//            searchResults = memberEndpoint.searchMember(ownerUsername, searchStr, isEmail).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // check the results returned from the server
        if(searchResults == null || searchResults.getMemberList() == null){
            return null;
        }

        // return a List<PeopleItem> which is created from the List<MemberWidget> returned from the server
        return convertToPeopleItemList(searchResults.getMemberList());
    }


    /**
     * Analyzes the string to check if it is an email address.
     * Create a separate method so that in the future, can add analysis for other parameters
     * @param searchStr
     * @return : true if the searchStr is an email address
     */
    private boolean analyzeSearchString(String searchStr) {
        return UtilityMethods.isEmailAddress(searchStr);
    }


    @Override
    protected void onPostExecute(List<PeopleItem> peopleItems) {
        if(peopleItems == null){
            return;
        }

        addItemsToAdapter(peopleItems);

    }


    /**checks if the items returned by the search are already present in the adapter.
     * Adds items only if they are not present
     *
     * @param peopleItems
     */
    private void addItemsToAdapter(List<PeopleItem> peopleItems) {
        // iterate through the input items
        for(PeopleItem item : peopleItems){
            if(mAdapter.contains(item)){ // if the adapter contains the item,
                Toast t = Toast.makeText(context, item.getUsername(), Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP|Gravity.CENTER, 0, 5);
                peopleItems.remove(item); // remove the item from the original list
            }
        }

        if(peopleItems.size() == 1){ // if there's only 1 item left in the list
            mAdapter.add(peopleItems.get(0));
        }else if(peopleItems.size() > 1)  // if there is more than 1 item
            mAdapter.addAll(peopleItems);
    }


    /**
     * Fetches data from the List<MemberWidget> and creates a List<PeopleItem> as needed by the adapter
     *
     * @param list<MemberWidget> : a list of the contacts with some information, sent from the serveras
     * @return : List<PeopleItem>
     *
     */
    private List<PeopleItem> convertToPeopleItemList(List<MemberWidget> list){
        List<PeopleItem> contactList = new ArrayList<>();
        if(list != null && list.size() > 0) {
            for (MemberWidget widget : list) {
                PeopleItem item = new PeopleItem(widget.getUsername(), widget.getName(), widget.getContact());
                contactList.add(item);
            }
        }

        return contactList;
    }
}
