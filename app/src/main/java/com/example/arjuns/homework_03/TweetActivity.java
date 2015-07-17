package com.example.arjuns.homework_03;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
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


    private static Twitter myTwitter;
    // Twitter
    private static RequestToken requestToken;
    private static SharedPreferences mySharedPreferences;
    Boolean isTwitterLoggedIn, authFlag;
    // Shared Preferences

    private String oauthUrl = "", oauthVerifier = "";
    //oauth url
    ProgressDialog myProgDialog;
    Dialog authDialog;
    WebView myWebView;
    AccessToken accessToken;
    String myPath;
    Button tweetButton;
    ImageView tweetImageView;
    TextView tweetTextView;
    String resultantTweet;
    BitmapFactory.Options myBitmapOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_to_twitter_layout);

        Intent myIntent = getIntent();
        myPath = myIntent.getExtras().getString("FilePath");
        tweetButton = (Button) findViewById(R.id.tweet_button);
        tweetImageView = (ImageView) findViewById(R.id.image_to_be_posted);
        tweetTextView = (TextView) findViewById(R.id.text_view_tweet);

        mySharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        mySharedPreferences = getPreferences(0);
        isTwitterLoggedIn = mySharedPreferences.getBoolean(ConstantValuesClass.IS_TWITTER_LOGGEDIN_INITIALLY,false);

        if(isTwitterLoggedIn) {
            authFlag = true;
            checkTweetContent();
        }
        else {
            createAndShowAlertDialog();
        }

    }

    public void createAndShowAlertDialog() {
        AlertDialog.Builder myAlertDialogBuilder = new AlertDialog.Builder(TweetActivity.this);
        myAlertDialogBuilder.setTitle("Request Twitter Access");
        myAlertDialogBuilder.setMessage("Homework-04 application would like to access your twitter account on behalf of you.");
        myAlertDialogBuilder.setCancelable(true);
        myAlertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                loginToTwitter();
            }
        });
        myAlertDialogBuilder.setNegativeButton("Dont allow", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finishActivity();
            }
        });
        AlertDialog myAlertDialog = myAlertDialogBuilder.create();
        myAlertDialog.show();
    }

    /**
     * Method to log into twitter
     */
    public void loginToTwitter() {

        //Shared preferences
        SharedPreferences.Editor edit = mySharedPreferences.edit();
        edit.putString("CONSUMER_KEY", ConstantValuesClass.TWITTER_CONSUMER_KEY);
        edit.putString("CONSUMER_SECRET", ConstantValuesClass.TWITTER_CONSUMER_SECRET);
        edit.commit();

        mySharedPreferences = getPreferences(0);
        myTwitter = new TwitterFactory().getInstance();
        myTwitter.setOAuthConsumer(mySharedPreferences.getString("CONSUMER_KEY", ConstantValuesClass.TWITTER_CONSUMER_KEY), mySharedPreferences.getString("CONSUMER_SECRET", ConstantValuesClass.TWITTER_CONSUMER_SECRET));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new TwitterGetAccessTokenTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            new TwitterGetAccessTokenTask().execute();
    }

    private void checkTweetContent() {
        try {

            TextView tweetText = (TextView) findViewById(R.id.text_view_tweet);
            tweetText.setVisibility(View.VISIBLE);
            tweetButton.setVisibility(View.VISIBLE);

            String handle = "@MobileApp4";
            String myAndrewID = "asanthan";
            String myDeviceName = Build.MANUFACTURER + " " + Build.MODEL;
            String deviceOsVersion = Build.VERSION.RELEASE;
            SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            Date myCurrentDate = new Date();
            String myCurrentDateTime = myDateFormat.format(myCurrentDate);
            resultantTweet = handle + "; " + myAndrewID + "; " + myDeviceName + "; " + deviceOsVersion + "; " + myCurrentDateTime;

            myBitmapOptions = new BitmapFactory.Options();
            myBitmapOptions.inSampleSize = 2;
            tweetImageView.setImageBitmap(BitmapFactory.decodeFile(myPath, myBitmapOptions));
            tweetTextView.setText(resultantTweet);

            tweetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //loginToTwitter();
                    if (authFlag) {
                        postStatus();
                    } else {
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to post status
     */
    public void postStatus() {

        //String message = getMessage();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new UpdateTwitterStatus().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, resultantTweet);
        else
            new UpdateTwitterStatus().execute();
    }

    private class TwitterGetAccessTokenTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            try {
                requestToken = myTwitter.getOAuthRequestToken(ConstantValuesClass.TWITTER_CALLBACK_URL);
                oauthUrl = requestToken.getAuthorizationURL();
                Log.d("Oauth URL", oauthUrl);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return oauthUrl;
        }

        @Override
        protected void onPostExecute(String oauth_url) {

            if (oauth_url != null) {
                authDialog = new Dialog(TweetActivity.this);
                authDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


                authDialog.setContentView(R.layout.oauth_dialog_layout);
                myWebView = (WebView) authDialog.findViewById(R.id.my_web_view);
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.loadUrl(oauth_url);
                myWebView.setWebViewClient(new WebViewClient() {
                    boolean isAuthComplete = false;

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.contains("oauth_verifier") && isAuthComplete == false) {
                            isAuthComplete = true;
                            Log.e("Url", url);
                            Uri uri = Uri.parse(url);
                            oauthVerifier = uri.getQueryParameter("oauth_verifier");
                            authDialog.dismiss();
                            new AccessTokenGet().execute();
                        } else if (url.contains("denied")) {
                            authDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Sorry !, Permission Denied", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
                authDialog.show();
                authDialog.setCancelable(true);
            }
            else {
                Toast.makeText(getApplicationContext(), "Sorry !, Network Error or Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * To show a progress dialog
     * @param message
     */
    public void showProgressDialog(String message) {
        myProgDialog = new ProgressDialog(this);
        myProgDialog.setMessage(message);
        myProgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        myProgDialog.setIndeterminate(true);
        myProgDialog.show();
    }

    /**
     * To hide the progress dialog
     */
    public void hideProgressDialog() {
        myProgDialog.hide();
    }

    private class AccessTokenGet extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            try {
                accessToken = myTwitter.getOAuthAccessToken(requestToken, oauthVerifier);
                SharedPreferences.Editor edit = mySharedPreferences.edit();
                edit.putString(ConstantValuesClass.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                edit.putString(ConstantValuesClass.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                User user = myTwitter.showUser(accessToken.getUserId());
                edit.putString(ConstantValuesClass.USER_NAME, user.getName());
                edit.commit();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            if (response) {
                // postStatus();
                authFlag = true;

                SharedPreferences.Editor edit = mySharedPreferences.edit();
                edit.putBoolean(ConstantValuesClass.IS_TWITTER_LOGGEDIN_INITIALLY, true);
                edit.commit();
                checkTweetContent();
            }
        }
    }

    class UpdateTwitterStatus extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Tweeting your status..");
        }

        protected String doInBackground(String... args) {

            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(ConstantValuesClass.TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(ConstantValuesClass.TWITTER_CONSUMER_SECRET);

                String access_token = mySharedPreferences.getString(ConstantValuesClass.PREF_KEY_OAUTH_TOKEN, "");
                // Access Token

                String access_token_secret = mySharedPreferences.getString(ConstantValuesClass.PREF_KEY_OAUTH_SECRET, "");
                // Access Token Secret

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                //Fetch the token
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                StatusUpdate statusUpdate = new StatusUpdate(status);
                File file = new File(myPath);
                if (file.exists()) {
                    statusUpdate.setMedia(file);
                    twitter4j.Status response = twitter.updateStatus(statusUpdate);
                    Log.d("Status - Twitter", "> " + response.getText());
                } // Update status

            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            hideProgressDialog();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Status tweeted successfully", Toast.LENGTH_SHORT)
                            .show();
                }
            });
            finishActivity();
        }
    }

    public void finishActivity() {
        Intent resultIntent = new Intent();
        this.setResult(Activity.RESULT_OK, resultIntent);
        this.finish();
    }
}

