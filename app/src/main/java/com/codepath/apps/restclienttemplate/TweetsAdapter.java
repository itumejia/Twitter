package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    private static final int REQUEST_CODE_FROM_DETAILS_ACTIVITY = 21; //Request code to go back to Timeline Activity after going to a Details Activity
    private Context context;
    private List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
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

//    //Methods to use for the SwipeRefresh
//    public void clear(){
//        tweets.clear();
//        notifyDataSetChanged();
//    }
//    public void addAll(List<Tweet> list) {
//        tweets.addAll(list);
//        notifyDataSetChanged();
//    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivProfileImage;
        TextView tvScreenName;
        TextView tvBody;
        TextView tvRelativeTime;
        ImageView ivMedia;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);

            //Reference all the elements of the item view (other views)
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvRelativeTime = itemView.findViewById(R.id.tvDetailsRelativeTime);
            ivMedia = itemView.findViewById(R.id.ivDetailsMedia);
            itemView.setOnClickListener(this);

        }

        public void bind(Tweet tweet) {
            tvScreenName.setText(tweet.getUser().getScreenName());
            tvBody.setText(tweet.getBody());
            Glide.with(context).load(tweet.getUser().getProfileImageURL()).into(ivProfileImage);
            tvRelativeTime.setText(tweet.getRelativeTimeAgo());
            if(!tweet.getMediaUrl().isEmpty()){
                Glide.with(context).load(tweet.getMediaUrl()).into(ivMedia);
            }
            else{
                ivMedia.setImageDrawable(null);
            }
        }

        @Override
        public void onClick(View v) {
            Log.i("Adapter", "Item clicked");
            Tweet tweet = tweets.get(getAdapterPosition());
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
            intent.putExtra("Position", getAdapterPosition());
            ((TimelineActivity) context).startActivityForResult(intent, REQUEST_CODE_FROM_DETAILS_ACTIVITY);
        }
    }
}
