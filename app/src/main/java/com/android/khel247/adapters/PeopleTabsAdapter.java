package com.android.khel247.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.khel247.fragments.MyPeopleFragment;
import com.android.khel247.fragments.SearchPeopleFragment;
import com.android.khel247.model.Token;

/**
 * Created by Misaal on 02/12/2014.
 */
public class PeopleTabsAdapter extends FragmentPagerAdapter {

    private final Token authToken;
    private final int NO_OF_FRAGMENTS = 2;

    private MyPeopleFragment myPeopleFragment;

    private SearchPeopleFragment searchPeopleFragment;

    /**
     *
     * @param manager
     * @param token
     */
    public PeopleTabsAdapter(FragmentManager manager, Token token){
        super(manager);
        this.authToken = token;
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                myPeopleFragment = MyPeopleFragment.newInstance(authToken);
                return myPeopleFragment;
            case 1:
                searchPeopleFragment = SearchPeopleFragment.newInstance(authToken);
                return searchPeopleFragment;
        }
        return null;
    }


    @Override
    public int getCount() {
        return NO_OF_FRAGMENTS;
    }


    public MyPeopleFragment getMyPeopleFragment() {
        return myPeopleFragment;
    }

    public SearchPeopleFragment getSearchPeopleFragment() {
        return searchPeopleFragment;
    }


}
