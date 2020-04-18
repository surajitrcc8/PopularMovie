package com.example.android.popularmovie.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovie.R;
import com.example.android.popularmovie.model.Reviews;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by surajitbiswas on 7/22/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private ArrayList<Reviews> mReviews = null;
    private Context context;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.review_list_item,parent,false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        if(mReviews != null){
            holder.mAuthorTextView.setText(String.format(context.getString(R.string.review_by),mReviews.get(position).getAuthor()));
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                holder.mContentTextView.setText(Html.fromHtml(mReviews.get(position).getContent(), Html.FROM_HTML_MODE_COMPACT));
            }else{
                holder.mContentTextView.setText(Html.fromHtml(mReviews.get(position).getContent(), Html.FROM_HTML_MODE_COMPACT));
            }

        }

    }

    @Override
    public int getItemCount() {
        return (mReviews == null) ? 0 : mReviews.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{
        private TextView mAuthorTextView;
        private TextView mContentTextView;
        public ReviewViewHolder(View itemView) {
            super(itemView);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.tv_author);
            mContentTextView = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
    public void setmReviews(ArrayList<Reviews> mReviews) {
        this.mReviews = mReviews;
        notifyDataSetChanged();
    }

}
