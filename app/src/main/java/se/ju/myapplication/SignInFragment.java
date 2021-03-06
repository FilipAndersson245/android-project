package se.ju.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import se.ju.myapplication.API.Connection;

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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_sign_in, container, false
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(Connection.getInstance().isSignedIn())
        {
            Objects.requireNonNull(getView()).post(() -> Objects.requireNonNull(getActivity()).onBackPressed());
        }

        Button signInButton = getView().findViewById(R.id.signInButton);
        EditText usernameField = getView().findViewById(R.id.usernameField);
        EditText passwordField = getView().findViewById(R.id.passwordField);

        signInButton.setOnClickListener(v1  -> {

            getView().post(() -> {
                view.findViewById(R.id.layoutProgressBar).setVisibility(View.VISIBLE);
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });

            Connection.getInstance().signInUser(usernameField.getText().toString(), passwordField.getText().toString(), didSignIn -> {
                if (didSignIn) {
                    Connection.getInstance().setSession(getActivity());
                    getView().post(() -> {
                        view.findViewById(R.id.layoutProgressBar).setVisibility(View.INVISIBLE);

                        getView().findViewById(R.id.successText).setVisibility(View.VISIBLE);
                        getView().findViewById(R.id.errorText).setVisibility(View.INVISIBLE);
                    });

                    getView().postDelayed(() -> {
                        dismissKeyboard(getActivity());

                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        MainActivity mainActivity = (MainActivity) getActivity();
                        assert mainActivity != null;
                        mainActivity.removeAndReplaceWithFragment(R.id.navHome);

                    }, 700);
                } else {
                    getView().post(() -> {
                        ((TextView) getView().findViewById(R.id.errorText)).setText("Error: " + Connection.getInstance().signInError);
                        view.findViewById(R.id.layoutProgressBar).setVisibility(View.INVISIBLE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
    }
}