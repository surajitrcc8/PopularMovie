package com.example.android.popularmovie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovie.model.Trailers;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by surajitbiswas on 7/21/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    ArrayList<Trailers> mTrailers = null;
    public TrailerListItemClickListener trailerListItemClickListener;

    public interface TrailerListItemClickListener{
        public void onClickedTrailerListItem(String key);
    }

    public TrailerAdapter(TrailerListItemClickListener trailerListItemClickListener) {
        this.trailerListItemClickListener = trailerListItemClickListener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.trailer_list_item,parent,false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        if(mTrailers != null){
            holder.mTrailerNameTextView.setText(mTrailers.get(position).getName());
        }
    }

    @Override
    public int getItemCount() {
        return (mTrailers == null) ? 0 : mTrailers.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTrailerNameTextView;
        public TrailerViewHolder(View itemView) {
            super(itemView);
            mTrailerNameTextView = (TextView) itemView.findViewById(R.id.tv_trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            trailerListItemClickListener.onClickedTrailerListItem(mTrailers.get(position).getKey());
        }
    }

    public void setmTrailers(ArrayList<Trailers> mTrailers) {
        this.mTrailers = mTrailers;
    }
}
