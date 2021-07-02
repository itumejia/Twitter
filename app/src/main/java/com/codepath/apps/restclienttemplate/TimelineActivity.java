package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    private static final String TAG = "AppCompatActivity";
    private static final int REQUEST_CODE_FROM_COMPOSE_TWEET = 20;
    private static final int REQUEST_CODE_FROM_DETAILS_ACTIVITY = 21;
    private TwitterClient client;
    private RecyclerView recyclerView;
    private List<Tweet> tweets;
    private TweetsAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //Set the toolbar as the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        //Get new Twitter client
        client = TwitterApp.getRestClient(this);

        //Get RV
        recyclerView = findViewById(R.id.rvTweets);

        //Initialize list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        //RV setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        populateHomeTimeline();

        //Swipe container configuration
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        //Set up refresh listener
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });
        //Setup refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    //Get data of the timeline from the API
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Got timeline OK"+ json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "JSON array not added", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG,"Got timeline FAILURE", throwable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("nklvd", "compose");
        switch (item.getItemId()){
            //Logout menu item selected
            case R.id.menu_log_out:
                logoutAccount();
                return true;

            case R.id.menu_compose:
                Log.i("nklvd", "compose");
                Intent intent = new Intent(this, ComposeActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FROM_COMPOSE_TWEET);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void logoutAccount() {
        //Log out from the client
        client.clearAccessToken();
        //Go back to login screen
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        //Coming back from composing a tweet
        if (requestCode == REQUEST_CODE_FROM_COMPOSE_TWEET && resultCode == RESULT_OK){
            //Update RV with new tweet
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("Tweet"));
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            recyclerView.smoothScrollToPosition(0); //Scroll to the top to see the tweet

        }
        else if (requestCode == REQUEST_CODE_FROM_DETAILS_ACTIVITY && resultCode == RESULT_OK){
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("Tweet"));
            int timelinePosition = data.getIntExtra("position", -1);
            tweets.set(timelinePosition,tweet);
            adapter.notifyItemChanged(timelinePosition);
            recyclerView.scrollToPosition(timelinePosition);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Update timeline with new tweets
    public void fetchTimelineAsync(){
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                tweets.clear();
                try {
                    tweets.addAll(Tweet.fromJsonArray(json.jsonArray));
                    adapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(TimelineActivity.this, "Could not refresh timeline", Toast.LENGTH_SHORT).show();

            }
        });
    }
}