package com.android.khel247;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.android.khel247.constants.Constants;
import com.android.khel247.fragments.MemberProfileFragment;
import com.android.khel247.model.Token;


/**
 * Activity that displays a member profile
 */
public class ProfileActivity extends ActionBarActivity {

    private Token mAuthToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        String profileUsername = null;
        if(intent != null){
            mAuthToken = (Token)intent.getSerializableExtra(Constants.ARG_TOKEN);
            profileUsername = intent.getStringExtra(Constants.ARG_PROFILE_USERNAME);

            // if input is null, navigate to main
            if(mAuthToken == null || profileUsername == null){
                navigateToMain();
                return;
            }

        }else{
            navigateToMain();
            return;
        }

        if (savedInstanceState == null) {
            MemberProfileFragment profileFragment = MemberProfileFragment.newInstance(mAuthToken, profileUsername);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.profile_container, profileFragment)
                    .commit();
        }
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
