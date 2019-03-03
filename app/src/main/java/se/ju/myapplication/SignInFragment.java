package se.ju.myapplication;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;

public class SignInFragment extends Fragment {
    public static SignInFragment newInstance() {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_sign_in, container, false
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button signInButton = getView().findViewById(R.id.signInButton);
        EditText usernameField = getView().findViewById(R.id.usernameField);
        EditText passwordField = getView().findViewById(R.id.passwordField);

        signInButton.setOnClickListener(v1 -> {

            Connection.getInstance().signInUser(usernameField.getText().toString(), passwordField.getText().toString(), didSignIn -> {

                if (didSignIn) {
                    getView().post(() -> {
                        dismissKeyboard(getActivity());
                        getFragmentManager().popBackStackImmediate();
                    });
                } else {
                    getView().post(() -> {
                        ((TextView) getView().findViewById(R.id.errorText)).setText("Error: " + Connection.getInstance().signInError);
                    });
                }
            });
        });
    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("SignInFragment.onDestroy");
    }
}