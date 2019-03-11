package se.ju.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

// https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial

public class MemeViewAdapter extends ArrayAdapter<Meme> {
    private ArrayList<Meme> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtName;
        TextView txtAuthor;
        TextView votesLabel;
        ImageView image;
        Button downVote;
        Button upVote;
        Integer votes;
        Integer votesWithoutUserVote;
        Integer position;
        Integer userVote;
    }

    public MemeViewAdapter(ArrayList<Meme> data, Context context) {
        super(context, R.layout.meme_list_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    private void updateButtonsAndVotes(ViewHolder viewHolder) {
        boolean isSignedIn = Connection.getInstance().isSignedIn();

        viewHolder.upVote.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        viewHolder.downVote.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));

        if (isSignedIn) {
            viewHolder.votesLabel.setText(String.valueOf(viewHolder.votesWithoutUserVote + viewHolder.userVote));
        } else {
            viewHolder.votesLabel.setText(String.valueOf(viewHolder.votes));
        }

        if (isSignedIn) {
            switch (viewHolder.userVote) {
                case 1:
                    viewHolder.upVote.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case -1:
                    viewHolder.downVote.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
                    break;
            }
        }
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Meme dataModel = dataSet.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.meme_list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.memeImage);
            viewHolder.votesLabel = (TextView) convertView.findViewById(R.id.votesLabel);
            viewHolder.txtAuthor = (TextView) convertView.findViewById(R.id.author);
            viewHolder.downVote = (Button) convertView.findViewById(R.id.downVote);
            viewHolder.upVote = (Button) convertView.findViewById(R.id.upVote);
            viewHolder.position = position;
            viewHolder.userVote = dataModel.getVote();
            viewHolder.votes = dataModel.getVotes();
            viewHolder.votesWithoutUserVote = dataModel.getVotes() - viewHolder.userVote;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.votes = dataModel.getVotes();
            viewHolder.votesWithoutUserVote = dataModel.getVotes() - viewHolder.userVote;

            viewHolder.userVote = dataModel.getVote();
        }

        viewHolder.downVote.setOnClickListener(v -> {
            if (!Connection.getInstance().isSignedIn()) {
                return;
            }

            if (viewHolder.userVote == -1) {
                try {
                    Connection.getInstance().removeVote(dataSet.get(position).getId(), Connection.getInstance().getSignedInUsername(), (nothing) -> {
                        v.post(() -> {
                            viewHolder.userVote = 0;
                            dataSet.get(position).setVote(0);
                            updateButtonsAndVotes(viewHolder);
                        });

                    });
                } catch (JsonProcessingException e) {
                    // Voting failed
                }
            } else {
                try {
                    Connection.getInstance().vote(dataSet.get(position).getId(), Connection.getInstance().getSignedInUsername(), -1, (vote) -> {
                        System.out.println("-1");

                        v.post(() -> {
                            viewHolder.userVote = -1;
                            dataSet.get(position).setVote(-1);
                            updateButtonsAndVotes(viewHolder);
                        });

                    });
                } catch (JsonProcessingException e) {
                    // Voting failed
                }
            }
        });

        viewHolder.upVote.setOnClickListener(v -> {
            if (!Connection.getInstance().isSignedIn()) {
                return;
            }

            if (viewHolder.userVote == 1) {
                try {
                    Connection.getInstance().removeVote(dataSet.get(position).getId(), Connection.getInstance().getSignedInUsername(), (nothing) -> {
                        v.post(() -> {
                            viewHolder.userVote = 0;
                            dataSet.get(position).setVote(0);
                            updateButtonsAndVotes(viewHolder);
                        });

                    });
                } catch (JsonProcessingException e) {
                    // Voting failed
                }
            } else {
                try {
                    Connection.getInstance().vote(dataSet.get(position).getId(), Connection.getInstance().getSignedInUsername(), 1, (vote) -> {
                        System.out.println("+1");
                        v.post(() -> {
                            viewHolder.userVote = 1;
                            dataSet.get(position).setVote(1);
                            updateButtonsAndVotes(viewHolder);
                        });

                    });
                } catch (JsonProcessingException e) {
                    // Voting failed
                }
            }
        });

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtAuthor.setText(dataModel.getUsername());

        Picasso.get()
                .load(dataModel.getImageSource())
                .into(viewHolder.image);

        // Return the completed view to render on screen

        convertView.post(() -> updateButtonsAndVotes(viewHolder));

        return convertView;
    }
}


