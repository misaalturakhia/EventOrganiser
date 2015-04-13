package com.android.khel247.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.khel247.R;
import com.android.khel247.constants.Constants;
import com.android.khel247.model.Token;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PeopleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PeopleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeopleFragment extends Fragment {


    private Token mAuthToken;

    public static PeopleFragment newInstance(Token token) {
        PeopleFragment fragment = new PeopleFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    public PeopleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAuthToken = (Token)getArguments().getSerializable(Constants.ARG_TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentTabHost tabHost = new FragmentTabHost(getActivity());
        tabHost.setup(getActivity(), getChildFragmentManager(), R.layout.fragment_home);

        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_TOKEN, mAuthToken);

        tabHost.addTab(tabHost.newTabSpec("Tab1").setIndicator("Contacts"),
                MyPeopleFragment.class, args);

        tabHost.addTab(tabHost.newTabSpec("Tab2").setIndicator("Search"),
                SearchPeopleFragment.class, args);

        return tabHost;
    }

}
