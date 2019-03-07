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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import se.ju.myapplication.MemeTemplate;
import se.ju.myapplication.R;

public class ListMemeTemplateAdapter extends RecyclerView.Adapter {

    private List<MemeTemplate> mtDataset;

    public ListMemeTemplateAdapter(ArrayList<MemeTemplate> templates) {
        mtDataset = templates;
    }

    public void addTemplatesToShow(List<MemeTemplate> newMemeTemplates){
        mtDataset.addAll(newMemeTemplates);
    }




}
