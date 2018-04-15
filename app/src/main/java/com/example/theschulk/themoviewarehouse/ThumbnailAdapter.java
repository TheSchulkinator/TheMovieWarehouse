package com.example.theschulk.themoviewarehouse;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by gregs on 3/22/2017.
 */

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder> {

    private String[][] mTmdbParsedData;
    /*//Will hold a reference to a specific movie that will be passed onto MovieDetailActivity
    when the image is clicked on*/
    private String[] mIndividualMovieDetail;

    private final ThumbnailAdapterOnClickHandler mClickHandler;

    public interface ThumbnailAdapterOnClickHandler{
        void onClick(String[] movieDetails);
    }

    public ThumbnailAdapter(ThumbnailAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }


    public class ThumbnailViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        ImageView thumbNailImageView;

        public ThumbnailViewHolder(View view){
            super(view);
            thumbNailImageView = (ImageView) view.findViewById(R.id.iv_item_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            mIndividualMovieDetail = new String[mTmdbParsedData.length];

            //populate the string array with the needed movie data
            for(int i = 0 ; i < mTmdbParsedData.length; i++){
                mIndividualMovieDetail[i] = mTmdbParsedData[i][adapterPosition];
            }

            mClickHandler.onClick(mIndividualMovieDetail);
        }
    }

    @Override
    public ThumbnailViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.image_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ThumbnailViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ThumbnailViewHolder thumbnailViewHolder, int position) {
        String posterPathUri =  "http://image.tmdb.org/t/p/w342/" + mTmdbParsedData[1][position];
        Uri uri = Uri.parse(posterPathUri);

        Context context = thumbnailViewHolder.thumbNailImageView.getContext();

        Picasso.with(context).load(uri)
                .placeholder(R.drawable.waiting)
                .error(R.drawable.error_icon)
                .into(thumbnailViewHolder.thumbNailImageView);

    }

    @Override
    public int getItemCount() {
        if (null == mTmdbParsedData) return 0;
        return mTmdbParsedData[1].length;
    }

    public void setTmdbData(String[][] tmdbParsedData){
        mTmdbParsedData = tmdbParsedData;
        notifyDataSetChanged();
    }
}
