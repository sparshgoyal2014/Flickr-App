package com.example.flickrsearch;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        activateToolbar(true);

        Intent intent = getIntent();
        Photo photo = (Photo) intent.getSerializableExtra(PHOTO_TRANSFER);
        if(photo != null){
            TextView photoTitle = findViewById(R.id.photo_title);
            photoTitle.setText(photo.getTitle());
            TextView photoTags = findViewById(R.id.photo_tags);
            photoTags.setText(photo.getTags());

            TextView photoAuthor = findViewById(R.id.photo_author);
            photoAuthor.setText(photo.getAuthor());

            ImageView photoImage = findViewById(R.id.photo_image);

            Picasso.get().load(photo.getLink())
                    .error(R.drawable.imageplaceholder)
                    .placeholder(R.drawable.imageplaceholder)
                    .into(photoImage);
        }



    }

}
