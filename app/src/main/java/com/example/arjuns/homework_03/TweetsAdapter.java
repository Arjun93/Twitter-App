package com.example.arjuns.homework_03;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;

/**
 * Created by asanthan on 7/16/15.
 */
public class TweetsAdapter extends BaseAdapter {
    private Context mContext;
    private Cursor myMediaCursor;
    Tab1Fragment myFetcher;
    private static LayoutInflater myInflater;
    ListView myListView;
    TextView myListElementTextView;
    List<twitter4j.Status> tweetData;
    ArrayList<String> values = new ArrayList<String>();

    // Constructor
    public TweetsAdapter(Context c, List<twitter4j.Status> statuses){
        mContext = c;
        myInflater =  (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tweetData = statuses;

        /*for (twitter4j.Status status : statuses) {
            values.add("@" + status.getUser().getScreenName() + " - " + status.getText());
            System.out.println("Arjun "+"@" + status.getUser().getScreenName() + " - " + status.getText());
        }*/

    }

    @Override
    public int getCount() {
        return tweetData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = myInflater.inflate(R.layout.list_view_element_layout, null);
        }
        myListView = (ListView)view.findViewById(R.id.mylistView);
        myListElementTextView = (TextView)view.findViewById(R.id.list_view_text_view);
        myListElementTextView.setText(tweetData.get(position).getText());

        return view;
    }
}
