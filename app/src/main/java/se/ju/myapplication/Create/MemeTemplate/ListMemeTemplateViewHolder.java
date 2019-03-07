package se.ju.myapplication.Create.MemeTemplate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import se.ju.myapplication.R;

public class ListMemeTemplateViewHolder extends RecyclerView.ViewHolder {
    public TextView templateName;
    public TextView templateUsername;
    public ImageView templateImage;
    public View templateListItem;

    public ListMemeTemplateViewHolder(View itemView) {
        super(itemView);
        templateName = itemView.findViewById(R.id.templateName);
        templateUsername = itemView.findViewById(R.id.templateUsername);
        templateImage = itemView.findViewById(R.id.templateImage);
        templateListItem = itemView.findViewById(R.id.templateItemView);
    }

    @NonNull
    @Override
    public ListMemeTemplateAdapter.ListMemeTemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View templateListItem = inflater.inflate(R.layout.meme_template_list_item, parent, false);

        return new ListMemeTemplateViewHolder(templateListItem);
    }

    @Override
    public void onBindViewHolder(ListMemeTemplateViewHolder holder, int position) {

        // Do stuff with data from a list later...

        holder.templateName.setText(mtDataset.get(position).getName());
        holder.templateUsername.setText(mtDataset.get(position).getUsername());

        holder.templateListItem.setOnClickListener((view) -> {
            System.out.print("###### IT WORKS ON ITEM " + position);
        });

        Picasso.get()
                .load(mtDataset.get(position).getImageSource())
                .fit()
                .into(holder.templateImage);
    }

    @Override
    public int getItemCount() {
        return mtDataset.size();
    }
}
