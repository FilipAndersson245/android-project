package se.ju.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import se.ju.myapplication.API.Connection;
import se.ju.myapplication.Models.Meme;
import se.ju.myapplication.Models.Vote;

import static java.util.Arrays.sort;

public class MainFeedFragment extends Fragment {

    private MemeViewAdapter memeViewAdapter;
    private int pageNumber = 0;

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

        ListView listView = Objects.requireNonNull(getView()).findViewById(R.id.feedListView);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int position = firstVisibleItem+visibleItemCount;
                int limit = totalItemCount;

                // Check if bottom has been reached
                if (position >= limit && totalItemCount > 0) {

                    System.out.println("###### WE ARE ATT BOTTOM");
                    System.out.println("###### PAGE: " + pageNumber);
                    loadMemes();
                }
            }
        });

        assert mainActivity != null;
        mainActivity.updateDrawerMenu();
    }


    void loadMemes() {
        Connection.getInstance().getMemes(null, null, null, null, pageNumber, (memesResult) -> {
            assert memesResult instanceof ArrayList;

            ArrayList<Meme> memes = (ArrayList<Meme>) memesResult;

            if (memes.size() > 0) {
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

                if (listView.getAdapter() == null) {
                    getView().post(() -> {
                                listView.setAdapter(new MemeViewAdapter(memes, getActivity()));
                                this.memeViewAdapter = (MemeViewAdapter) listView.getAdapter();
//                                memeViewAdapter.notifyDataSetChanged();
                                getView().postDelayed(this::updateList, 0);
                            }
                    );
                }
                else {
                    getView().post(() -> {
                                memeViewAdapter.addMemesToShow(memes);
//                                memeViewAdapter.notifyDataSetChanged();
                                getView().postDelayed(this::updateList, 0);
                            }
                    );
                }
                pageNumber++;
            }
        });
    }

    public void updateList() {
        getView().post(memeViewAdapter::notifyDataSetChanged);
    }
}
