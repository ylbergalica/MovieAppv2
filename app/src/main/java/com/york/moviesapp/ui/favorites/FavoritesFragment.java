package com.york.moviesapp.ui.favorites;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.york.moviesapp.R;
import com.york.moviesapp.database.FavoriteDao;
import com.york.moviesapp.database.FavoriteEntity;
import com.york.moviesapp.database.MovieDao;
import com.york.moviesapp.database.MovieDatabase;
import com.york.moviesapp.database.MovieEntity;
import com.york.moviesapp.databinding.FragmentNotificationsBinding;
import com.york.moviesapp.recyclerview.FavoritesViewAdapter;
import com.york.moviesapp.recyclerview.GenresViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private FavoriteDao favoriteDao;
    private MovieDao movieDao;
    private MovieDatabase movieDatabase;
    private FragmentNotificationsBinding binding;
    private ArrayList<FavoriteEntity> favoriteList = new ArrayList<>();
    private ArrayList<MovieEntity> movieList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        FavoritesViewModel notificationsViewModel =
                new ViewModelProvider(this).get(FavoritesViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        movieDatabase = MovieDatabase.getInstance(getContext());
        movieDao = movieDatabase.movieDao();
        favoriteDao = movieDatabase.favoriteDao();

        getFavoriteIds();

        return root;
    }

    private void getFavoriteIds(){
            new getFavoriteIdsAsyncTask().execute();
    }

    private class getFavoriteIdsAsyncTask extends AsyncTask<Void, Void, List<FavoriteEntity>> {
        @Override
        protected List<FavoriteEntity> doInBackground(Void... voids) {
            return favoriteDao.getFavoriteMovies();
        }

        @Override
        protected void onPostExecute(List<FavoriteEntity> favoriteEntities) {
            super.onPostExecute(favoriteEntities);

            if (favoriteList.isEmpty()) {
                favoriteList.addAll(favoriteEntities);
            }

            getAllFavoriteMovies();
        }
    }

    private void getAllFavoriteMovies(){
        new getAllFavoriteMoviesAsyncTask().execute();
    }

    private class getAllFavoriteMoviesAsyncTask extends AsyncTask<Void, Void, List<MovieEntity>> {
        @Override
        protected List<MovieEntity> doInBackground(Void... voids) {
            return movieDao.getAllMovies();
        }

        @Override
        protected void onPostExecute(List<MovieEntity> movies) {
            super.onPostExecute(movies);

            if (movieList.isEmpty()) {
                for (FavoriteEntity favorite : favoriteList) {
                    for (MovieEntity movie : movies) {
                        if (favorite.getMovieId() == movie.getId()) {
                            movieList.add(movie);
                        }
                    }
                }
            }

            binding.favoritesView.setAdapter(new FavoritesViewAdapter(movieList, getContext(), getLayoutInflater(), new GenresViewAdapter.OnMovieClickListener() {
                @Override
                public void onMovieClick(com.york.moviesapp.database.MovieEntity movie) {
                    // Handle movie click
                    Bundle bundle = new Bundle();
                    bundle.putInt("movieId", movie.getId());
                    NavHostFragment.findNavController(FavoritesFragment.this)
                            .navigate(R.id.action_favorites_to_details, bundle);
                }
            }));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}