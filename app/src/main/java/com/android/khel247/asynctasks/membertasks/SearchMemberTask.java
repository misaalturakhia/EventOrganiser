package com.android.khel247.asynctasks.membertasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.khel247.adapters.MyPeopleListAdapter;
import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.constants.DialogMessages;
import com.android.khel247.model.PeopleItem;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.memberEndpoint.MemberEndpoint;
import com.khel247.backend.memberEndpoint.model.MemberResults;
import com.khel247.backend.memberEndpoint.model.MemberWidget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Calls the api to search for a single member based on the user's search string
 * Created by Misaal on 05/12/2014.
 */
public class SearchMemberTask extends AsyncTask<String, Void, PeopleItem> {

    private final String username;
    private final Activity mActivity;
    MyPeopleListAdapter mAdapter;
    MemberEndpoint memberEndpoint;
    private ProgressDialog mProgressDialog;

    /**
     * Constructor
     *
     * @param context
     * @param username
     * @param adapter
     */
    public SearchMemberTask(Activity activity, String username, MyPeopleListAdapter adapter) {
        this.mActivity = activity;
        this.username = username;
        this.mAdapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        if (!NetworkTaskMethods.internetCheck(mActivity)) {
            cancel(true);
            return;
        }
        mProgressDialog = NetworkTaskMethods.showLoadingDialog(mActivity, DialogMessages.SEARCHING);
    }

    @Override
    protected void onCancelled(PeopleItem item) {
        UtilityMethods.dismissDialog(mProgressDialog);
        Toast.makeText(mActivity, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }

    @Override
    protected PeopleItem doInBackground(String... strings) {
        if (isCancelled()) //checks if the task has been canceled
            return null;

        String searchStr = strings[0];
        if (UtilityMethods.isEmptyOrNull(searchStr)) {
            return null;
        }
        boolean isEmail = analyzeSearchString(searchStr);

        memberEndpoint = EndpointUtil.getMemberEndpointService();

        MemberWidget result = null;
        try {
            result = memberEndpoint.searchMember(username, searchStr, isEmail).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }

        if (result == null) {
            return null;
        }

        PeopleItem item = new PeopleItem(result.getUsername(), result.getName(), result.getContact());
        return item;
    }

    /**
     * Analyzes the string to check if it is an email address.
     * Create a separate method so that in the future, can add analysis for other parameters
     *
     * @param searchStr
     * @return : true if the searchStr is an email address
     */
    private boolean analyzeSearchString(String searchStr) {
        return UtilityMethods.isEmailAddress(searchStr);
    }


    @Override
    protected void onPostExecute(final PeopleItem peopleItem) {
        mProgressDialog.dismiss();
        if (peopleItem != null)
            mAdapter.add(peopleItem);

    }
}