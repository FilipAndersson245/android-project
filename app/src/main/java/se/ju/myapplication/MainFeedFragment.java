package se.ju.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

import se.ju.myapplication.API.Connection;
import se.ju.myapplication.Models.Meme;


public class MainFeedFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private MemeViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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
        super.onCreate(savedInstanceState);

        mRecyclerView = view.findViewById(R.id.feedRecyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        loadMemesOnStart();
        addOnDownScrollListener();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.updateDrawerMenu();
    }


    private void signInVotes() {
        if (Connection.getInstance().isSignedIn()) {
            Handler mainHandler = new Handler(getActivity().getMainLooper());

            Runnable myRunnable = () -> {
                mAdapter.updateVotesForLogin();
                mAdapter.notifyDataSetChanged();
            };
            mainHandler.post(myRunnable);
        }
    }

    public void signOutVotes() {
        mAdapter.notifyDataSetChanged();
    }

    private void addOnDownScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {

                    loadMemes();
                }
            }
        });
    }

    private void loadMemesOnStart() {
        Connection.getInstance().getMemes(null, null, null, null, pageNumber, (memesResult) -> {

            ArrayList<Meme> memes = (ArrayList<Meme>) memesResult;
            Collections.sort(memes);

            if (memes.size() > 0) {
                Handler mainHandler = new Handler(getActivity().getMainLooper());

                Runnable myRunnable = () -> {
                    this.mAdapter = new MemeViewAdapter(getActivity(), memes);

                    mRecyclerView.setAdapter(mAdapter);
                    signInVotes();
                    pageNumber++;
                };
                mainHandler.post(myRunnable);
            }
        });
    }


    private void loadMemes() {
        Connection.getInstance().getMemes(null, null, null, null, pageNumber, (memesResult) -> {

            ArrayList<Meme> memes = (ArrayList<Meme>) memesResult;
            Collections.sort(memes);

            if (memes.size() > 0) {
                Handler mainHandler = new Handler(getActivity().getMainLooper());

                Runnable myRunnable = () -> {
                    mAdapter.addMemesToShow(memes);
                    signInVotes();
                    mAdapter.notifyDataSetChanged();
                    pageNumber++;
                };
                mainHandler.post(myRunnable);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pageNumber = 0;
    }

}
