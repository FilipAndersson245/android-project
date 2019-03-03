package se.ju.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;

public class MainFeedFragment extends Fragment {

    public static MainFeedFragment newInstance() {
        MainFeedFragment fragment = new MainFeedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadMemes();
    }

    void loadMemes() {
        Connection.getInstance().getMemes(null, null, null, null, null, (memesResult) -> {
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

            ListView listView = getView().findViewById(R.id.feedListView);

            getView().post(() ->
                    listView.setAdapter(new MemeViewAdapter(memes, getActivity())));
        });
    }
}
