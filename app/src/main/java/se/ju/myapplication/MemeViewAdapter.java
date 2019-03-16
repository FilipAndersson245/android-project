package se.ju.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.squareup.picasso.Picasso;

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

    public void addMemesToShow(ArrayList<Meme> newMemes){
        mDataSet.addAll(newMemes);
    }

    @Override
    public MemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View templateListItem = inflater.inflate(R.layout.meme_list_item, parent, false);

        return new MemeViewHolder(templateListItem);
    }

    @Override
    public void onBindViewHolder(MemeViewHolder holder, int position) {

        holder.title.setText(mDataSet.get(position).getName());
        holder.memeAuthor.setText(mDataSet.get(position).getUsername());
        holder.votes.setText(mDataSet.get(position).getVotes().toString());

        Picasso.get()
            .load(mDataSet.get(position).getImageSource())
            .into(holder.memeImage);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void updateVotesForLogin() {

        // Add votes if user is signed in

        if (Connection.getInstance().isSignedIn()) {
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
    }


//    public void setOnItemClickListener(ClickListener clickListener) {
//        MemeViewAdapter.clickListener = clickListener;
//    }
//
//
//    public interface ClickListener {
//        void onItemClick(int templateId, String imageSrouce, Drawable image);
//    }




//    public static class MemeViewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public static class MemeViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView memeAuthor;
        public TextView votes;
        public ImageView memeImage;

        public MemeViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.memeName);
            memeAuthor = itemView.findViewById(R.id.memeAuthor);
            votes = itemView.findViewById(R.id.memeVotes);
            memeImage = itemView.findViewById(R.id.memeImage);
        }

//        @Override
//        public void onClick(View v) {
//            clickListener.onItemClick(this.templateId, this.templateImageSource, templateImage.getDrawable());
//        }
    }



//    private void updateButtonsAndVotes(ViewHolder viewHolder) {
//        boolean isSignedIn = Connection.getInstance().isSignedIn();
//
//        viewHolder.upVote.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
//        viewHolder.downVote.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
//
//        if (isSignedIn) {
//            viewHolder.votesLabel.setText(String.valueOf(viewHolder.votesWithoutUserVote + viewHolder.userVote));
//        } else {
//            viewHolder.votesLabel.setText(String.valueOf(viewHolder.votes));
//        }
//
//        if (isSignedIn) {
//            switch (viewHolder.userVote) {
//                case 1:
//                    viewHolder.upVote.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                    break;
//                case -1:
//                    viewHolder.downVote.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
//                    break;
//            }
//        }
//    }

//    @NotNull
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // Get the data item for this position
//        Meme dataModel = dataSet.get(position);
//        // Check if an existing view is being reused, otherwise inflate the view
//        ViewHolder viewHolder; // view lookup cache stored in tag
//
//        if (convertView == null) {
//
//            viewHolder = new ViewHolder();
//            LayoutInflater inflater = LayoutInflater.from(getContext());
//            convertView = inflater.inflate(R.layout.meme_list_item, parent, false);
//            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
//            viewHolder.image = (ImageView) convertView.findViewById(R.id.memeImage);
//            viewHolder.votesLabel = (TextView) convertView.findViewById(R.id.votesLabel);
//            viewHolder.txtAuthor = (TextView) convertView.findViewById(R.id.author);
//            viewHolder.downVote = (Button) convertView.findViewById(R.id.downVote);
//            viewHolder.upVote = (Button) convertView.findViewById(R.id.upVote);
//            viewHolder.position = position;
//            viewHolder.userVote = dataModel.getVote();
//            viewHolder.votes = dataModel.getVotes();
//            viewHolder.votesWithoutUserVote = dataModel.getVotes() - viewHolder.userVote;
//
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//            viewHolder.votes = dataModel.getVotes();
//            viewHolder.votesWithoutUserVote = dataModel.getVotes() - viewHolder.userVote;
//
//            viewHolder.userVote = dataModel.getVote();
//        }
//
//        viewHolder.downVote.setOnClickListener(v -> {
//            if (!Connection.getInstance().isSignedIn()) {
//                return;
//            }
//
//            if (viewHolder.userVote == -1) {
//                try {
//                    Connection.getInstance().removeVote(dataSet.get(position).getId(), Connection.getInstance().getSignedInUsername(), (nothing) -> {
//                        v.post(() -> {
//                            viewHolder.userVote = 0;
//                            dataSet.get(position).setVote(0);
//                            updateButtonsAndVotes(viewHolder);
//                        });
//
//                    });
//                } catch (JsonProcessingException e) {
//                    // Voting failed
//                }
//            } else {
//                try {
//                    Connection.getInstance().vote(dataSet.get(position).getId(), Connection.getInstance().getSignedInUsername(), -1, (vote) -> {
//                        System.out.println("-1");
//
//                        v.post(() -> {
//                            viewHolder.userVote = -1;
//                            dataSet.get(position).setVote(-1);
//                            updateButtonsAndVotes(viewHolder);
//                        });
//
//                    });
//                } catch (JsonProcessingException e) {
//                    // Voting failed
//                }
//            }
//        });
//
//        viewHolder.upVote.setOnClickListener(v -> {
//            if (!Connection.getInstance().isSignedIn()) {
//                return;
//            }
//
//            if (viewHolder.userVote == 1) {
//                try {
//                    Connection.getInstance().removeVote(dataSet.get(position).getId(), Connection.getInstance().getSignedInUsername(), (nothing) -> {
//                        v.post(() -> {
//                            viewHolder.userVote = 0;
//                            dataSet.get(position).setVote(0);
//                            updateButtonsAndVotes(viewHolder);
//                        });
//
//                    });
//                } catch (JsonProcessingException e) {
//                    // Voting failed
//                }
//            } else {
//                try {
//                    Connection.getInstance().vote(dataSet.get(position).getId(), Connection.getInstance().getSignedInUsername(), 1, (vote) -> {
//                        System.out.println("+1");
//                        v.post(() -> {
//                            viewHolder.userVote = 1;
//                            dataSet.get(position).setVote(1);
//                            updateButtonsAndVotes(viewHolder);
//                        });
//
//                    });
//                } catch (JsonProcessingException e) {
//                    // Voting failed
//                }
//            }
//        });
//
//        viewHolder.txtName.setText(dataModel.getName());
//        viewHolder.txtAuthor.setText(dataModel.getUsername());
//
//        Picasso.get()
//            .load(dataModel.getImageSource())
//            .placeholder(R.drawable.spinner)
//            .into(viewHolder.image);
//
//
//        // Return the completed view to render on screen
//
//        convertView.post(() -> updateButtonsAndVotes(viewHolder));
//
//        return convertView;
//    }


}


