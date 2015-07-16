package com.example.arjuns.homework_03;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asanthan on 7/16/15.
 */
public class Tab3Fragment extends Fragment {
    private static View view;
    List<Status> statuses;
    ArrayList<String> values = new ArrayList<String>();
    ListView myListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.timeline_layout, container, false);
        myListView = (ListView)view.findViewById(R.id.mylistView);
        Twitter twitter = new TwitterFactory().getInstance();
        new CollectTwitterFeed().execute();
        return view;
    }

    public Activity returnActivity() {
        return this.getActivity();
    }

    class CollectTwitterFeed extends AsyncTask<String, String, List<twitter4j.Status>> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * getting Places JSON
         */
        protected List<twitter4j.Status> doInBackground(String... args) {
            try {
                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey("RY9BJkK4Yc6C4diEIbMpPPL4Q")
                        .setOAuthConsumerSecret("EU2ryTjoLmYclkwmmD72c8ceCaCrBhTtWC49qalTMkOu1sAuZh")
                        .setOAuthAccessToken("3377937105-CXZYeIkIjakailTyfNaFB5l0dcDu7U9jMYwU9sZ")
                        .setOAuthAccessTokenSecret("Q0EUd62sWDkllMwbX6aHUzrFHnMZBnYBLlnjOqt3gMw5O");
                TwitterFactory tf = new TwitterFactory(cb.build());
                Twitter twitter1 = tf.getInstance();
                statuses = twitter1.getUserTimeline("nujra93");
            }
            catch(TwitterException te) {
                te.printStackTrace();
                System.out.println("Failed to get timeline: " + te.getMessage());
                System.exit(-1);
            }
            return statuses;
        }

        protected void onPostExecute(List<twitter4j.Status> statuses) {

            readTimeLine(statuses);


        }
    }

    public void readTimeLine(List<twitter4j.Status> statuses) {

        /*for (twitter4j.Status status : statuses) {
            values.add("@" + status.getUser().getScreenName() + " - " + status.getText());
            System.out.println("Arjun "+"@" + status.getUser().getScreenName() + " - " + status.getText());
        }*/
        myListView.setAdapter(new TweetsAdapter(view.getContext(), statuses));

    }

}
