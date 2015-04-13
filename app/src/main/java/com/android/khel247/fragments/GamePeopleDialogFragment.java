package com.android.khel247.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.khel247.ProfileActivity;
import com.android.khel247.R;
import com.android.khel247.adapters.MyPeopleListAdapter;
import com.android.khel247.asynctasks.membertasks.FetchPeopleInfoTask;
import com.android.khel247.constants.Constants;
import com.android.khel247.model.PeopleItem;
import com.android.khel247.model.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *
 */
public class GamePeopleDialogFragment extends DialogFragment{

    private static final String ARG_EMPTY_VIEW_TEXT = "Empty View Text";
    private static final String ARG_DIALOG_TITLE = "Dialog Title";
    private static final String ARG_ORGANISER_USERNAME = "Organiser Username";
    private ArrayList<String> mPeopleList;
    private MyPeopleListAdapter mAdapter;
    private Token authToken;
    private String mEmptyViewText;
    private String mDialogTitle;
    private TextView mEmptyView;

    public static GamePeopleDialogFragment newInstance(Token token, List<String> peopleList,
                             String emptyViewText, String dialogTitle){
        ArrayList<String> peopleArrayList = null;
        if(peopleList != null){
            peopleArrayList = new ArrayList<>(peopleList);
        }else{
            peopleArrayList = new ArrayList<>();
        }
        GamePeopleDialogFragment fragment = new GamePeopleDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_TOKEN, token);
        args.putStringArrayList(Constants.ARG_PEOPLE_LIST, peopleArrayList);
        args.putString(ARG_EMPTY_VIEW_TEXT, emptyViewText);
        args.putString(ARG_DIALOG_TITLE, dialogTitle);
        fragment.setArguments(args);
        return fragment;
    }

    public GamePeopleDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            authToken = (Token)getArguments().getSerializable(Constants.ARG_TOKEN);
            mPeopleList = getArguments().getStringArrayList(Constants.ARG_PEOPLE_LIST);
            mEmptyViewText = getArguments().getString(ARG_EMPTY_VIEW_TEXT);
            mDialogTitle = getArguments().getString(ARG_DIALOG_TITLE);
        }
        mAdapter = new MyPeopleListAdapter(getActivity(), authToken, new ArrayList<PeopleItem>(), true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View contentView = getActivity().getLayoutInflater().inflate(R.layout.dialog_game_people, null);
        populateContentView(contentView);

        updateList();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(contentView)
                .setTitle(mDialogTitle)
                .setPositiveButton(Constants.DIALOG_DONE_BTN_TEXT, null);
        return builder.create();
    }

    /**
     *
     */
    private void updateList() {
        if(mPeopleList == null || mPeopleList.isEmpty())
            return;
//        if(mPeopleList.contains(authToken.getUsername()))
//            mPeopleList.remove(authToken.getUsername());

        FetchPeopleInfoTask task = new FetchPeopleInfoTask(getActivity(), authToken, mAdapter,
                mPeopleList);
        task.execute();
        try {
            if(task.get() != null && task.get().size() == 0){
                mEmptyView.setVisibility(View.VISIBLE);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param contentView
     */
    private void populateContentView(View contentView) {
        mEmptyView = (TextView)contentView.findViewById(R.id.dialog_game_people_listview_empty_view);
        mEmptyView.setVisibility(View.INVISIBLE);
        mEmptyView.setText(mEmptyViewText);

        ListView invitedPeopleList = (ListView)contentView.findViewById(R.id.dialog_game_people_listview);
        invitedPeopleList.setEmptyView(mEmptyView);
        invitedPeopleList.setAdapter(mAdapter);
        invitedPeopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PeopleItem item = mAdapter.getItem(position);
                navigateToProfile(item);
            }
        });
    }

    private void navigateToProfile(PeopleItem item) {
        String username = item.getUsername();
        Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(Constants.ARG_TOKEN, authToken);
        extras.putString(Constants.ARG_PROFILE_USERNAME, username);
        profileIntent.putExtras(extras);
        startActivity(profileIntent);
    }

}
