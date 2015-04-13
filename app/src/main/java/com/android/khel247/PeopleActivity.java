package com.android.khel247;

import android.content.Intent;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.android.khel247.adapters.PeopleTabsAdapter;
import com.android.khel247.constants.Constants;
import com.android.khel247.fragments.AttendingGamesFragment;
import com.android.khel247.fragments.InvitedGamesFragment;
import com.android.khel247.fragments.MyPeopleFragment;
import com.android.khel247.fragments.OrganisedGamesFragment;
import com.android.khel247.fragments.PeopleFragment;
import com.android.khel247.fragments.SearchPeopleFragment;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;

public class PeopleActivity extends ActionBarActivity implements
        ActionBar.TabListener {

    private ViewPager viewPager;
    private PeopleTabsAdapter mAdapter;
    private ActionBar actionBar;
    private Token authToken;


    // Tab titles
    private String[] tabs = { "Contacts", "Search"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // get auth token from the calling activity, and return if there is no authtoken
        if(getIntent() != null){
            Bundle args = getIntent().getExtras();
            authToken = (Token)args.getSerializable(Constants.ARG_TOKEN);
        }else{  // in the case that the token is not received, navigate back to the MainActivity
            //
            Toast.makeText(this, MessageService.ERROR_SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
            Intent backToMain = new Intent(this, MainActivity.class);
            startActivity(backToMain);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        /*PeopleFragment fragment = PeopleFragment.newInstance(authToken);
        getSupportFragmentManager().beginTransaction().add(R.id.people_activity_container, fragment)
                .commit();*/

        // Initilization
        actionBar = getSupportActionBar();
        mAdapter = new PeopleTabsAdapter(getSupportFragmentManager(), authToken);
        viewPager = (ViewPager) findViewById(R.id.people_pager);

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        // set background color for action bar tab
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#ABABAB")));


        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}

            @Override
            public void onPageScrollStateChanged(int arg0) {}
        });
    }

    /**
     * Lets you switch tabs from inside the tab
     * @param tabIndex
     */
    public void switchSelectedTab(int tabIndex){
        actionBar.getTabAt(tabIndex).select();
    }



    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // on tab selected
        // show respected fragment view
        int position = tab.getPosition();
        viewPager.setCurrentItem(position);

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if(tab.getPosition() == 0)
            mAdapter.getMyPeopleFragment().updateContacts();
    }

}
