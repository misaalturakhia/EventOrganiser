package com.android.khel247.asynctasks.membertasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.constants.DialogMessages;
import com.android.khel247.model.PeopleItem;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.memberEndpoint.MemberEndpoint;
import com.khel247.backend.memberEndpoint.model.LoginToken;
import com.khel247.backend.memberEndpoint.model.MemberResults;
import com.khel247.backend.memberEndpoint.model.MemberWidget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Misaal on 03/12/2014.
 */
public class FetchContactsTask extends AsyncTask<Void, Void, List<PeopleItem>> {

    private final ArrayAdapter mAdapter;
    private final String mUsername;
    private final LoginToken loginToken;
    MemberEndpoint memberEndpoint = null;
    private final Context context;
    private ProgressDialog mProgressDialog;

    public FetchContactsTask(Context context, ArrayAdapter adapter,Token token, String username){
        this.context = context;
        this.mAdapter = adapter;
        this.loginToken = Token.createMemberEndpointLoginToken(token);
        this.mUsername = username;
    }

    public FetchContactsTask(Context context, ArrayAdapter adapter, Token token){
        this(context, adapter, token, token.getUsername());
    }


    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(context)){
            cancel(true);
            return;
        }
        mProgressDialog = NetworkTaskMethods.showLoadingDialog(context, DialogMessages.LOADING);
    }


    @Override
    protected void onCancelled(List<PeopleItem> resultItems) {
        if(mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }


    @Override
    protected List<PeopleItem> doInBackground(Void... voids) {
        if (isCancelled()) // checks if the task has been cancelled. If yes, the execution never goes
            return null;   //  to onPostExecute

        memberEndpoint = EndpointUtil.getMemberEndpointService();
        MemberResults result = null;
        try {
            result = memberEndpoint.getContacts(mUsername, loginToken).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }
        List<PeopleItem> contactList = new ArrayList<>();
        if(result != null){
            contactList = convertToPeopleItemList(result.getMemberList());
        }

        return contactList;
    }

    @Override
    protected void onPostExecute(List<PeopleItem> contacts) {
        UtilityMethods.dismissDialog(mProgressDialog);
        if(contacts.size() == 1){ // if only one contact
            mAdapter.add(contacts.get(0));
        } else if(contacts.size() > 1) // if more than one
            mAdapter.addAll(contacts);
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
