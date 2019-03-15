package se.ju.myapplication;

import android.content.Context;
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
    private Boolean scrollReady = true;

    private RecyclerView mRecyclerView;
    private MemeViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private int pageNumber = 0;

    Context context;

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

        context = view.getContext();

        mRecyclerView = view.findViewById(R.id.feedRecyclerView);

        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);


        loadMemesOnStart();
    }

    private void loadMemesOnStart() {
        Connection.getInstance().getMemes(null, null, null, null, pageNumber, (memesResult) -> {
            assert memesResult instanceof ArrayList;

            ArrayList<Meme> memes = (ArrayList<Meme>) memesResult;
            Collections.sort(memes);

            if (memes.size() > 0) {

                System.out.println("###### WE LOAD START MEMES");

                Handler mainHandler = new Handler(context.getMainLooper());

                Runnable myRunnable = () -> {
                    this.mAdapter = new MemeViewAdapter(context, memes);

                    // Code here

                    mRecyclerView.setAdapter(mAdapter);
                    pageNumber++;
                };

                mainHandler.post(myRunnable);













//                // Add votes if user is signed in
//                if (Connection.getInstance().isSignedIn()) {
//                    for (Meme meme : memes) {
//                        try {
//                            Connection.getInstance().getVote(meme.getId(), Connection.getInstance().getSignedInUsername(), (voteResult) -> {
//                                Vote vote = (Vote) voteResult;
//                                meme.setVote(vote.getVote());
//                            });
//                        } catch (JsonProcessingException e) {
//                            meme.setVote(0);
//                        }
//                    }
//                }
//
//                Collections.sort(memes);
//                ListView listView = Objects.requireNonNull(getView()).findViewById(R.id.feedRecyclerView);
//
//                if (listView.getAdapter() == null) {
//                    getView().post(() -> {
//                                listView.setAdapter(new MemeViewAdapter(memes, getActivity()));
//                                this.memeViewAdapter = (MemeViewAdapter) listView.getAdapter();
//                                memeViewAdapter.notifyDataSetChanged();
////                                getView().postDelayed(this::updateList, 0);
//                            }
//                    );
//                    pageNumber++;
//                    scrollReady = true;
//                }
//                else {
//                    getView().post(() -> {
//                                memeViewAdapter.addMemesToShow(memes);
//                                memeViewAdapter.notifyDataSetChanged();
////                                getView().postDelayed(this::updateList, 0);
//                            }
//                    );
//                    pageNumber++;
//                    scrollReady = true;
//                }
//
            }
        });
    }


//    private void loadMemes() {
//        Connection.getInstance().getMemes(null, null, null, null, pageNumber, (memesResult) -> {
//            assert memesResult instanceof ArrayList;
//
//            ArrayList<Meme> memes = (ArrayList<Meme>) memesResult;
//
//            if (memes.size() > 0) {
//                // Add votes if user is signed in
//                if (Connection.getInstance().isSignedIn()) {
//                    for (Meme meme : memes) {
//                        try {
//                            Connection.getInstance().getVote(meme.getId(), Connection.getInstance().getSignedInUsername(), (voteResult) -> {
//                                Vote vote = (Vote) voteResult;
//                                meme.setVote(vote.getVote());
//                            });
//                        } catch (JsonProcessingException e) {
//                            meme.setVote(0);
//                        }
//                    }
//                }
//
//                Collections.sort(memes);
//                ListView listView = Objects.requireNonNull(getView()).findViewById(R.id.feedRecyclerView);
//
//                if (listView.getAdapter() == null) {
//                    getView().post(() -> {
//                                listView.setAdapter(new MemeViewAdapter(memes, getActivity()));
//                                this.memeViewAdapter = (MemeViewAdapter) listView.getAdapter();
//                                memeViewAdapter.notifyDataSetChanged();
////                                getView().postDelayed(this::updateList, 0);
//                            }
//                    );
//                    pageNumber++;
//                    scrollReady = true;
//                }
//                else {
//                    getView().post(() -> {
//                                memeViewAdapter.addMemesToShow(memes);
//                                memeViewAdapter.notifyDataSetChanged();
////                                getView().postDelayed(this::updateList, 0);
//                            }
//                    );
//                    pageNumber++;
//                    scrollReady = true;
//                }
//
//            }
//        });
//    }




//    public void updateList() {
//        getView().post(memeViewAdapter::notifyDataSetChanged);
//    }
}
