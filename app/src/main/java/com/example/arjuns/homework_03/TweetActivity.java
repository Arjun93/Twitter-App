package com.example.arjuns.homework_03;

/**
 * Created by asanthan on 7/15/15.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by arjuns on 7/15/2015.
 */
public class TweetActivity extends Activity {


    private static Twitter twitter;
    // Twitter

    private static RequestToken requestToken;

    private static SharedPreferences mSharedPreferences;
    // Shared Preferences

    private String oauth_url = "", oauth_verifier = "", profile_url = "";
    //oauth url

    Dialog auth_dialog;
    WebView myWebView;
    AccessToken accessToken;
    String path;
    BitmapFactory.Options myBitmapOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_to_twitter_layout);

        ImageView tweetImageView = (ImageView) findViewById(R.id.image_to_be_posted);
        Button tweetButton = (Button) findViewById(R.id.tweet_button);

        Intent myIntent = getIntent();
        path = myIntent.getStringExtra("FilePath");
        myBitmapOptions = new BitmapFactory.Options();
        myBitmapOptions.inSampleSize = 2;
        tweetImageView.setImageBitmap(BitmapFactory.decodeFile(path, myBitmapOptions));

        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToTwitter();
            }
        });
    }


    public void loginToTwitter() {

        mSharedPreferences=getApplicationContext().getSharedPreferences("MyPref", 0);
        //Shared preferences
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString("CONSUMER_KEY", ConstantValuesClass.TWITTER_CONSUMER_KEY);
        edit.putString("CONSUMER_SECRET",ConstantValuesClass.TWITTER_CONSUMER_SECRET);
        edit.commit();

        mSharedPreferences = getPreferences(0);
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(mSharedPreferences.getString("CONSUMER_KEY", ConstantValuesClass.TWITTER_CONSUMER_KEY), mSharedPreferences.getString("CONSUMER_SECRET", ConstantValuesClass.TWITTER_CONSUMER_SECRET));


        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
            new TwitterGetAccessTokenTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            new TwitterGetAccessTokenTask().execute();
    }

    public void postStatus() {

        SimpleDateFormat postDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String currentTime = postDateFormat.format(new Date());
        String tag= "@" + getResources().getString(R.string.twitter_tag);

        String message = tag +"; " + currentTime;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
            new UpdateTwitterStatus().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,message);
        else
            new UpdateTwitterStatus().execute();
    }

    private class TwitterGetAccessTokenTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            try {
                requestToken = twitter.getOAuthRequestToken("https://sophie.mobileapp.com");
                oauth_url = requestToken.getAuthorizationURL();
                Log.e("Oauth URL", oauth_url);
            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return oauth_url;
        }

        @Override
        protected void onPostExecute(String oauth_url) {

            if (oauth_url != null) {
                auth_dialog = new Dialog(TweetActivity.this);
                auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                auth_dialog.setContentView(R.layout.oauth_dialog_layout);
                myWebView = (WebView) auth_dialog.findViewById(R.id.my_web_view);
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.loadUrl(oauth_url);
                myWebView.setWebViewClient(new WebViewClient() {
                    boolean authComplete = false;

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.contains("oauth_verifier") && authComplete == false) {
                            authComplete = true;
                            Log.e("Url", url);
                            Uri uri = Uri.parse(url);
                            oauth_verifier = uri.getQueryParameter("oauth_verifier");

                            auth_dialog.dismiss();
                            new AccessTokenGet().execute();
                        } else if (url.contains("denied")) {
                            auth_dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Sorry!, Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                auth_dialog.show();
                auth_dialog.setCancelable(true);

            } else {

                Toast.makeText(getApplicationContext(), "Sorry!, Network Error or Invalid Credentials", Toast.LENGTH_SHORT).show();


            }
        }
    }

    private class AccessTokenGet extends AsyncTask<String, String, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Boolean doInBackground(String... args) {

            try {
                accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);
                SharedPreferences.Editor edit = mSharedPreferences.edit();
                edit.putString(ConstantValuesClass.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                edit.putString(ConstantValuesClass.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                User user = twitter.showUser(accessToken.getUserId());
                edit.putString("NAME", user.getName());
                edit.commit();

            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            if (response) {
                Log.i("STATUS", "logged in");
                postStatus();
                Log.i("STATUS", "Status posted");
                }
        }
    }

    class UpdateTwitterStatus extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * getting Places JSON
         * */
        protected String doInBackground(String... args) {

            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(ConstantValuesClass.TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(ConstantValuesClass.TWITTER_CONSUMER_SECRET);

                // Access Token
                String access_token = mSharedPreferences.getString(ConstantValuesClass.PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(ConstantValuesClass.PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                File myFile = new File(path);
                try {
                    StatusUpdate status = new StatusUpdate(args[0]);
                    status.setMedia(myFile);
                    twitter.updateStatus(status);
                }
                catch(TwitterException e){
                    Log.d("TAG", "Pic Upload error" + e.getErrorMessage());
                    throw e;
                }

            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            //pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Tweeted!!!", Toast.LENGTH_SHORT).show();
                    // Clearing EditText field
                }
            });
        }

    }

}
