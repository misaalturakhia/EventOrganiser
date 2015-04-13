package com.android.khel247.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.khel247.GameActivity;
import com.android.khel247.MainActivity;
import com.android.khel247.R;
import com.android.khel247.adapters.OrganisedGamesListAdapter;
import com.android.khel247.asynctasks.gametasks.GetOrganisedGamesTask;
import com.android.khel247.constants.Constants;
import com.android.khel247.constants.GameConstants;
import com.android.khel247.model.GameData;
import com.android.khel247.model.Token;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrganisedGamesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrganisedGamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganisedGamesFragment extends Fragment {

    private Token authToken;
    private OrganisedGamesListAdapter mOrganisedGameAdapter;

    /**
     *
     * @param token
     * @return
     */
    public static OrganisedGamesFragment newInstance(Token token) {
        OrganisedGamesFragment fragment = new OrganisedGamesFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    public OrganisedGamesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            authToken = (Token)getArguments().getSerializable(Constants.ARG_TOKEN);
        }

        mOrganisedGameAdapter = new OrganisedGamesListAdapter(getActivity(), new ArrayList<GameData>());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_organised_games, container, false);

        FrameLayout listProgressLayout = (FrameLayout)rootView
                .findViewById(R.id.home_organised_list_progressbar_layout);

        LinearLayout emptyView = (LinearLayout)rootView.findViewById(R.id.home_organised_games_list_empty_view);
        Button createBtn = (Button)rootView.findViewById(R.id.home_organised_list_create_btn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).displayView(4);
            }
        });

        ListView listView = (ListView)rootView.findViewById(R.id.home_organised_games_list_view);
        listView.setEmptyView(listProgressLayout);
        listView.setAdapter(mOrganisedGameAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                GameData data = mOrganisedGameAdapter.getItem(index);
                navigateToGameActivity(data);
            }
        });

        updateList(listProgressLayout, emptyView, listView);
        return rootView;
    }


    /** Calls an AsyncTask that fetches the games the user is invited to
     *
     * @param layout : the view that will be set as the empty view of the listview once the server
     *               call retrieving the games list has been made
     * @param listView : the listview that will display the list of invited games
     */
    private void updateList(FrameLayout progressLayout, LinearLayout layout, ListView listView) {
        GetOrganisedGamesTask organisedGamesTask = new GetOrganisedGamesTask(getActivity(), authToken,
                mOrganisedGameAdapter,progressLayout, layout, listView);
        organisedGamesTask.execute();
    }


    /**
     *
     * @param data
     */
    private void navigateToGameActivity(GameData data) {
        Intent gameIntent = new Intent(getActivity(), GameActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(Constants.ARG_GAME_INTENT_TYPE, Constants.INTENT_LOADED_GAME_DATA);
        extras.putSerializable(Constants.ARG_TOKEN, authToken);
        extras.putSerializable(GameConstants.ARG_GAME_DATA, data);
        gameIntent.putExtras(extras);
        startActivity(gameIntent);
    }

}
