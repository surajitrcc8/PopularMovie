/**
 *
 */

package com.example.android.popularmovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovie.R;
import com.example.android.popularmovie.model.Movie;
import com.example.android.popularmovie.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PopularMovieAdapter extends  RecyclerView.Adapter<PopularMovieAdapter.PopularMovieHolder> {


    private static final String TAG = PopularMovieAdapter.class.getSimpleName();

    /**
     * Contaning the list of movie which we are going to show
     * in the screen
     */
    private ArrayList<Movie> mMovieList;


    /**
     *
     */
    private Context mParentContext;

    private MovieBanerClickListener movieBanerClickListener;


    public interface MovieBanerClickListener{
        public void onClickListener(int movieId);
    }

    /**
     *
     * @param context
     */
    public PopularMovieAdapter(Context context,MovieBanerClickListener listener){
        mParentContext = context;
        movieBanerClickListener = listener;
    }

    /**
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public PopularMovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.movie_list_item,parent,false);
        return new PopularMovieHolder(view);
    }

    /**
     *
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(PopularMovieHolder holder, int position) {
        holder.bind(position);
    }

    /**
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mMovieList != null ? mMovieList.size() : 0;
    }

    /**
     *
     */
    public class PopularMovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mPopularMovieImageView;
        /**
         *
         * @param itemView
         */
        public PopularMovieHolder(View itemView) {
            super(itemView);
            mPopularMovieImageView = (ImageView) itemView.findViewById(R.id.iv_popular_movie);
            itemView.setOnClickListener(this);
        }
        public void bind(int position){
            String bannerPath = NetworkUtils.MOVIE_POSTER_BASE_URL + mMovieList.get(position).getBannerPath();

            Picasso.with(mParentContext).load(bannerPath).into(mPopularMovieImageView);

           // mPopularMovieImageView.setImageResource(R.drawable.poster);
            Log.d(TAG, "Path for banner is " + bannerPath);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            movieBanerClickListener.onClickListener(mMovieList.get(position).getId());
        }
    }
    public void setItems(ArrayList<Movie> posterList){
        mMovieList = posterList;
        notifyDataSetChanged();

    }

}
