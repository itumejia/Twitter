package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import okhttp3.Headers;

//Class with functions to make operations of Like, Dislike, Retweet and Unretweet
public class TweetsOperations {
    
    private static final String TAG = "TweetsOperations";
    public static void retweet(final Context context, final Tweet tweet, final TextView tvCount, final CheckBox cbState, TwitterClient client){
        tweet.retweeted();
        tvCount.setText(String.valueOf(tweet.getRetweet_count()));
        //Call API to rt
        client.retweetTweet(tweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Success retweet, response: " + json.toString());
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(context, "Retweet was not possible", Toast.LENGTH_SHORT).show();
                tweet.unretweeted();
                tvCount.setText(String.valueOf(tweet.getRetweet_count()));
                cbState.setChecked(false);

            }
        });
    }

    public static void unretweet(final Context context, final Tweet tweet, final TextView tvCount, final CheckBox cbState, TwitterClient client){
        tweet.unretweeted();
        tvCount.setText(String.valueOf(tweet.getRetweet_count()));
        //Call API to unrt
        client.unretweetTweet(tweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Success unretweet, response: " + json.toString());
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(context, "Unretweet was not possible", Toast.LENGTH_SHORT).show();
                tweet.retweeted();
                tvCount.setText(String.valueOf(tweet.getRetweet_count()));
                cbState.setChecked(true);

            }
        });
    }

    public static void like(final Context context, final Tweet tweet, final TextView tvCount, final CheckBox cbState, TwitterClient client) {
        tweet.liked();
        tvCount.setText(String.valueOf(tweet.getFavorite_count()));
        //Call API to like
        client.likeTweet(tweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Success like, response: " + json.toString());
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "On failure like", throwable);
                Toast.makeText(context, "Like was not possible", Toast.LENGTH_SHORT).show();
                tweet.disliked();
                tvCount.setText(String.valueOf(tweet.getFavorite_count()));
                cbState.setChecked(false);

            }
        });
    }

    public static void dislike(final Context context, final Tweet tweet, final TextView tvCount, final CheckBox cbState, TwitterClient client) {
        tweet.disliked();
        tvCount.setText(String.valueOf(tweet.getFavorite_count()));
        //Call API to dislike
        client.dislikeTweet(tweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Success dislike, response: " + json.toString());
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "On failure like", throwable);
                Toast.makeText(context, "Disike was not possible", Toast.LENGTH_SHORT).show();
                tweet.liked();
                tvCount.setText(String.valueOf(tweet.getFavorite_count()));
                cbState.setChecked(true);

            }
        });
    }
}
