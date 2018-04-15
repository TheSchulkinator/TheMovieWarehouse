package com.example.theschulk.themoviewarehouse;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyReviewRecyclerViewAdapter extends RecyclerView.Adapter<MyReviewRecyclerViewAdapter.ViewHolder> {

    private String[][] mReviewValues;

    public MyReviewRecyclerViewAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(mReviewValues != null || mReviewValues.length > 0){
        holder.mIdView.setText(mReviewValues[0][position] + ":");
        holder.mContentView.setText(mReviewValues[1][position]);
        } else{
            holder.mContentView.setText(R.string.review_error);
        }

    }

    @Override
    public int getItemCount() {
        if(null == mReviewValues) return 0;
        int arrayLength = mReviewValues[0].length;
        return arrayLength;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public void setReviewData(String[][] reviewData){
        mReviewValues = reviewData;
        notifyDataSetChanged();
    }
}
