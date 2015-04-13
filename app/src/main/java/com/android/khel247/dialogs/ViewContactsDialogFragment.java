package com.android.khel247.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.khel247.ProfileActivity;
import com.android.khel247.R;
import com.android.khel247.adapters.MyPeopleListAdapter;
import com.android.khel247.asynctasks.membertasks.FetchContactsTask;
import com.android.khel247.constants.Constants;
import com.android.khel247.model.PeopleItem;
import com.android.khel247.model.Token;
import com.android.khel247.utilities.UtilityMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** A dialogfragment that displays a member's contacts
 * Created by Misaal on 30/12/2014.
 */
public class ViewContactsDialogFragment extends DialogFragment {

    private static final String ARG_FULL_NAME = "FullName";
    private static final String DIALOG_TITLE = "Contacts";
    private String mUsername;
    private Token mAuthToken;
    private MyPeopleListAdapter mAdapter;
    private ListView mListView;
    private String mFullName;
    private boolean isOwnContacts;
    private TextView emptyTextView;
    private ProgressBar mLoadingContactsBar;


    /**
     *
     * @param token - The user's login token
     * @param fullName - The fullname of the user
     * @param username - the username of the person whos contacts they are
     * @return
     */
    public static ViewContactsDialogFragment newInstance(Token token, String fullName, String username) {
        ViewContactsDialogFragment fragment = new ViewContactsDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_USERNAME, username);
        args.putString(ARG_FULL_NAME, fullName);
        args.putSerializable(Constants.ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null){
            mFullName = args.getString(ARG_FULL_NAME);
            mAuthToken = (Token)args.getSerializable(Constants.ARG_TOKEN);
            mUsername = args.getString(Constants.ARG_USERNAME);
        }
        if(mAuthToken != null && mUsername != null){
            isOwnContacts = mUsername.equals(mAuthToken.getUsername());
        }
        mAdapter = new MyPeopleListAdapter(getActivity(), mAuthToken, new ArrayList<PeopleItem>());

    }


    /**
     * Populates the list adapter with contact information by making  call to the server
     */
    private void updateContacts() {
        FetchContactsTask task = new FetchContactsTask(getActivity(), mAdapter, mAuthToken, mUsername);
        task.execute();
        List<PeopleItem> contacts = new ArrayList<>();
        try {
            contacts = task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(contacts == null || contacts.size() < 1){
            // set empty view to text saying that there are no contacts and hide the progress bar
            mLoadingContactsBar.setVisibility(View.GONE);
            setNoContactsTextView();
        }
    }


    /**
     * Sets the Textview that says that the member has no contacts as the empty view of the listview
     */
    private void setNoContactsTextView(){
        emptyTextView.setText(buildNoContactsText());
        emptyTextView.setVisibility(View.VISIBLE);
        mListView.setEmptyView(emptyTextView);
    }


    /**
     * Checks if the dialog is displaying the users own contacts and set the text displayed accordingly
     * @return
     */
    private String buildNoContactsText(){
        if(isOwnContacts){
            return "You have no contacts";
        }else{
            return mFullName + " has no contacts";
        }
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View contentView = getContentView();
        String dialogTitle = buildDialogTitle();
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity())
                .setTitle(dialogTitle)
                .setView(contentView)
                .setPositiveButton(Constants.DIALOG_DONE_BTN_TEXT, null);

        return builder.create();
    }


    /**
     * Builds the dialog title based on if the contacts is the user's own contacts or somebody
     * else's contacts
     * @return
     */
    private String buildDialogTitle() {
        if(isOwnContacts){
            return DIALOG_TITLE;
        }else{
            return mFullName+"'s "+DIALOG_TITLE;
        }
    }


    /**
     * Build the view of the dialog
     * @return
     */
    private View getContentView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_view_contacts, null);

        emptyTextView = (TextView)view.findViewById(R.id.dialog_view_contacts_emptyView);
        emptyTextView.setVisibility(View.GONE);

        mLoadingContactsBar = (ProgressBar)view.findViewById(R.id.dialog_view_contacts_progress);

        mListView = (ListView)view.findViewById(R.id.dialog_view_contacts_listView);
        mListView.setEmptyView(mLoadingContactsBar);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                PeopleItem item = mAdapter.getItem(index);
                if(item != null){
                    navigateToProfile(item.getUsername());
                }
            }
        });

        updateContacts();
        return view;
    }


    /**
     * Navigate to the appropriate contact's profile page
     * @param username
     */
    private void navigateToProfile(String username) {
        Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(Constants.ARG_TOKEN, mAuthToken);
        extras.putString(Constants.ARG_PROFILE_USERNAME, username);
        profileIntent.putExtras(extras);
        startActivity(profileIntent);
    }
}
