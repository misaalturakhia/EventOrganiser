package com.android.khel247.asynctasks.membertasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.khel247.adapters.MyPeopleListAdapter;
import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.model.PeopleItem;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.memberEndpoint.MemberEndpoint;
import com.khel247.backend.memberEndpoint.model.LoginToken;
import com.khel247.backend.memberEndpoint.model.MemberResults;
import com.khel247.backend.memberEndpoint.model.MemberWidget;
import com.khel247.backend.memberEndpoint.model.MembersForm;
import com.khel247.backend.memberEndpoint.model.WrappedBoolean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Misaal on 23/12/2014.
 */
public class FetchPeopleInfoTask extends AsyncTask<Void, Void, List<PeopleItem>> {

    private final Context context;
    private final MyPeopleListAdapter mAdapter;
    private final List<String> peopleList;
    private final LoginToken loginToken;
    private MemberEndpoint memberEndpoint;

    public FetchPeopleInfoTask(Context context,Token token, MyPeopleListAdapter adapter, List<String> people){
        this.context = context;
        this.loginToken  = Token.createMemberEndpointLoginToken(token);
        this.mAdapter = adapter;
        this.peopleList = people;
    }


    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(context)){
            cancel(true);
            return;
        }
    }


    @Override
    protected void onCancelled(List<PeopleItem> items) {
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }

    @Override
    protected List<PeopleItem> doInBackground(Void... voids) {
        memberEndpoint = EndpointUtil.getMemberEndpointService();
        MemberResults result = null;
        MembersForm form = new MembersForm();
        form.setLoginToken(loginToken);
        form.setPeopleUsernames(peopleList);
        try {
            result = memberEndpoint.getPeopleInfo(form).execute();
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
    protected void onPostExecute(List<PeopleItem> peopleItems) {
        if(peopleItems != null && peopleItems.size() != 0){
            if(peopleItems.size() == 1){
                mAdapter.add(peopleItems.get(0));
            }else
                mAdapter.addAll(peopleItems);
        }
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
