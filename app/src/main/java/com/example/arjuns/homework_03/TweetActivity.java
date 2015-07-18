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
import android.widget.EditText;
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


    private static Twitter myTwitter;
    private static RequestToken requestToken;
    private static SharedPreferences mySharedPreferences;
    Boolean isTwitterLoggedIn, authFlag;
    private String oauthUrl = "", oauthVerifier = "", resultantTweet;
    ProgressDialog myProgDialog;
    Dialog authDialog;
    WebView myWebView;
    AccessToken accessToken;
    String myPath;
    Button tweetButton;
    ImageView tweetImageView;
    EditText tweetEditText;
    BitmapFactory.Options myBitmapOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_to_twitter_layout);

        Intent myIntent = getIntent();
        myPath = myIntent.getExtras().getString("FilePath"); //Obtaining the file path

        //Initializing the UI components present in post_to_twitter_layout
        tweetButton = (Button) findViewById(R.id.tweet_button);
        tweetImageView = (ImageView) findViewById(R.id.image_to_be_posted);
        tweetEditText = (EditText) findViewById(R.id.edit_text_tweet);

        //Obtaining sharedpreferences
        mySharedPreferences = getApplicationContext().getSharedPreferences("MyPreference", 0);
        mySharedPreferences = getPreferences(0);
        isTwitterLoggedIn = mySharedPreferences.getBoolean(ConstantValuesClass.IS_TWITTER_LOGGEDIN_INITIALLY,false);
        //If the user has already logged into twitter, the user is allowed to check the content he would be tweeting.
        if(isTwitterLoggedIn) {
            authFlag = true;
            checkTweetContent();
        }
        //If the user is not looged into twitter, he/she is displayed an alert dialog indicating that this app would be using his/her twitter login credentials.
        else {
            createAndShowAlertDialog();
        }
    }


    /**
    * createAndShowAlertDialog() creates and displays an alert dialog indicating that this application would be
    * using his/her twitter login credentials
     */
    public void createAndShowAlertDialog() {

        AlertDialog.Builder myAlertDialogBuilder = new AlertDialog.Builder(TweetActivity.this);
        myAlertDialogBuilder.setTitle("Request Twitter Access"); //Setting the alert dialog title
        myAlertDialogBuilder.setMessage("Homework-04 application would like to access your twitter account on behalf of you."); //Setting the alert dialog meesage
        myAlertDialogBuilder.setCancelable(true);

        //If the user clicks the OK button
        myAlertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                loginToTwitter();
            }
        });

        //If the user clicks DONT ALLOW
        myAlertDialogBuilder.setNegativeButton("DONT ALLOW", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
            }
        });
        AlertDialog myAlertDialog = myAlertDialogBuilder.create(); //Creating an alert dialog
        myAlertDialog.show(); //Displaying altert dialog
    }

    /**
     * loginToTwitter() enables the user to log into twitter
     */
    public void loginToTwitter() {

        SharedPreferences.Editor edit = mySharedPreferences.edit();
        edit.putString("CONSUMER_KEY", ConstantValuesClass.TWITTER_CONSUMER_KEY);
        edit.putString("CONSUMER_SECRET", ConstantValuesClass.TWITTER_CONSUMER_SECRET);
        edit.commit();
        //Obtaining the shared preferences
        mySharedPreferences = getPreferences(0);
        myTwitter = new TwitterFactory().getInstance();
        myTwitter.setOAuthConsumer(mySharedPreferences.getString("CONSUMER_KEY", ConstantValuesClass.TWITTER_CONSUMER_KEY), mySharedPreferences.getString("CONSUMER_SECRET", ConstantValuesClass.TWITTER_CONSUMER_SECRET));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new ObtainAccessTokenTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            new ObtainAccessTokenTask().execute();
    }

    /**
     * checkTweetContent() enables the user to have a look at the content he/she would be tweeting
     */
    private void checkTweetContent() {

        try {
            EditText tweetText = (EditText) findViewById(R.id.edit_text_tweet);
            tweetText.setVisibility(View.VISIBLE);
            tweetButton.setVisibility(View.VISIBLE);

            //Setting the twitter status
            String handle = "@MobileApp4";
            String myAndrewID = "asanthan";
            String myDeviceName = Build.MANUFACTURER + " " + Build.MODEL;
            String deviceOsVersion = Build.VERSION.RELEASE;
            SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            Date myCurrentDate = new Date();
            String myCurrentDateTime = myDateFormat.format(myCurrentDate);
            resultantTweet = handle + "; " + myAndrewID + "; " + myDeviceName + "; " + deviceOsVersion + "; " + myCurrentDateTime;

            //Setting the BitmapOptions for the photo to be displayed.
            myBitmapOptions = new BitmapFactory.Options();
            myBitmapOptions.inSampleSize = 2;
            tweetImageView.setImageBitmap(BitmapFactory.decodeFile(myPath, myBitmapOptions));
            tweetEditText.setText(resultantTweet);

            //OnClickListener for tweet button
            tweetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Post status only if the user is logged in.
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
     * postStatus() allows the status to be posted.
     */
    public void postStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new PostTwitterStatus().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, resultantTweet);
        else
            new PostTwitterStatus().execute();
    }


    private class ObtainAccessTokenTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            try {
                requestToken = myTwitter.getOAuthRequestToken(ConstantValuesClass.TWITTER_CALLBACK_URL);
                oauthUrl = requestToken.getAuthorizationURL();
                Log.d("Twitter Oauth URL", oauthUrl);
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
                //Creating the webview for authentication dialog
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
                            new FetchAccessToken().execute();
                        }
                        else if (url.contains("denied")) {
                            authDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Permission has been denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //Displaying authentication dialog
                authDialog.show();
                authDialog.setCancelable(true);
            }
            else {
                Toast.makeText(getApplicationContext(), "Network Error or Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * showProgressDialog() displays progress bar when twitter status is being posted
     */
    public void showProgressDialog(String message) {

        myProgDialog = new ProgressDialog(this);
        myProgDialog.setMessage(message); //Setting the message for progress dialog
        myProgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        myProgDialog.setIndeterminate(true);
        myProgDialog.show(); //Displaying progress dialog

    }

    /*
    * FetchAccessToken is an async task used for obtaining the access token
     */
    private class FetchAccessToken extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            try {
                //Obtaining requestToken
                accessToken = myTwitter.getOAuthAccessToken(requestToken, oauthVerifier);
                SharedPreferences.Editor edit = mySharedPreferences.edit();
                edit.putString(ConstantValuesClass.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                edit.putString(ConstantValuesClass.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                //Ontaining the twitter user name
                User user = myTwitter.showUser(accessToken.getUserId());
                edit.putString(ConstantValuesClass.USER_NAME, user.getName());
                edit.commit();
            }
            catch (TwitterException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            if (response) {
                authFlag = true; //Setting back authentication flag to true
                SharedPreferences.Editor edit = mySharedPreferences.edit();
                edit.putBoolean(ConstantValuesClass.IS_TWITTER_LOGGEDIN_INITIALLY, true);
                edit.commit();
                checkTweetContent(); //Enabling the user to check the tweet content after first time log in.
            }
        }
    }

    /*
    * PostTwitterStatus is an async task that tweets the hardcoded twitter status
     */
    class PostTwitterStatus extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Tweeting in progress..."); //Show progress dialog while tweeting
        }

        protected String doInBackground(String... args) {

            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(ConstantValuesClass.TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(ConstantValuesClass.TWITTER_CONSUMER_SECRET);
                String access_token = mySharedPreferences.getString(ConstantValuesClass.PREF_KEY_OAUTH_TOKEN, ""); //Obtaining access token
                String access_token_secret = mySharedPreferences.getString(ConstantValuesClass.PREF_KEY_OAUTH_SECRET, ""); //Obtaining access token secret

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
                StatusUpdate myStatusUpdate = new StatusUpdate(status);
                File file = new File(myPath);
                //Updating the twitter status
                if (file.exists()) {
                    myStatusUpdate.setMedia(file);
                    twitter4j.Status response = twitter.updateStatus(myStatusUpdate);
                    Log.d("Twitter Status", " " + response.getText());
                }

            }
            catch (TwitterException e) {
                Log.d("Twitter Update Failed", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            myProgDialog.hide();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Tweeted!", Toast.LENGTH_SHORT).show();
                }
            });
            finish();
        }
    }
}

