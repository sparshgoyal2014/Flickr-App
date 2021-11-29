package com.example.flickrsearch;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.FlickrImageViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    private List<Photo> photoList;
    private Context context;

    public RecyclerViewAdapter(List<Photo> photoList, Context context) {
        this.photoList = photoList;
        this.context = context;
    }

    @Override
    public FlickrImageViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        //Called by the layout manager when it needs a new view
        Log.d(TAG, "onCreateViewHolder: new View requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, parent, false);

        return new FlickrImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder( FlickrImageViewHolder flickrImageViewHolder, int position) {
        // called by the layout manager when it wants anew data in an existing row

        if((photoList == null) || (photoList.size() == 0)){
            Log.d(TAG, "onBindViewHolder: I am here");
            flickrImageViewHolder.thumbnail.setImageResource(R.drawable.imageplaceholder);
            flickrImageViewHolder.title.setText("NO PHOTOS MATCHEd YOUR SEARCh. \n use the search icon to search for photos");
        }else{
            Photo photoItem = photoList.get(position);
            Log.d(TAG, "onBindViewHolder: " + photoItem.getTitle() + "---->" + position);
            Picasso.get().load(photoItem.getImage())
                    .error(R.drawable.imageplaceholder)
                    .placeholder(R.drawable.imageplaceholder)
                    .into(flickrImageViewHolder.thumbnail);

            flickrImageViewHolder.title.setText(photoItem.getTitle());
        }




    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called");
        return (photoList != null && photoList.size() != 0) ? photoList.size() : 0;
    }

    void loadNewData(List<Photo> newPhotos){
        photoList = newPhotos;
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position){
        return (photoList != null && photoList.size() != 0) ? photoList.get(position): null;
    }

    static class FlickrImageViewHolder extends RecyclerView.ViewHolder{

        private static final String TAG = "FlickrImageViewHolder";
        ImageView thumbnail = null;
        TextView title = null;


        public FlickrImageViewHolder( View itemView) {
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: starts");
            this.thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
            this.title = (TextView)itemView.findViewById(R.id.title);

        }
    }


}
