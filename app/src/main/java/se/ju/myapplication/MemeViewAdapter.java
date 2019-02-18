package se.ju.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

// https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial

public class MemeViewAdapter extends ArrayAdapter<Meme> implements View.OnClickListener {
    private ArrayList<Meme> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtName;
        TextView txtAuthor;
        TextView votes;
        ImageView image;
    }

    public MemeViewAdapter(ArrayList<Meme> data, Context context) {
        super(context, R.layout.meme_list_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
    }

    private int lastPosition = -1;

    @SuppressLint("SetTextI18n")
    @NotNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Meme dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.meme_list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.memeImage);
            viewHolder.votes = (TextView) convertView.findViewById(R.id.votes);
            viewHolder.txtAuthor = (TextView) convertView.findViewById(R.id.author);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        // Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        // result.startAnimation(animation);
        lastPosition = position;


        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtAuthor.setText(dataModel.getUsername());
        viewHolder.votes.setText(dataModel.getVotes().toString());

        Picasso.get()
                .load(dataModel.getImageSource())
                .into(viewHolder.image);

        // Return the completed view to render on screen
        return convertView;
    }
}


