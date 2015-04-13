package com.android.khel247.fragments;



import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.khel247.R;
import com.android.khel247.asynctasks.membertasks.UsernameCheckTask;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.UtilityMethods;
import com.android.khel247.utilities.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;


/**
 * A simple {@link android.support.v4.app.Fragment} fragment class that lets the user enter an email,
 * username, password for registration.
 *
 */
public class RegisterFragment extends Fragment {
    private static final String LOG_TAG = RegisterFragment.class.getSimpleName();
    private static final int DEFAULT_TIMER_DELAY = 1500; // im milli seconds

    private Context context;

    private EditText mUsernameTF;
    private AutoCompleteTextView mEmailTF;
    private EditText mPasswordTF;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        context = getActivity();

        final Button registerBtn = (Button)rootView.findViewById(R.id.next_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String username = mUsernameTF.getText().toString().trim();
                String email = mEmailTF.getText().toString().trim().toLowerCase();
                String password = mPasswordTF.getText().toString().trim();

                // validate input
                if(isInputValid(username, email, password)){
                    // navigate to the next activity (FillProfileActivity)
                    nextFragmentAction(email, username, password);
                }
            }
        });

        mEmailTF = (AutoCompleteTextView)rootView.findViewById(R.id.register_email_tf);
        List<String> possibleEmails = getEmailsFromAccountManager();
        if(possibleEmails.size() > 0){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.autocomplete_item_view, possibleEmails);
            mEmailTF.setAdapter(adapter);
            // set the first email in the list
            mEmailTF.setText(possibleEmails.get(0)); // just take the first email
            UtilityMethods.setCursorToEndOfText(mEmailTF);
        }

        mEmailTF.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String email = mEmailTF.getText().toString().trim().toLowerCase();
                if (!hasFocus) { // lost focus
                    if(isEmailValid(email)){ // if valid email
                        String extractedUsername = extractUsername(email);
                        mUsernameTF.setText(extractedUsername);
                    }
                }
            }
        });

        mUsernameTF = (EditText)rootView.findViewById(R.id.register_username_tf);
        mUsernameTF.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && isUsernameValid(mUsernameTF.getText().toString())) {
                    checkUsername(registerBtn);
                }
            }
        });

        mUsernameTF.addTextChangedListener(new TextWatcher() {
            Timer mTimer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // cancel the timer cause another key has been pressed
                mTimer.cancel();
                // need to intialize the timer again because cancel() stops letting the timer
                // schedule new tasks
                mTimer = new Timer();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void afterTextChanged(Editable editable) {
                checkUsernameAfterDelay(mTimer, registerBtn);
            }
        });

        mPasswordTF = (EditText)rootView.findViewById(R.id.register_password_tf);
        // to change the font of the password hint to normal
        mPasswordTF.setTypeface(Typeface.DEFAULT);
        mPasswordTF.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        // get the 'Show Password' checkbox
        CheckBox showPWCheckBox = (CheckBox)rootView.findViewById(R.id.show_password_cb);
        showPWCheckBox.setChecked(true);

        // add onCheckedChanged listener which handles events when a checkbox is checked or unchecked
        showPWCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    // show password
                    mPasswordTF.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    UtilityMethods.setCursorToEndOfText(mPasswordTF);
                }else{
                    // hide password
                    mPasswordTF.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    UtilityMethods.setCursorToEndOfText(mPasswordTF);
                }
            }
        });

        return rootView;
    }


    /**
     * Checks the AccountManager and fetches an email address
     * @return
     */
    private List<String> getEmailsFromAccountManager() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(getActivity()).getAccounts();
        List<String> emails = new ArrayList<>();
        for(Account account : accounts){
            String accountName = account.name;
            if(emailPattern.matcher(accountName).matches() && !emails.contains(accountName)){
                emails.add(account.name);
            }
        }
        return emails;
    }


    /**
     * Invokes a call to the server to check if the input username is available after DEFAULT_TIMER_DELAY
     * milliseconds of delay
     * @param timer : the Timer object
     * @param registerBtn
     */
    private void checkUsernameAfterDelay(final Timer timer, final Button registerBtn){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // connects to the server and checks if the username is available
                checkUsername(registerBtn);
                // remove all previously cancelled tasks
                timer.purge();
            }
        }, DEFAULT_TIMER_DELAY);
    }


    /**
     * Makes a call to the server to check if the username entered is taken
     * @param registerBtn
     */
    private void checkUsername(Button registerBtn){
        UsernameCheckTask task = new UsernameCheckTask(context,
                mUsernameTF.getText().toString(), registerBtn);
        task.execute();
    }

    /**
     * Displays the next fragment which lets the user enter details about himself
     */
    private void nextFragmentAction(String email, String username, String password){
        CompleteProfileFragment fragment = CompleteProfileFragment.newInstance(username, email, password);
        FragmentManager fm  = getFragmentManager();
        fm.beginTransaction().replace(R.id.register_container, fragment).commit();
    }

    /** Validates the input of the user to the registration form
     *
     * @param username
     * @param email
     * @param password
     * @return
     */
    private boolean isInputValid(String username, String email, String password) {
        if(!isUsernameValid(username)){
            return false;
        }else if(!isEmailValid(email)){
            return false;
        }else if(!isPasswordValid(password)){
            return false;
        }else
            return true;

    }


    /** Validates the username input by the user
     *
     * @param username : input username text
     * @return : true if valid, false otherwise
     */
    private boolean isUsernameValid(String username) {
        if(isFieldEmpty(username, MessageService.EMPTY_USERNAME))
            return false;
        if(!Validator.isUsernameValid(username)) {
            UtilityMethods.showShortToast(context, MessageService.INVALID_USERNAME);
            return false;
        }
        return true;
    }

    /**
     * Validates the email input by the user
     * @param email : input email text
     * @return : true if valid , false otherwise
     */
    private boolean isEmailValid(String email) {
        if(isFieldEmpty(email, MessageService.EMPTY_EMAIL))
            return false;
        if(!Validator.isEmailValid(email)) {
            Toast.makeText(context, MessageService.INVALID_EMAIL, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    /**
     * Validates password input
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        if(isFieldEmpty(password, MessageService.EMPTY_PASSWORD))
            return false;
        if(!Validator.isPasswordValid(password)) {
            Toast.makeText(context, MessageService.INVALID_PASSWORD, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }



    /**
     * Checks if the input string is empty, if it is, it displays a Toast message
     * @param str : string to be checked
     * @param errMsg : error message to be displayed
     * @return
     */
    private boolean isFieldEmpty(String str, String errMsg) {
        if(TextUtils.isEmpty(str)){
            Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
            return true;
        }else {
            return false;
        }
    }


    /**
     * Extracts a username from the local part of the email address
     * @param email : the input email address
     * @return
     */
    private String extractUsername(String email) {
        String localPart = email.substring(0, email.indexOf("@"));
        // replace everything that is not a word character (a-z in any case, 0-9 or _) or whitespace.
        localPart.replaceAll("[^\\w\\s]","");
        localPart.trim();
        return localPart;
    }




}