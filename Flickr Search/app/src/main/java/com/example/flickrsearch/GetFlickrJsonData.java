package com.example.flickrsearch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetFlickrJsonData implements GetRawData.OnDownloadComplete {

    private static final String TAG = "GetFlickrJsonData";

    private List<Photo> photoList = null;
    private String baseUrl;

    private final OnDataAvailable callBack;

    public GetFlickrJsonData(String baseUrl, OnDataAvailable callBack) {
        Log.d(TAG, "GetFlickrJsonData: called");
        this.baseUrl = baseUrl;
        this.callBack = callBack;
    }

    interface OnDataAvailable{
        void onDataAvailable(List<Photo> data, DownloadStatus downloadStatus);
    }


    @Override
    public void onDownloadComplete(String result, DownloadStatus downloadStatus) {
        Log.d(TAG, "onDownloadComplete: starts" + downloadStatus);

        if(downloadStatus == DownloadStatus.OK){
            photoList = new ArrayList<>();


            try{
                JSONObject jsonData = new JSONObject(result);
                JSONArray itemsArray = jsonData.getJSONArray("items");

                for(int i=0; i<itemsArray.length(); i++){
                    JSONObject jsonPhoto = itemsArray.getJSONObject(i);
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");

                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jsonMedia.getString("m");

                    String link = photoUrl.replaceFirst("_m.", "_b.");

                    Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl);
                    photoList.add(photoObject);

                    Log.d(TAG, "onDownloadComplete: ends" + photoObject.toString());

                }
            }catch (JSONException e){
                Log.e(TAG, "onDownloadComplete: json Error occured" + e.getMessage());
                e.printStackTrace();
                downloadStatus = DownloadStatus.FAILED_OR_EMPTY;

            }
        }


        if(callBack != null){
            // inform the caller that the processing is done. possibly returning null if there was an error
            callBack.onDataAvailable(photoList, downloadStatus);
        }

        Log.d(TAG, "onDownloadComplete: ends");

    }

    void executeOnSameThread(String searchCriteria){
        Log.d(TAG, "executeOnSameThread: starts");
        String destinationUri = createUri(searchCriteria);

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);

        Log.d(TAG, "executeOnSameThread: ended");
    }


    String createUri(String searchCriteria){
        Log.d(TAG, "createUri: starts");

//        Uri uri = Uri.parse(baseUrl);
//        Uri.Builder builder = uri.buildUpon();
//        builder = builder.appendQueryParameter();


        return Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("tags", searchCriteria)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build().toString();
    }



}
