package com.android.khel247.fragments;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.khel247.MainActivity;
import com.android.khel247.R;
import com.android.khel247.asynctasks.membertasks.GetProfileTask;
import com.android.khel247.dialogs.ViewContactsDialogFragment;
import com.android.khel247.model.MemberData;
import com.android.khel247.model.Token;
import com.android.khel247.constants.Constants;
import com.android.khel247.utilities.UtilityMethods;

/**
 * A fragment that displays the member's profile page
 */
public class MemberProfileFragment extends Fragment implements GetProfileTask.GetProfileListener{

    private Token authToken;
    private boolean isOwnProfile = false;
    private View mRootView;
    private MemberData mMemberData;
    private GetProfileTask.GetProfileListener mProfileLoadedListener;
    private String mProfileUsername;
    private LinearLayout mProfileLayout;
    private ProgressBar mLoader;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided authentication token.
     *
     * @param token : the member's login token
     * @return A new instance of fragment MemberProfile.
     */
    public static MemberProfileFragment newInstance(Token token, String profileUsername) {
        MemberProfileFragment fragment = new MemberProfileFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_PROFILE_USERNAME, profileUsername);
        args.putSerializable(Constants.ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }
    public MemberProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            authToken= (Token)args.getSerializable(Constants.ARG_TOKEN);
            mProfileUsername = args.getString(Constants.ARG_PROFILE_USERNAME);
            if(authToken == null || mProfileUsername == null){
                navigateToMain();
                return;
            }
        }else{
            navigateToMain();
            return;
        }

        // check and set flag if the profile is the user's own profile. Used to activate the edit
        // profile interface
        if(authToken.getUsername().equals(mProfileUsername)){
            // set flag that this is the user's own profile
            isOwnProfile = true;
        }

    }


    /**
     * Navigate to MainActivity
     */
    private void navigateToMain(){
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_member_profile, container, false);

        // pass the pointer to mMemberData to the task and it will populate it in its onPostExecute()
        GetProfileTask task = new GetProfileTask(getActivity(), this);
        task.execute(mProfileUsername);

        return mRootView;
    }


    private void populateFields(){
        // the linear layout that holds the entire profile layout
        mProfileLayout = (LinearLayout)mRootView.findViewById(R.id.member_profile_layout);
        mProfileLayout.setVisibility(View.VISIBLE);

        final String nameText = mMemberData.getFullName();
        fetchAndPopulateTextView(R.id.member_profile_fullname_text, nameText);

        String positionText = createPositionText();
        fetchAndPopulateTextView(R.id.member_profile_position, positionText);

        String usernameText = mMemberData.getUsername();
        fetchAndPopulateTextView(R.id.member_profile_username_text, usernameText);

        String emailText = mMemberData.getEmailAddress();
        fetchAndPopulateTextView(R.id.member_profile_email_text, emailText);

        String areaCityText = createAreaCityText();
        fetchAndPopulateTextView(R.id.member_profile_city_text, areaCityText);

        String countryText = mMemberData.getCountry();
        fetchAndPopulateTextView(R.id.member_profile_country_text, countryText);

        String noOfContacts = String.valueOf(mMemberData.getNoOfContacts());
        fetchAndPopulateTextView(R.id.member_profile_no_of_contacts_text, noOfContacts);

        Button viewContactsButton = (Button)mRootView.findViewById(R.id.member_profile_view_contacts_btn);
        viewContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewContactsDialogFragment fragment = ViewContactsDialogFragment.newInstance(authToken,
                        mMemberData.getFullName(), mMemberData.getUsername());
                fragment.show(getFragmentManager(), "viewContacts");
            }
        });

    }


    private String createPositionText() {
        String position = mMemberData.getPosition();
        if(position.contains(")"))
            position = position.substring(position.indexOf(")") + 1, position.length());
        return position;
    }


    /**
     * Creates the text to display the area and the city in one line
     * @return
     */
    private String createAreaCityText() {
        String area = mMemberData.getArea();
        String city = mMemberData.getCity();

        StringBuilder builder = new StringBuilder();
        if(!UtilityMethods.isEmptyOrNull(area)){
            builder.append(area);
            builder.append(", ");
        }
        builder.append(city);
        return builder.toString();
    }


    /**
     * Fetches the textview identified by resourceId and sets the input text as its text
     * @param resourceId : identifier of textview
     * @param text : text to set to the textview
     */
    private TextView fetchAndPopulateTextView(int resourceId, String text) {
        if(text == null){
            text = "";
        }
        // get textview and set text
        TextView textView = (TextView) mRootView.findViewById(resourceId);
        textView.setText(text);
        return textView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mProfileLoadedListener = this;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // free listener resources
        mProfileLoadedListener = null;
    }

    @Override
    public void onProfileLoaded(MemberData data) {
        if(data == null){
            UtilityMethods.showShortToast(getActivity(), "Couldnt load profile");
            return;
        }
        mMemberData = data;
        populateFields();
    }
}
