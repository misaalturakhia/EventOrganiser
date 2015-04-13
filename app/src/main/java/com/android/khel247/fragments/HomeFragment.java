package com.android.khel247.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.khel247.R;
import com.android.khel247.constants.Constants;
import com.android.khel247.model.Token;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {


    private Token authToken;

    public static HomeFragment newInstance(Token token) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            authToken = (Token)getArguments().getSerializable(Constants.ARG_TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(savedInstanceState != null){
            return container;
        }
        FragmentTabHost tabHost = new FragmentTabHost(getActivity());
        tabHost.setup(getActivity(), getChildFragmentManager(), R.layout.fragment_home);

        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_TOKEN, authToken);

        tabHost.addTab(tabHost.newTabSpec("Tab1").setIndicator("Organised"),
                OrganisedGamesFragment.class, args);

        tabHost.addTab(tabHost.newTabSpec("Tab2").setIndicator("Attending"),
                AttendingGamesFragment.class, args);

        tabHost.addTab(tabHost.newTabSpec("Tab3").setIndicator("Invited"),
                InvitedGamesFragment.class, args);

        return tabHost;
    }



    /**
     * Adds the two fragments that are used to display the home view of MainActivity
     */
    private void addHomeViewFragments() {
        // a fragment that displays the list of organised games
        OrganisedGamesFragment organisedGamesFragment = OrganisedGamesFragment.newInstance(authToken);
        // a fragment that displays the list of invited games
        InvitedGamesFragment invitedGamesFragment = InvitedGamesFragment.newInstance(authToken);
        // get fragment manager
        FragmentManager manager = getFragmentManager();

        // start a FragmentTransaction to add the two fragments
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.home_fragment_container, organisedGamesFragment);
        transaction.add(R.id.home_fragment_container, invitedGamesFragment);
        transaction.commit(); // commit the transaction
    }
}
