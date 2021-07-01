package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {

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

    }
}