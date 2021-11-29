package com.example.flickrsearch;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Security;

enum DownloadStatus {
    IDLE,
    PROCESSING,
    NOT_INITIALIZED,
    FAILED_OR_EMPTY,
    OK
}


//package private
class GetRawData extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetRawData";
    private DownloadStatus downloadStatus;
    private final OnDownloadComplete callback;


    interface OnDownloadComplete{
        void onDownloadComplete(String result, DownloadStatus downloadStatus);
    }

    public GetRawData(OnDownloadComplete onDownloadComplete) {
        this.callback = onDownloadComplete;
        this.downloadStatus = DownloadStatus.IDLE;
    }

    @Override
    protected String doInBackground(String... strings) {

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        if(strings == null){
            downloadStatus = DownloadStatus.NOT_INITIALIZED;
            return null;
        }

        try{
            this.downloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            int response = httpURLConnection.getResponseCode();
            Log.d(TAG, "doInBackground: The Response code: " + response);

            StringBuilder result = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            String line;
            while((line = bufferedReader.readLine()) != null){
                result.append(line).append("\n");
            }

            this.downloadStatus = DownloadStatus.OK;
            return result.toString();

        }catch (MalformedURLException e){
            Log.e(TAG, "doInBackground: Invalid URL" + e.getMessage());
        }catch (IOException e){
            Log.e(TAG, "doInBackground: IOException reading Data" + e.getMessage());
        }catch (SecurityException e){
            Log.e(TAG, "doInBackground: Security Exception. Needs Permission?" + e.getMessage());
        }
        finally {
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }

            if(bufferedReader != null){
                try{
                    bufferedReader.close();
                }catch (IOException e){
                    Log.e(TAG, "doInBackground: Error Closing Stream" + e.getMessage() );
                }
            }
        }


        this.downloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if(callback != null){
            callback.onDownloadComplete(s, downloadStatus);
        }
        Log.d(TAG, "onPostExecute: ends");

    }
}
