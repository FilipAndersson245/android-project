package se.ju.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.squareup.picasso.Picasso;

import java.time.Duration;
import java.util.ArrayList;

import se.ju.myapplication.API.Connection;
import se.ju.myapplication.Models.Meme;
import se.ju.myapplication.Models.Vote;


public class MemeViewAdapter extends RecyclerView.Adapter<MemeViewAdapter.MemeViewHolder> {

    private ArrayList<Meme> mDataSet;
    Context context;

    public MemeViewAdapter(Context context, ArrayList<Meme> data) {
        mDataSet = data;
        this.context = context;
    }

    public void addMemesToShow(ArrayList<Meme> newMemes) {
        for (Meme newMeme : newMemes) {
            if (!mDataSet.contains(newMeme)) {
                mDataSet.add(newMeme);
            }
        }
    }

    @Override
    public MemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View templateListItem = inflater.inflate(R.layout.meme_list_item, parent, false);

        return new MemeViewHolder(templateListItem);
    }

    @Override
    public void onBindViewHolder(MemeViewHolder holder, int position) {

        Picasso.get()
                .load(mDataSet.get(position).getImageSource())
                .into(holder.memeImage);

        holder.title.setText(mDataSet.get(position).getName());
        holder.memeAuthor.setText(mDataSet.get(position).getUsername());
        holder.votes.setText(mDataSet.get(position).getVotes().toString());

        holder.upVote.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        holder.downVote.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));

        holder.currentVotes = mDataSet.get(position).getVotes();

        if (Connection.getInstance().isSignedIn()) {

            if (Connection.getInstance().getSignedInUsername().equals(mDataSet.get(position).getUsername())) {
                holder.remove.setVisibility(View.VISIBLE);
            }

            try {
                Connection.getInstance().getVote(mDataSet.get(position).getId(), Connection.getInstance().getSignedInUsername(), (voteResult) -> {
                    Vote vote = (Vote) voteResult;
                    mDataSet.get(position).setVote(vote.getVote());
                    holder.currentVotes += vote.getVote();

                    switch (vote.getVote()) {
                        case 1:
                            System.out.println("###### GÖR GRÖN");
                            holder.upVote.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                            break;
                        case -1:
                            System.out.println("###### GÖR BLÅ");
                            holder.downVote.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
                            break;
                    }
                });
            } catch (JsonProcessingException e) {
                mDataSet.get(position).setVote(0);
            }

        }

        holder.currentVotes = mDataSet.get(position).getVotes();

        holder.upVote.setOnClickListener(v -> {
            if (!Connection.getInstance().isSignedIn()) {
                return;
            }

            switch (mDataSet.get(position).getVote()) {
                case 0:
                    voteHandle(holder, position, 1);
                    holder.currentVotes++;
                    holder.votes.setText(holder.currentVotes.toString());
                    break;
                case 1:
                    voteHandle(holder, position, 0);
                    holder.currentVotes--;
                    holder.votes.setText(holder.currentVotes.toString());
                    break;
                case -1:
                    voteHandle(holder, position, 1);
                    holder.currentVotes += 2;
                    holder.votes.setText(holder.currentVotes.toString());
                    break;
            }
        });

        holder.downVote.setOnClickListener(v -> {
            if (!Connection.getInstance().isSignedIn()) {
                return;
            }

            switch (mDataSet.get(position).getVote()) {
                case 0:
                    voteHandle(holder, position, -1);
                    holder.currentVotes--;
                    holder.votes.setText(holder.currentVotes.toString());
                    break;
                case 1:
                    voteHandle(holder, position, -1);
                    holder.currentVotes -= 2;
                    holder.votes.setText(holder.currentVotes.toString());
                    break;
                case -1:
                    voteHandle(holder, position, 0);
                    holder.currentVotes++;
                    holder.votes.setText(holder.currentVotes.toString());
                    break;
            }
        });

        holder.remove.setOnClickListener(v -> {
            if (!Connection.getInstance().isSignedIn()) {
                return;
            }
            new AlertDialog.Builder(context)
                    .setTitle(R.string.remove_meme)
                    .setMessage(R.string.remove_meme_message)
                    .setPositiveButton(
                            R.string.yes,
                            (dialog, whichButton) -> Connection.getInstance().deleteMeme(mDataSet.get(position).getId(), nothing -> {
                                mDataSet.remove(position);
                                v.post(() -> notifyDataSetChanged());
                            })
                    ).setNegativeButton(
                    R.string.no,
                    (dialog, whichButton) -> {
                        // Do not delete it.
                    }
            ).show();
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void voteHandle(MemeViewHolder holder, Integer position, Integer vote) {
        if (vote == 0) {
            try {
                Connection.getInstance().removeVote(mDataSet.get(position).getId(), Connection.getInstance().getSignedInUsername(), (nothing) -> {
                    mDataSet.get(position).setVote(vote);

                    holder.upVote.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                    holder.downVote.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                });
            } catch (JsonProcessingException e) {
                // Voting failed
            }
        } else {
            try {
                Connection.getInstance().vote(mDataSet.get(position).getId(), Connection.getInstance().getSignedInUsername(), vote, (nothing) -> {
                    mDataSet.get(position).setVote(vote);

                    switch (mDataSet.get(position).getVote()) {
                        case 1:
                            holder.upVote.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                            holder.downVote.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                            break;
                        case -1:
                            holder.upVote.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                            holder.downVote.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
                            break;
                    }
                });
            } catch (JsonProcessingException e) {
                // Voting failed
            }
        }
    }

    public void updateVotesForLogin() {

        for (Meme meme : mDataSet) {
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

    public void updateViewforVotes(MemeViewHolder holder) {

        switch (holder.currentVotes) {
            case 1:
                System.out.println("###### GÖR GRÖN");
                holder.upVote.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                break;
            case -1:
                System.out.println("###### GÖR BLÅ");
                holder.downVote.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
                break;
        }
    }

    public static class MemeViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView memeAuthor;
        public TextView votes;
        public ImageView memeImage;
        public Button upVote;
        public Button downVote;
        public Integer currentVotes;
        public Button remove;

        public MemeViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.memeName);
            memeAuthor = itemView.findViewById(R.id.memeAuthor);
            votes = itemView.findViewById(R.id.memeVotes);
            memeImage = itemView.findViewById(R.id.memeImage);
            upVote = itemView.findViewById(R.id.upVote);
            downVote = itemView.findViewById(R.id.downVote);
            remove = itemView.findViewById(R.id.removeButton);
        }
    }

}


