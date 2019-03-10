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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterFragment extends Fragment {
    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
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
                R.layout.fragment_register, container, false
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(Connection.getInstance().isSignedIn())
        {
            getView().post(() -> {
                getActivity().onBackPressed();
            });
        }

        Button registerButton = getView().findViewById(R.id.registerButton);
        EditText usernameField = getView().findViewById(R.id.usernameField);
        EditText passwordField = getView().findViewById(R.id.passwordField);

        registerButton.setOnClickListener(v1 -> {
            Connection.getInstance().createUser(usernameField.getText().toString(), passwordField.getText().toString(), didRegister -> {

                if (didRegister) {
                    getView().post(() -> {
                        getView().findViewById(R.id.successText).setVisibility(View.VISIBLE);
                        getView().findViewById(R.id.errorText).setVisibility(View.INVISIBLE);
                    });

                    getView().postDelayed(() -> {
                        dismissKeyboard(getActivity());

                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.removeAndReplaceWithFragment(R.id.nav_sign_in);
                    }, 700);
                } else {
                    getView().post(() -> {
                        ((TextView) getView().findViewById(R.id.errorText)).setText("Error: " + Connection.getInstance().registerError);
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
}