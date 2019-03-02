package se.ju.myapplication.Create.MemeTemplate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import se.ju.myapplication.MemeTemplate;
import se.ju.myapplication.R;

public class ListMemeTemplateAdapter extends RecyclerView.Adapter<ListMemeTemplateAdapter.ListMemeTemplateViewHolder> {
    private List<MemeTemplate> mtDataset;

    public static class ListMemeTemplateViewHolder extends RecyclerView.ViewHolder {
        public TextView templateName;
        public TextView templateUsername;
        public ImageView templateImage;

        public ListMemeTemplateViewHolder(View itemView) {
            super(itemView);
            templateName = itemView.findViewById(R.id.templateName);
            templateUsername = itemView.findViewById(R.id.templateUsername);
            templateImage = itemView.findViewById(R.id.templateImage);
        }
    }

    public ListMemeTemplateAdapter(List<MemeTemplate> incomingDataset) {
        mtDataset = incomingDataset;
    }

    @Override
    public ListMemeTemplateAdapter.ListMemeTemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View templateListItem = inflater.inflate(R.layout.meme_template_list_item, parent, false);

        ListMemeTemplateViewHolder viewholder = new ListMemeTemplateViewHolder(templateListItem);

        return viewholder;
    }

    @Override
    public void onBindViewHolder(ListMemeTemplateViewHolder holder, int position) {

        // Do stuff with data from a list later...

        holder.templateName.setText(mtDataset.get(position).getName());
        holder.templateUsername.setText(mtDataset.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return mtDataset.size();
    }
}
