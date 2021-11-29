package com.example.flickrsearch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements GetFlickrJsonData.OnDataAvailable, RecyclerItemClickListener.OnRecyclerClickListener {
    private static final String TAG = "MainActivity";
    private RecyclerViewAdapter recyclerViewAdapter;

    private AppBarConfiguration mAppBarConfiguration;
    private List<Photo> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: onCreate Method called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activateToolbar(false);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));

        recyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<Photo>(), this);
        recyclerView.setAdapter(recyclerViewAdapter);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "This is FAB", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SavedFeedsActivity.class);
                startActivity(intent);
            }
        });

        Log.d(TAG, "onCreate: ends");
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String queryResult = sharedPreferences.getString(FLICKR_QUERY, "");

        if(queryResult.length() > 0){
            GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData("https://www.flickr.com/services/feeds/photos_public.gne?lang=en-us", this);
            getFlickrJsonData.executeOnSameThread(queryResult);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        Log.d(TAG, "onCreateOptionsMenu() returned: " + true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if(id == R.id.search){
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }


        if(id == R.id.logout){
            Intent intent = new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();;
            FirebaseAuth.getInstance().signOut();

            return true;

        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDataAvailable(List<Photo> data, DownloadStatus downloadStatus) {

        Log.d(TAG, "onDataAvailable: starts");

        if(downloadStatus == DownloadStatus.OK){
            recyclerViewAdapter.loadNewData(data);
            photoList = data;
            Log.d(TAG, "onDataAvailable: data is" + data);
        }else{
            Log.e(TAG, "onDataAvailable: failed with status" + downloadStatus);
        }

        Log.d(TAG, "onDataAvailable: finished") ;

    }


    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: starts");
//        Toast.makeText(this, "Normal tap at position" + position, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra(PHOTO_TRANSFER, recyclerViewAdapter.getPhoto(position));
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

        Log.d(TAG, "onItemLongClick: starts");
//        Toast.makeText(this, "Long tap at position" + position, Toast.LENGTH_SHORT).show();


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


        builder.setTitle("Save the Feed...");


        builder.setMessage("Do you want to Save this Feed.");


        //Button One : Yes
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(MainActivity.this, "Yes button Clicked!", Toast.LENGTH_LONG).show();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SavedFeeds").child(firebaseUser.getUid().toString());

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("title", photoList.get(position).getTitle());
                hashMap.put("author", photoList.get(position).getAuthor());
                hashMap.put("authorId", photoList.get(position).getAuthorId());
                hashMap.put("link", photoList.get(position).getLink());
                hashMap.put("tags", photoList.get(position).getTags());
                hashMap.put("image", photoList.get(position).getImage());

                databaseReference.push().setValue(hashMap);

            }
        });


        //Button Two : No
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(MainActivity.this, "No button Clicked!", Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });

        AlertDialog diag = builder.create();
        diag.show();


    }
}
