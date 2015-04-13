package com.android.khel247.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.khel247.ProfileActivity;
import com.android.khel247.R;
import com.android.khel247.adapters.InviteListAdapter;
import com.android.khel247.asynctasks.membertasks.FetchContactsTask;
import com.android.khel247.constants.Constants;
import com.android.khel247.constants.GameConstants;
import com.android.khel247.model.PeopleItem;
import com.android.khel247.model.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Misaal on 11/12/2014.
 */
public class InvitePeopleDialogFragment extends DialogFragment {

    private static final String LOG_TAG = InvitePeopleDialogFragment.class.getSimpleName();
    private static final String INVITE_DIALOG_TITLE = "Invite Contacts";
    private static final String ARG_IS_INVITING_MORE = "more";
    private InviteListAdapter mAdapter;

    private Token mAuthToken;
    private ArrayList<String> mInvitedList;
    InvitePeopleListener mListener;
    private boolean isInvitingMore;
    private LinearLayout mEmptyView;

    public InvitePeopleDialogFragment(){}

    public static InvitePeopleDialogFragment newInstance(Token token, ArrayList<String> invitedUsernames,
                                                         boolean isInvitingMore){
        InvitePeopleDialogFragment fragment = new InvitePeopleDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_TOKEN, token);
        args.putStringArrayList(GameConstants.ARG_INVITED_LIST, invitedUsernames);
        args.putBoolean(ARG_IS_INVITING_MORE, isInvitingMore);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null){
            mAuthToken = (Token)args.getSerializable(Constants.ARG_TOKEN);
            mInvitedList = args.getStringArrayList(GameConstants.ARG_INVITED_LIST);
            isInvitingMore = args.getBoolean(ARG_IS_INVITING_MORE);
        }

        mAdapter = new InviteListAdapter(getActivity(), new ArrayList<PeopleItem>(), mInvitedList,
                isInvitingMore);
        updateContacts();

    }

    private void updateContacts() {
        FetchContactsTask task = new FetchContactsTask(getActivity(), mAdapter,mAuthToken);
        task.execute();
        try {
            if(task.get()!= null && task.get().size() == 0){
                mEmptyView.setVisibility(View.VISIBLE);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (InvitePeopleListener)getTargetFragment();
        } catch (ClassCastException e){
            throw new ClassCastException("Target Fragment must implement dialogListener!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View view = createInviteDialogView();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(INVITE_DIALOG_TITLE)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        List<PeopleItem> items = getInviteData();
                        mListener.onPeopleInvited(items);
                    }
                }).setNegativeButton("Cancel", null);
        return dialogBuilder.create();
    }


    /**
     *
     * @return
     */
    private List<PeopleItem> getInviteData() {
        List<PeopleItem> items = new ArrayList<>();
        for(int i = 0; i < mAdapter.getCount(); i ++){
            PeopleItem item = mAdapter.getItem(i);
            if(mInvitedList.contains(item.getUsername())){
                items.add(item);
            }
        }
        return items;
    }


    private View createInviteDialogView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_invite_people, null);

        mEmptyView = (LinearLayout) view.findViewById(R.id.invite_people_empty_view);
        mEmptyView.setVisibility(View.INVISIBLE);

        ListView myPeopleListView = (ListView)view.findViewById(R.id.invite_people_listview);
        myPeopleListView.setAdapter(mAdapter);
        myPeopleListView.setEmptyView(mEmptyView);
        myPeopleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String username = mAdapter.getItem(i).getUsername();
                navigateToProfile(username);
            }
        });

        return view;
    }

    private void navigateToProfile(String username) {
        Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(Constants.ARG_TOKEN, mAuthToken);
        extras.putString(Constants.ARG_PROFILE_USERNAME, username);
        profileIntent.putExtras(extras);
        startActivity(profileIntent);
    }

    public interface InvitePeopleListener{

        public void onPeopleInvited(List<PeopleItem> items);
    }
}
