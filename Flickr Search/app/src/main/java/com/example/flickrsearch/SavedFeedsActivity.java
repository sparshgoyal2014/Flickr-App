package com.example.flickrsearch;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SavedFeedsActivity extends BaseActivity implements RecyclerItemClickListener.OnRecyclerClickListener {

    List<Photo> photoList = new ArrayList<>();;
    RecyclerViewAdapter recyclerViewAdapter;
    private static final String TAG = "SavedFeedsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_feeds);

        activateToolbar(true);

        RecyclerView recyclerView = findViewById(R.id.savedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SavedFeedsActivity.this));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(SavedFeedsActivity.this, recyclerView, SavedFeedsActivity.this));
        recyclerViewAdapter = new RecyclerViewAdapter(photoList, SavedFeedsActivity.this);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SavedFeeds").child(firebaseUser.getUid().toString());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String key = snapshot.getKey().toString();
                    String title = snapshot.child("title").getValue().toString();
                    String author = snapshot.child("author").getValue().toString();
                    String authorId = snapshot.child("authorId").getValue().toString();
                    String image = snapshot.child("image").getValue().toString();
                    String link = snapshot.child("link").getValue().toString();
                    String tags = snapshot.child("tags").getValue().toString();

                    Photo photo = new Photo(title, author, authorId, link, tags, image);
                    photoList.add(photo);

                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerViewAdapter.loadNewData(photoList);

                    Log.d(TAG, "onDataChange: " + title);
//                    Toast.makeText(SavedFeedsActivity.this, title, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}
