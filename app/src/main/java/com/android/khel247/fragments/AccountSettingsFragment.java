package com.android.khel247.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.khel247.MainActivity;
import com.android.khel247.R;
import com.android.khel247.asynctasks.membertasks.ChangePasswordTask;
import com.android.khel247.asynctasks.membertasks.DeleteAccountTask;
import com.android.khel247.constants.DialogMessages;
import com.android.khel247.model.Token;
import com.android.khel247.constants.Constants;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.UtilityMethods;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Created by Misaal on 26/11/2014.
 *
 */
public class AccountSettingsFragment extends Fragment {

    /**
     * The logged in member's authentication token object
     */
    private Token token;
    private EditText newPasswordTF;
    private Button changePasswordBtn;
    private EditText deleteAccountPasswordTF;
    private Button deleteAccountBtn;
    private EditText originalPasswordTF;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param token : The user's login token
     * @return A new instance of fragment AccountSettingsFragment.
     */
    public static AccountSettingsFragment newInstance(Token token) {
        AccountSettingsFragment fragment = new AccountSettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }
    public AccountSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { // get token from arguments sent by MainActivity
            token = (Token)getArguments().getSerializable(Constants.ARG_TOKEN);
            // check contents of token, and if false, call the onStart() method of MainActivity that
            // retrieves a user token from the account manager
            if(!Token.checkToken(token))
                ((MainActivity)getActivity()).onStart();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_account_settings, container, false);

        // UI actions related to the change password functionality
        newPasswordTF = (EditText)rootView.findViewById(R.id.account_change_new_pw_tf);
        // sets the password hint text to the normal font
        newPasswordTF.setTypeface(Typeface.DEFAULT);
        newPasswordTF.setTransformationMethod(new PasswordTransformationMethod());

        final EditText confirmPasswordTF = (EditText)rootView.findViewById(R.id.account_change_conf_pw_tf);
        confirmPasswordTF.setTypeface(Typeface.DEFAULT);
        confirmPasswordTF.setTransformationMethod(new PasswordTransformationMethod());

        originalPasswordTF = (EditText)rootView.findViewById(R.id.account_change_pw_orig_tf);
        // sets the password hint text to the normal font
        originalPasswordTF.setTypeface(Typeface.DEFAULT);
        originalPasswordTF.setTransformationMethod(new PasswordTransformationMethod());

        changePasswordBtn = (Button) rootView.findViewById(R.id.account_change_pw_btn);
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePasswordAction(); // add on click functionality to the CHANGE PASSWORD button
            }
        });

        // on losing focus validate that the new password, and the confirm passwords entered match
        confirmPasswordTF.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    if(!confirmPasswordTF.getText().toString().equals(newPasswordTF.getText().toString())){
                        Toast.makeText(getActivity(), MessageService.PASSWORDS_DONT_MATCH,
                                Toast.LENGTH_SHORT).show();
                        changePasswordBtn.setEnabled(false); // disable button if they dont match
                    }else{
                        changePasswordBtn.setEnabled(true); // enable button if they do
                    }
                }
            }
        });

        // UI actions related to the delete account functionality
        deleteAccountPasswordTF = (EditText)rootView.findViewById(R.id.account_delete_pw_tf);
        // sets the password hint text to the normal font
        deleteAccountPasswordTF.setTypeface(Typeface.DEFAULT);
        deleteAccountPasswordTF.setTransformationMethod(new PasswordTransformationMethod());

        deleteAccountBtn = (Button)rootView.findViewById(R.id.account_delete_btn);
        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccountAction();
            }
        });

        return rootView;
    }

    /**
     * Handles the deactivation of accounts
     */
    private void deleteAccountAction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 0);
        builder.setTitle("DELETE ACCOUNT?").setMessage(DialogMessages.ARE_YOU_SURE_TEXT);
        builder.setPositiveButton(Constants.DIALOG_DONE_BTN_TEXT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String password = deleteAccountPasswordTF.getText().toString();
                if(!UtilityMethods.isEmptyOrNull(password)){
                    DeleteAccountTask task = new DeleteAccountTask(getActivity(), token, password);
                    task.execute();
                }

            }
        }).setNegativeButton(Constants.DIALOG_CANCEL_BTN_TEXT, null);
        builder.create();
    }


    /**
     * Executes the functionality of the change password button
     */
    private void changePasswordAction() {
        String currentPassword = originalPasswordTF.getText().toString();
        String newPassword = newPasswordTF.getText().toString();
        ChangePasswordTask task = new ChangePasswordTask(getActivity(), token, currentPassword,
                newPassword);
        task.execute();

    }


}
