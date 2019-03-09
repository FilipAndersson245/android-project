package se.ju.myapplication.Create.MemeTemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import se.ju.myapplication.Create.Meme.CreateMemeActivity;
import se.ju.myapplication.MemeTemplate;
import se.ju.myapplication.R;

public class ListMemeTemplateAdapter extends RecyclerView.Adapter<ListMemeTemplateAdapter.ListMemeTemplateViewHolder> {

    private ArrayList<MemeTemplate> mtDataset;

    private Context context;

    public ListMemeTemplateAdapter(Context context, ArrayList<MemeTemplate> templates) {
        mtDataset = templates;

        this.context = context;
    }

    public void addTemplatesToShow(ArrayList<MemeTemplate> newMemeTemplates){
        mtDataset.addAll(newMemeTemplates);
    }


    @Override
    public ListMemeTemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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

        holder.templateItemView.setOnClickListener((view) -> {
            System.out.println("###### IT WORKS ON ITEM " + position);

            itemClicked(holder.templateImage);

        });

        Picasso.get()
                .load(mtDataset.get(position).getImageSource())
                .fit()
                .into(holder.templateImage);
    }

    private void itemClicked(ImageView image) {

        Bitmap bmp = ((BitmapDrawable) image.getDrawable()).getBitmap();

        try {
            //Write file
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            //Cleanup
            stream.close();
            bmp.recycle();

            //Pop intent
            Intent intent = new Intent();
            intent.putExtra("image", byteArray);

            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }

//        Intent intent = new Intent();
//        intent.putExtra("selectedTemplate",Integer.parseInt(quantity.getText().toString()));

//        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }






    @Override
    public int getItemCount() {
        return mtDataset.size();
    }


    public static class ListMemeTemplateViewHolder extends RecyclerView.ViewHolder {
        public TextView templateName;
        public TextView templateUsername;
        public ImageView templateImage;
        public View templateItemView;

        public ListMemeTemplateViewHolder(View itemView) {
            super(itemView);
            templateName = itemView.findViewById(R.id.templateName);
            templateUsername = itemView.findViewById(R.id.templateUsername);
            templateImage = itemView.findViewById(R.id.templateImage);
            templateItemView = itemView.findViewById(R.id.templateItemView);
        }
    }


}
