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
import com.android.khel247.adapters.InvitedGamesListAdapter;
import com.android.khel247.asynctasks.gametasks.GetAttendingGamesTask;
import com.android.khel247.constants.Constants;
import com.android.khel247.constants.GameConstants;
import com.android.khel247.model.GameData;
import com.android.khel247.model.Token;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AttendingGamesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AttendingGamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendingGamesFragment extends Fragment {


    private Token mAuthToken;
    private InvitedGamesListAdapter mAttendingGameAdapter;

    public static AttendingGamesFragment newInstance(Token token) {
        AttendingGamesFragment fragment = new AttendingGamesFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    public AttendingGamesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAuthToken = (Token)getArguments().getSerializable(Constants.ARG_TOKEN);
        }
        mAttendingGameAdapter = new InvitedGamesListAdapter(getActivity(), new ArrayList<GameData>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_attending_games, container, false);

        FrameLayout listProgressLayout = (FrameLayout) rootView
                .findViewById(R.id.home_attending_list_progressbar_layout);

        LinearLayout emptyView = (LinearLayout)rootView.findViewById(R.id.home_attending_games_list_empty_view);
        Button organiseBtn = (Button)rootView.findViewById(R.id.home_attending_list_create_btn);
        organiseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // navigate to create game activity
                ((MainActivity) getActivity()).displayView(4);
            }
        });

        Button findBtn = (Button)rootView.findViewById(R.id.home_attending_find_games_btn);
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // navigate to find games activity
                ((MainActivity) getActivity()).displayView(3);
            }
        });

        ListView listView = (ListView)rootView.findViewById(R.id.home_attending_games_list_view);
        listView.setEmptyView(listProgressLayout);
        listView.setAdapter(mAttendingGameAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                GameData data = mAttendingGameAdapter.getItem(index);
                navigateToGameActivity(data);
            }
        });

        updateList(listProgressLayout, emptyView, listView);
        return rootView;
    }


    /**
     * Updates the listview with a list of games from the server
     * @param listProgressLayout
     * @param emptyViewLayout
     * @param listView
     */
    private void updateList(FrameLayout listProgressLayout, LinearLayout emptyViewLayout, ListView listView) {
        GetAttendingGamesTask task = new GetAttendingGamesTask(getActivity(), mAuthToken, mAttendingGameAdapter,
                listProgressLayout, emptyViewLayout, listView);
        task.execute();
    }


    /**
     * On cli
     * @param data
     */
    private void navigateToGameActivity(GameData data) {
        Intent gameIntent = new Intent(getActivity(), GameActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(Constants.ARG_GAME_INTENT_TYPE, Constants.INTENT_LOADED_GAME_DATA);
        extras.putSerializable(Constants.ARG_TOKEN, mAuthToken);
        extras.putSerializable(GameConstants.ARG_GAME_DATA, data);
        gameIntent.putExtras(extras);
        startActivity(gameIntent);
    }

}
