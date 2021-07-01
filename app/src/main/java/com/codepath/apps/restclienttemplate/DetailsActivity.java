package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";

    private TwitterClient client;

    private Tweet tweet;
    private ImageView ivDetailsProfileImage;
    private TextView tvDetailsRelativeTime;
    private TextView tvDetailsName;
    private TextView tvDetailsScreenName;
    private TextView tvDetailsBody;
    private ImageView ivDetailsMedia;
    private CheckBox cbDetailsLike;
    private CheckBox cbDetailsRt;
    private TextView tvDetailsRtCount;
    private TextView tvDetailsLikeCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        client = TwitterApp.getRestClient(this);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        ivDetailsProfileImage = findViewById(R.id.ivDetailsProfileImage);
        tvDetailsRelativeTime = findViewById(R.id.tvDetailsRelativeTime);
        tvDetailsName = findViewById(R.id.tvDetailsName);
        tvDetailsScreenName = findViewById(R.id.tvDetailsScreenName);
        tvDetailsBody = findViewById(R.id.tvDetailsBody);
        ivDetailsMedia = findViewById(R.id.ivDetailsMedia);
        cbDetailsLike = findViewById(R.id.cbDetailsLike);
        cbDetailsRt = findViewById(R.id.cbDetailsRt);
        tvDetailsRtCount = findViewById(R.id.tvDetailsRtCount);
        tvDetailsLikeCount = findViewById(R.id.tvDetailsLikeCount);

        //Retweet checkbox changed
        cbDetailsRt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbDetailsRt.isChecked()){
                    retweet();
                }
                else{
                    unretweet();
                }
            }
        });

        //Like checkbox changed
        cbDetailsLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbDetailsLike.isChecked()){
                    like();
                }
                else{
                    dislike();
                }
            }
        });


        bindTweetToScreen(tweet);
    }

    public void bindTweetToScreen(Tweet tweet) {
        tvDetailsScreenName.setText(tweet.getUser().getScreenName());
        tvDetailsName.setText(tweet.getUser().getName());
        tvDetailsBody.setText(tweet.getBody());
        Glide.with(this).load(tweet.getUser().getProfileImageURL()).into(ivDetailsProfileImage);
        tvDetailsRelativeTime.setText(tweet.getRelativeTimeAgo());
        if(!tweet.getMediaUrl().isEmpty()){
            Glide.with(this).load(tweet.getMediaUrl()).into(ivDetailsMedia);
        }
        tvDetailsLikeCount.setText(String.valueOf(tweet.getFavorite_count()));
        tvDetailsRtCount.setText(String.valueOf(tweet.getRetweet_count()));
        cbDetailsRt.setChecked(tweet.isRetweeted());
        cbDetailsLike.setChecked(tweet.isFavorited());

    }

    public void retweet(){
        tweet.retweeted();
        tvDetailsRtCount.setText(String.valueOf(tweet.getRetweet_count()));
        //Call API to rt
        client.retweetTweet(tweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Success retweet, response: " + json.toString());
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(DetailsActivity.this, "Retweet was not possible", Toast.LENGTH_SHORT).show();
                tweet.unretweeted();
                tvDetailsRtCount.setText(String.valueOf(tweet.getRetweet_count()));
                cbDetailsRt.setChecked(false);

            }
        });
    }

    public void unretweet(){
        tweet.unretweeted();
        tvDetailsRtCount.setText(String.valueOf(tweet.getRetweet_count()));
        //Call API to unrt
        client.unretweetTweet(tweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Success unretweet, response: " + json.toString());
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(DetailsActivity.this, "Unretweet was not possible", Toast.LENGTH_SHORT).show();
                tweet.retweeted();
                tvDetailsRtCount.setText(String.valueOf(tweet.getRetweet_count()));
                cbDetailsRt.setChecked(true);

            }
        });
    }

    public void like() {
        tweet.liked();
        tvDetailsLikeCount.setText(String.valueOf(tweet.getFavorite_count()));
        //Call API to like
        client.likeTweet(tweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Success like, response: " + json.toString());
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "On failure like", throwable);
                Toast.makeText(DetailsActivity.this, "Like was not possible", Toast.LENGTH_SHORT).show();
                tweet.disliked();
                tvDetailsLikeCount.setText(String.valueOf(tweet.getFavorite_count()));
                cbDetailsLike.setChecked(false);

            }
        });
    }

    public void dislike() {
        tweet.disliked();
        tvDetailsLikeCount.setText(String.valueOf(tweet.getFavorite_count()));
        //Call API to dislike
        client.dislikeTweet(tweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Success dislike, response: " + json.toString());
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "On failure like", throwable);
                Toast.makeText(DetailsActivity.this, "Disike was not possible", Toast.LENGTH_SHORT).show();
                tweet.liked();
                tvDetailsLikeCount.setText(String.valueOf(tweet.getFavorite_count()));
                cbDetailsLike.setChecked(true);

            }
        });
    }
}