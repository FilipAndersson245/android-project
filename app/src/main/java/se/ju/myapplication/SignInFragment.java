package se.ju.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fasterxml.jackson.core.JsonProcessingException;

public class SignInFragment extends Fragment {
    private static final String TAG = "MyActivity";

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
            Connection.getInstance().signInUser(usernameField.getText().toString(), passwordField.getText().toString(), nothing -> {
                // Signed in
                System.out.println("!!!!!!!!!!!!!!!! SIGNED IN");

                getView().post(()->{
                    getFragmentManager().popBackStackImmediate();
                });

            });
            // Not signed in
            System.out.println("!!!!!!!!!!!!!!!! 1");
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("SignInFragment.onDestroy");
    }
}