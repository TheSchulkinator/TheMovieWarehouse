package com.example.theschulk.themoviewarehouse;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by gregs on 6/13/2017.
 */

public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.TrailerViewHolder> {

    private String[] mTrailerValues;
    private final TrailerOnClickHandler mClickHandler;

    public interface TrailerOnClickHandler{
        void onClick(String trailerDetails);
    }

    public  TrailerRecyclerViewAdapter (TrailerOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_view, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        if(mTrailerValues != null || mTrailerValues.length > 0) {
            int trailerNumber = position + 1;
            String currentTrailerNumber = "Trailer: " + trailerNumber;
            holder.mTrailerNumberView.setText(currentTrailerNumber);
        }
        else {
            holder.mTrailerNumberView.setText(R.string.trailer_error);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mTrailerValues) return  0;
        return mTrailerValues.length;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        public final View mView;
        public final TextView mTrailerNumberView;

        public TrailerViewHolder(View view){
            super(view);
            mView = view;
            mTrailerNumberView = (TextView) view.findViewById(R.id.tv_trailer_number);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String trailerSelectedKey = mTrailerValues[adapterPosition];
            mClickHandler.onClick(trailerSelectedKey);
        }
    }

    public void setTrailerData(String[] trailerData){
        mTrailerValues = trailerData;
        notifyDataSetChanged();
    }
}
