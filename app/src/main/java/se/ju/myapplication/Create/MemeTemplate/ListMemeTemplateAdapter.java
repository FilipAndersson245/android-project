package se.ju.myapplication.Create.MemeTemplate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import se.ju.myapplication.MemeTemplate;
import se.ju.myapplication.R;

public class ListMemeTemplateAdapter extends RecyclerView.Adapter<ListMemeTemplateAdapter.ListMemeTemplateViewHolder> {

    private ArrayList<MemeTemplate> mtDataset;
    private Context context;

    private static ClickListener clickListener;

    public ListMemeTemplateAdapter(Context context, ArrayList<MemeTemplate> templates) {
        mtDataset = templates;
        this.context = context;
    }

    public void addTemplatesToShow(ArrayList<MemeTemplate> newMemeTemplates){
        mtDataset.addAll(newMemeTemplates);
    }


    @Override
    public ListMemeTemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View templateListItem = inflater.inflate(R.layout.meme_template_list_item, parent, false);

        return new ListMemeTemplateViewHolder(templateListItem);
    }

    @Override
    public void onBindViewHolder(ListMemeTemplateViewHolder holder, int position) {

        holder.templateName.setText(mtDataset.get(position).getName());
        holder.templateUsername.setText(mtDataset.get(position).getUsername());

        Picasso.get()
                .load(mtDataset.get(position).getImageSource())
                .fit()
                .into(holder.templateImage);
    }

    @Override
    public int getItemCount() {
        return mtDataset.size();
    }


    public void setOnItemClickListener(ClickListener clickListener) {
        ListMemeTemplateAdapter.clickListener = clickListener;
    }


    public interface ClickListener {
        void onItemClick(int position, Drawable image);
    }


    public static class ListMemeTemplateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView templateName;
        public TextView templateUsername;
        public ImageView templateImage;

        public ListMemeTemplateViewHolder(View itemView) {
            super(itemView);
            templateName = itemView.findViewById(R.id.templateName);
            templateUsername = itemView.findViewById(R.id.templateUsername);
            templateImage = itemView.findViewById(R.id.templateImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), templateImage.getDrawable());
        }
    }
}
