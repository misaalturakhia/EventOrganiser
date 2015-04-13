package com.android.khel247.fragments;

/**
 * Created by Misaal on 06/11/2014.
 */

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.khel247.LoginActivity;
import com.android.khel247.R;
import com.android.khel247.RegisterActivity;
import com.android.khel247.asynctasks.membertasks.LoginTask;
import com.android.khel247.model.LoginCredentials;
import com.android.khel247.constants.Constants;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.UtilityMethods;
import com.android.khel247.utilities.Validator;


/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {

    private String username;
    private String password;
    private EditText passwordTF;
    private CheckBox showPWCheckBox;

    public LoginFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        Bundle args = getArguments();
        if(args != null){
            final EditText usernameTF = (EditText)rootView.findViewById(R.id.login_username_tf);
            usernameTF.setText(args.getString(Constants.ARG_USERNAME));
        }

        Button loginBtn = (Button)rootView.findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchInput(view);
                if(validateInput()){

                    LoginCredentials credentials = new LoginCredentials(username, password);
                    LoginTask task = new LoginTask((LoginActivity)getActivity(), credentials);
                    task.execute();
                }
            }
        });

        // add functionality to the register button
        Button regOpenBtn = (Button)rootView.findViewById(R.id.login_register_btn);
        regOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // navigate to the register activity
                Intent registrationIntent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(registrationIntent);
            }
        });

        // to change the font of the password hint to normal
        passwordTF = (EditText)rootView.findViewById(R.id.login_password_tf);
        passwordTF.setTypeface(Typeface.DEFAULT);
        passwordTF.setTransformationMethod(new PasswordTransformationMethod());

        // get the 'Show Password' checkbox
        showPWCheckBox = (CheckBox)rootView.findViewById(R.id.login_show_password_cb);

        // add onCheckedChanged listener which handles events when a checkbox is checked or unchecked
        showPWCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    // show password
                    passwordTF.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    UtilityMethods.setCursorToEndOfText(passwordTF);
                }else{
                    // hide password
                    passwordTF.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    UtilityMethods.setCursorToEndOfText(passwordTF);
                }
            }
        });

        return rootView;
    }

    /**
     * Validates the details input by the user
     */
    private boolean validateInput() {
        if(username.length() < 1){
            Toast.makeText(getActivity(), MessageService.EMPTY_USERNAME, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!Validator.isUsernameValid(username)){
            Toast.makeText(getActivity(), MessageService.INVALID_EMAIL, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length() < 1){
            Toast.makeText(getActivity(), MessageService.EMPTY_PASSWORD, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    /**
     * Store user input to fields 'username' & 'password'
     * @param view
     */
    private void fetchInput(View view) {
        EditText usernameTF = (EditText)getView().findViewById(R.id.login_username_tf);
        username = usernameTF.getText().toString();

        EditText passwordTextField = (EditText)getView().findViewById(R.id.login_password_tf);
        password = passwordTextField.getText().toString();

    }

    @Override
    public void onPause() {
        super.onPause();

        // if the activity gets paused, revert to default of keeping the password hidden
        showPWCheckBox.setChecked(false);
        passwordTF.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }
}
