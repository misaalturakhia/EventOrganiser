package com.android.khel247.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.khel247.ProfileActivity;
import com.android.khel247.R;

import com.android.khel247.adapters.MyPeopleListAdapter;
import com.android.khel247.asynctasks.membertasks.SearchMemberTask;
import com.android.khel247.constants.Constants;
import com.android.khel247.model.PeopleItem;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.UtilityMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class SearchPeopleFragment extends Fragment implements ListView.OnItemClickListener {

    private static final String EMPTY_VIEW_DEFAULT_TEXT = "";
    private static final int TIMER_DELAY = 1000;


    /**
     * User's authentication token
     */
    private Token authToken;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private MyPeopleListAdapter mAdapter;

    private Timer mTimer;
    private TextView mSearchEmptyView;
    private EditText memberSearchTF;
    private ImageButton mSearchBtn;

    // TODO: Rename and change types of parameters
    public static SearchPeopleFragment newInstance(Token token) {
        SearchPeopleFragment fragment = new SearchPeopleFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchPeopleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            authToken = (Token)getArguments().getSerializable(Constants.ARG_TOKEN);
        }

        List<PeopleItem> peopleList = new ArrayList<>();

        mAdapter = new MyPeopleListAdapter(getActivity(), authToken,peopleList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_people_list, container, false);

        memberSearchTF = (EditText) view.findViewById(R.id.search_people_tf);
        memberSearchTF.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String text = textView.getText().toString().trim();
                if(i == EditorInfo.IME_ACTION_SEARCH){
                    if(!UtilityMethods.isEmptyOrNull(text))
                        searchAction(text);
                }
                return false;
            }
        });

        mSearchBtn = (ImageButton)view.findViewById(R.id.search_people_search_btn);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = memberSearchTF.getText().toString().trim();
                if(!UtilityMethods.isEmptyOrNull(text)){
                    mAdapter.clear();
                    searchAction(text);
                }
            }
        });

        mSearchEmptyView = (TextView) view.findViewById(R.id.search_people_list_empty_view);
        mSearchEmptyView.setText(EMPTY_VIEW_DEFAULT_TEXT);

        // Set the adapter
        mListView = (ListView) view.findViewById(R.id.search_people_listview);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mSearchEmptyView);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }


    /**
     * Searches for a member with the username or emailAddress specified in the search string
     * @param searchStr
     */
    private void searchAction(String searchStr) {
        String searcherUsername = authToken.getUsername();
        if(searchStr.equals(searcherUsername)){// deny search of own username
            Toast.makeText(getActivity(), MessageService.SEARCH_OWN_USERNAME, Toast.LENGTH_SHORT).show();
        }
        SearchMemberTask task = new SearchMemberTask(getActivity(), authToken.getUsername(), mAdapter);
        task.execute(searchStr);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        String username = mAdapter.getUsernameOfPerson(index);
        Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(Constants.ARG_TOKEN, authToken);
        extras.putString(Constants.ARG_PROFILE_USERNAME, username);
        profileIntent.putExtras(extras);
        startActivity(profileIntent);
    }

}
