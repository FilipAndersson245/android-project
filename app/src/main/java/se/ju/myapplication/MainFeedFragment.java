package se.ju.myapplication;

import android.os.Bundle;
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

    private static final int PAGE_SIZE = 5;

    private RecyclerView mRecyclerView;
    private MemeViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeLayout;

    private int mPageNumber = 0;

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

        mSwipeLayout = view.findViewById(R.id.swipeContainer);
        mSwipeLayout.setOnRefreshListener(this);

        loadMemesOnStart();
        addOnDownScrollListener();
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.updateDrawerMenu();
        }
    }

    public void signInVotesUpdater() {
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
        mPageNumber = 0;
        Connection.getInstance().getMemes(null, null, null, PAGE_SIZE, mPageNumber, (memesResult) -> {
            ArrayList<Meme> memes = clearRemovedMemes((ArrayList<Meme>) memesResult);

            if (memes.size() > 0) {
                getView().post(() -> {
                    this.mAdapter = new MemeViewAdapter(getActivity(), memes);
                    mRecyclerView.setAdapter(mAdapter);
                });
                mPageNumber++;
            }
        });
    }

    private void loadMemes() {
        Connection.getInstance().getMemes(null, null, null, PAGE_SIZE, mPageNumber, (memesResult) -> {
            ArrayList<Meme> memes = clearRemovedMemes((ArrayList<Meme>) memesResult);

            if (memes.size() > 0) {

                getView().post(() -> {
                    mAdapter.addMemesToShow(memes);
                    mAdapter.notifyDataSetChanged();
                });
                mPageNumber++;
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
    }

    @Override
    public void onRefresh() {
        loadMemesOnStart();

        mSwipeLayout.setRefreshing(false);
    }
}
