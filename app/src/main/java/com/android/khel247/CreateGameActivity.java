package com.android.khel247;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.android.khel247.constants.Constants;
import com.android.khel247.fragments.CreateGameFragment;
import com.android.khel247.model.Token;

import java.io.Serializable;


public class CreateGameActivity extends ActionBarActivity {

    private Token mAuthToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        Intent intent = getIntent();
        if(intent != null){
            mAuthToken = (Token)intent.getSerializableExtra(Constants.ARG_TOKEN);
        }
        if(intent == null || mAuthToken == null){
            startActivity(new Intent(this, MainActivity.class));
        }

        if(savedInstanceState == null){
            CreateGameFragment fragment = CreateGameFragment.newInstance(mAuthToken);
            getSupportFragmentManager().beginTransaction().add(R.id.create_game_container, fragment)
            .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_create_game, menu);
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
