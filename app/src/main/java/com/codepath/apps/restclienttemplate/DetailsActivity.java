package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;


import static com.codepath.apps.restclienttemplate.TweetsOperations.dislike;
import static com.codepath.apps.restclienttemplate.TweetsOperations.like;
import static com.codepath.apps.restclienttemplate.TweetsOperations.retweet;
import static com.codepath.apps.restclienttemplate.TweetsOperations.unretweet;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";

    private TwitterClient client;

    private int timelinePosition;
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

        timelinePosition = getIntent().getIntExtra("Position", -1);
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
                    retweet(DetailsActivity.this, tweet, tvDetailsRtCount, cbDetailsRt, client);
                }
                else{
                    unretweet(DetailsActivity.this, tweet, tvDetailsRtCount, cbDetailsRt, client);
                }
            }
        });

        //Like checkbox changed
        cbDetailsLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbDetailsLike.isChecked()){
                    like(DetailsActivity.this, tweet, tvDetailsLikeCount, cbDetailsLike, client);
                }
                else{
                    dislike(DetailsActivity.this, tweet, tvDetailsLikeCount, cbDetailsLike, client);
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



    @Override
    public void onBackPressed() {
        //Make intent to go back to Timeline with updated info of tweet
        Intent intent = new Intent();
        intent.putExtra("Tweet", Parcels.wrap(tweet));
        intent.putExtra("position", timelinePosition);
        setResult(RESULT_OK, intent);
        finish();
    }
}