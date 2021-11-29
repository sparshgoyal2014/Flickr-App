package com.example.flickrsearch;

import android.content.Context;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {

    private static final String TAG = "RecyclerItemClickListen";

    interface OnRecyclerClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);

    }


    private final OnRecyclerClickListener listener;
    private final GestureDetectorCompat gestureDetectorCompat;


    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, final OnRecyclerClickListener listener) {
        this.listener = listener;
        gestureDetectorCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if(childView != null && listener != null){
                    Log.d(TAG, "onSingleTapUp: Calling listener onItemClick");
                    listener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && listener != null){
                    Log.d(TAG, "onLongPress: calling listener onItemLongClick");
                    listener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv,  MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: starts");

        if(gestureDetectorCompat != null){
            boolean result = gestureDetectorCompat.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent: returned" + result);
            return result;
        }else{
            Log.d(TAG, "onInterceptTouchEvent: returned false");
            return false;
        }
    }
}
