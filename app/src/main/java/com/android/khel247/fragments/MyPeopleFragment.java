package com.android.khel247.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.khel247.PeopleActivity;
import com.android.khel247.ProfileActivity;
import com.android.khel247.R;

import com.android.khel247.adapters.MyPeopleListAdapter;
import com.android.khel247.asynctasks.membertasks.FetchContactsTask;
import com.android.khel247.constants.Constants;
import com.android.khel247.model.PeopleItem;
import com.android.khel247.model.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class MyPeopleFragment extends Fragment implements ListView.OnItemClickListener{

    private static final long DELAY_MILLISECONDS = 1000;
    private Token authToken;

    /**
     * The fragment's ListView
     */
    private ListView mListView;

    /**
     * A separate listview for the searched PeopleItem/s
     */
    private ListView mSearchListView;
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private MyPeopleListAdapter mAdapter;

    /**
     * A list of the user's contacts
     */
    private List<PeopleItem> myPeopleList;

    /**
     * the search view component
     */
    private EditText mSearchTF;
    private LinearLayout mListEmptyView;
    private TextView mEmptyViewLink;

    public static MyPeopleFragment newInstance(Token token) {
        MyPeopleFragment fragment = new MyPeopleFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyPeopleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            // retrieve auth token
            authToken = (Token)getArguments().getSerializable(Constants.ARG_TOKEN);
        }

        // list of the user's contacts
        myPeopleList = new ArrayList<>();

        // initialize list adapters
        mAdapter = new MyPeopleListAdapter(getActivity(), authToken, myPeopleList);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_people, container, false);

        final PeopleActivity parentActivity = (PeopleActivity)getActivity();
        
        mSearchTF = (EditText) view.findViewById(R.id.my_people_searchview);
        mSearchTF.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i2, int i3) {
                String searchStr = cs.toString().trim(); // remove leading and trailing white spaces
                mAdapter.getFilter().filter(searchStr);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // the view that is displayed when the list is empty
        mListEmptyView = (LinearLayout) view.findViewById(R.id.my_people_empty_view);
        mEmptyViewLink = (TextView) view.findViewById(R.id.my_people_search_people_link);

        // set underlined text to make the textview look like a link
        String text = getActivity().getString(R.string.my_people_search_people_link_text);
        SpannableString string = new SpannableString(text);
        string.setSpan(new UnderlineSpan(), 0, string.length(), 0);
        mEmptyViewLink.setText(string);

        mEmptyViewLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentActivity.switchSelectedTab(1); // navigate to the search people tab
            }
        });

        // initialize the listview and Set the adapter
        mListView = (ListView) view.findViewById(R.id.my_people_listview);
        mListView.setEmptyView(mListEmptyView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);


        updateContacts();

        return view;
    }


    /**
     *
     */
    public void updateContacts(){
        mAdapter.clear();
        FetchContactsTask task = new FetchContactsTask(getActivity(), mAdapter,authToken);
        task.execute();
        // make sure the results are sorted
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String username = mAdapter.getUsernameOfPerson(position);
        Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(Constants.ARG_TOKEN, authToken);
        extras.putString(Constants.ARG_PROFILE_USERNAME, username);
        profileIntent.putExtras(extras);
        startActivity(profileIntent);
    }
}
