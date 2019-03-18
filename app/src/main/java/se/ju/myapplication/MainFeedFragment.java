package se.ju.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import se.ju.myapplication.API.Connection;
import se.ju.myapplication.Models.Meme;


public class MainFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private MemeViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout swipeLayout;

    private int pageSize = 5;
    private int pageNumber = 0;
    private Boolean canScoll = true;

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

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        loadMemesOnStart();
        addOnDownScrollListener();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.updateDrawerMenu();
    }

    public void signInVotesUpdater() {
        if (Connection.getInstance().isSignedIn()) {
            Handler mainHandler = new Handler(getActivity().getMainLooper());

            Runnable myRunnable = () -> {
                mAdapter.updateVotesForLogin();
            };
            mainHandler.post(myRunnable);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void addOnDownScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (canScoll && !recyclerView.canScrollVertically(1)) {
                    canScoll = false;
                    System.out.println("###### SCROLL DOWN ");
                    loadMemes();
                }
            }
        });
    }

    private void loadMemesOnStart() {
        Connection.getInstance().getMemes(null, null, null, pageSize, pageNumber, (memesResult) -> {

            ArrayList<Meme> memes = clearRemovedMemes((ArrayList<Meme>) memesResult);
            pageNumber = 0;

            System.out.println("###### PAGESIZE: " + pageNumber);

            if (memes.size() > 0) {
                Handler mainHandler = new Handler(getActivity().getMainLooper());

                Runnable myRunnable = () -> {
                    this.mAdapter = new MemeViewAdapter(getActivity(), memes);

                    mRecyclerView.setAdapter(mAdapter);
                    signInVotesUpdater();
                    pageNumber++;
                };
                mainHandler.post(myRunnable);
            }
        });
    }

    private void loadMemes() {
        Connection.getInstance().getMemes(null, null, null, pageSize, pageNumber, (memesResult) -> {

            ArrayList<Meme> memes = clearRemovedMemes((ArrayList<Meme>) memesResult);

            System.out.println("###### PAGESIZE: " + pageNumber);

            if (memes.size() > 0) {
                Handler mainHandler = new Handler(getActivity().getMainLooper());

                Runnable myRunnable = () -> {
                    mAdapter.addMemesToShow(memes);
                    signInVotesUpdater();
                    mAdapter.notifyDataSetChanged();
                    pageNumber++;
                    canScoll = true;
                };
                mainHandler.post(myRunnable);
            }
        });
    }

    private ArrayList<Meme> clearRemovedMemes(ArrayList<Meme> memeList) {
        ArrayList<Meme> clearedList = new ArrayList<Meme>();
        for (Meme meme : memeList) {
            if (meme.getImageSource() != null) {
                clearedList.add(meme);
            }
        }
        return clearedList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pageNumber = 0;
    }

    @Override
    public void onRefresh() {
        mAdapter = null;
        pageNumber = 0;
        canScoll = true;
        onViewCreated(getView(), null);

        swipeLayout.setRefreshing(false);
    }
}
