package se.ju.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import static java.util.Arrays.sort;

public class MainFeedFragment extends Fragment {
    public static MainFeedFragment newInstance() {
        MainFeedFragment fragment = new MainFeedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadMemes();
    }

    @Override
    public void onStart() {
        super.onStart();

        MainActivity mainActivity = (MainActivity) getActivity();

        assert mainActivity != null;
        mainActivity.updateDrawerMenu();
    }

    void loadMemes() {
        Connection.getInstance().getMemes(null, null, null, null, null, (memesResult) -> {
            assert memesResult instanceof ArrayList;
            ArrayList<Meme> memes = (ArrayList<Meme>) memesResult;

            // Add votes if user is signed in
            if (Connection.getInstance().isSignedIn()) {
                for (Meme meme : memes) {
                    try {
                        Connection.getInstance().getVote(meme.getId(), Connection.getInstance().getSignedInUsername(), (voteResult) -> {
                            Vote vote = (Vote) voteResult;
                            meme.setVote(vote.getVote());
                        });
                    } catch (JsonProcessingException e) {
                        meme.setVote(0);
                    }
                }
            }

            Collections.sort(memes);
            ListView listView = Objects.requireNonNull(getView()).findViewById(R.id.feedListView);

            getView().post(() -> {
                        listView.setAdapter(new MemeViewAdapter(memes, getActivity()));
                        getView().postDelayed(this::updateList, 0);
                    }
            );

        });
    }

    public void updateList() {
        ListView listView = Objects.requireNonNull(getView()).findViewById(R.id.feedListView);
        ArrayAdapter<Meme> adapter = (ArrayAdapter<Meme>) listView.getAdapter();
        getView().post(adapter::notifyDataSetChanged);
    }
}
