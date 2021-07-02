package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

import static com.codepath.apps.restclienttemplate.TweetsOperations.dislike;
import static com.codepath.apps.restclienttemplate.TweetsOperations.like;
import static com.codepath.apps.restclienttemplate.TweetsOperations.retweet;
import static com.codepath.apps.restclienttemplate.TweetsOperations.unretweet;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    private static final int REQUEST_CODE_FROM_DETAILS_ACTIVITY = 21; //Request code to go back to Timeline Activity after going to a Details Activity
    private Context context;
    private List<Tweet> tweets;
    TwitterClient client;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
        this.client = TwitterApp.getRestClient(context);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivProfileImage;
        TextView tvName;
        TextView tvScreenName;
        TextView tvBody;
        TextView tvRelativeTime;
        ImageView ivMedia;
        private CheckBox cbLike;
        private CheckBox cbRt;
        private TextView tvRtCount;
        private TextView tvLikeCount;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);

            //Reference all the elements of the item view (other views)
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            cbLike = itemView.findViewById(R.id.cbLike);
            cbRt = itemView.findViewById(R.id.cbRt);
            tvRtCount = itemView.findViewById(R.id.tvRtCount);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);

            cbLike.setOnClickListener(this);
            cbRt.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        public void bind(Tweet tweet) {
            tvScreenName.setText(tweet.getUser().getScreenName());
            tvName.setText(tweet.getUser().getName());
            tvBody.setText(tweet.getBody());
            Glide.with(context).load(tweet.getUser().getProfileImageURL()).into(ivProfileImage);
            tvRelativeTime.setText(tweet.getRelativeTimeAgo());
            if(!tweet.getMediaUrl().isEmpty()){
                Glide.with(context).load(tweet.getMediaUrl()).into(ivMedia);
            }
            else{
                ivMedia.setImageDrawable(null);
            }
            tvLikeCount.setText(String.valueOf(tweet.getFavorite_count()));
            tvRtCount.setText(String.valueOf(tweet.getRetweet_count()));
            cbRt.setChecked(tweet.isRetweeted());
            cbLike.setChecked(tweet.isFavorited());

        }

        @Override
        public void onClick(View v) {
            Tweet tweet = tweets.get(getAdapterPosition());
            //User clicked Retweet button
            if(v == cbRt){
                if (cbRt.isChecked()){
                    retweet(context, tweet, tvRtCount, cbRt, client);
                }
                else{
                    unretweet(context, tweet, tvRtCount, cbRt, client);
                }
            }
            //User clicked Favorite button
            else if(v == cbLike){
                if (cbLike.isChecked()){
                    like(context, tweet, tvLikeCount, cbLike, client);
                }
                else{
                    dislike(context, tweet, tvLikeCount, cbLike, client);
                }
                
            }
            //User clicked in other part of the post
            else{
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                intent.putExtra("Position", getAdapterPosition());
                ((TimelineActivity) context).startActivityForResult(intent, REQUEST_CODE_FROM_DETAILS_ACTIVITY);    
            }

            
        }
    }
}
