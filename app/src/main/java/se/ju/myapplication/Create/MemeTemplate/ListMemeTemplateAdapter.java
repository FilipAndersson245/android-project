package se.ju.myapplication.Create.MemeTemplate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import se.ju.myapplication.Models.MemeTemplate;
import se.ju.myapplication.R;

public class ListMemeTemplateAdapter extends RecyclerView.Adapter<ListMemeTemplateAdapter.ListMemeTemplateViewHolder> {

    private ArrayList<MemeTemplate> mDataset;
    private Context mContext;

    private static ClickListener clickListener;

    public static class ListMemeTemplateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTemplateName;
        public TextView mTemplateUsername;
        public ImageView mTemplateImage;
        private int mTemplateId;
        private String mTemplateImageSource;

        public ListMemeTemplateViewHolder(View itemView) {
            super(itemView);
            mTemplateName = itemView.findViewById(R.id.templateName);
            mTemplateUsername = itemView.findViewById(R.id.templateUsername);
            mTemplateImage = itemView.findViewById(R.id.templateImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(this.mTemplateId, this.mTemplateImageSource, mTemplateImage.getDrawable());
        }
    }

    public ListMemeTemplateAdapter(Context context, ArrayList<MemeTemplate> templates) {
        mDataset = templates;
        this.mContext = context;
    }

    public void addTemplatesToShow(ArrayList<MemeTemplate> newMemeTemplates){
        mDataset.addAll(newMemeTemplates);
    }


    @NonNull
    @Override
    public ListMemeTemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View templateListItem = inflater.inflate(R.layout.meme_template_list_item, parent, false);

        return new ListMemeTemplateViewHolder(templateListItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ListMemeTemplateViewHolder holder, int position) {
        holder.mTemplateName.setText(mDataset.get(position).getName());
        holder.mTemplateUsername.setText(mDataset.get(position).getUsername());
        holder.mTemplateId = mDataset.get(position).getId();
        holder.mTemplateImageSource = mDataset.get(position).getImageSource();

        Picasso.get()
                .load(mDataset.get(position).getImageSource())
                .placeholder(R.drawable.spinner)
                .into(holder.mTemplateImage);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public void setOnItemClickListener(ClickListener clickListener) {
        ListMemeTemplateAdapter.clickListener = clickListener;
    }


    public interface ClickListener {
        void onItemClick(int templateId, String imageSrouce, Drawable image);
    }
}
