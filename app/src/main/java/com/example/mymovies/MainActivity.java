package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.adapter.MovieAdapter;
import com.example.mymovies.data.Genres;
import com.example.mymovies.data.MainViewModel;
import com.example.mymovies.data.Movie;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    private RecyclerView moviesRv;
    private Switch sortBySwitch;
    private MovieAdapter adapter;
    private TextView mostPopularTv;
    private TextView topRatedTv;
    private ProgressBar loadingProgressBar;
    private Spinner genresSpinner;

    private MainViewModel viewModel;

    private static final int LOADER_ID_MOVIES = 133;
    private LoaderManager loaderManager;

    private static int page = 1;
    private static boolean isLoading = false;
    private static int sortBy;
    private static int genreId = -1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        MenuItem mSearch = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) mSearch.getActionView();

        searchView.setQueryHint("Search a movie...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                JSONObject searchJson = NetworkUtils.getJSONFromSearch(query);
                ArrayList<Movie> searchMovies = JSONUtils.getMoviesFromJSON(searchJson);
                adapter.clear();
                adapter.setMovies(searchMovies);
                page = 1;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter.clear();
                getData(sortBy, page, genreId);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_main:
                Intent main_intent = new Intent(this, MainActivity.class);
                startActivity(main_intent);
                break;
            case R.id.menu_favourites:
                Intent favourites_intent = new Intent(this, FavouriteMoviesActivity.class);
                startActivity(favourites_intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getColumnCount(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int)(displayMetrics.widthPixels / displayMetrics.density);
        return width / 185 > 2 ? width / 185 : 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loaderManager = LoaderManager.getInstance(this);

        moviesRv = findViewById(R.id.movies_rv);
        sortBySwitch = findViewById(R.id.sort_by_switch);
        topRatedTv = findViewById(R.id.top_rate_tv);
        mostPopularTv = findViewById(R.id.most_popular_tv);
        loadingProgressBar = findViewById(R.id.loading_progressbar);
        genresSpinner = findViewById(R.id.genres_spinner);


        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        adapter = new MovieAdapter();
        moviesRv.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        moviesRv.setAdapter(adapter);

        sortBySwitch.setChecked(true);
        sortBySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                page = 1;
                setMethodOfSort(isChecked);
            }
        });
        sortBySwitch.setChecked(false);

        JSONObject jsonGenres = NetworkUtils.getJSONForGenres();
        final ArrayList<Genres> genres = JSONUtils.getGenresFromJSON(jsonGenres);
        genres.add(0, new Genres(0, "All"));
        String[] genresName = new String[genres.size()];
        for (int i = 0; i < genres.size(); i++) {
            genresName[i] = genres.get(i).getName();
        }

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genresName);
        genresSpinner.setAdapter(adapterSpinner);

        genresSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = genresSpinner.getSelectedItem().toString();
                if (genres.get(0).getName().equals(selectedItem)) {
                    genreId = -1;
                } else {
                    for (int i = 0; i < genres.size(); i++) {
                        if (genres.get(i).getName().equals(selectedItem)) {
                            genreId = genres.get(i).getId();
                            break;
                        }
                    }
                }
                adapter.clear();
                getData(sortBy, page, genreId);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                viewModel.insertMovie(movie);
                startActivity(intent);
            }
        });
        adapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if (!isLoading) {
                    getData(sortBy, page, genreId);
                }
            }
        });
        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if (page == 1) {
                    adapter.setMovies(movies);
                }
            }
        });
    }

    public void onClickMostPopular(View view) {
        setMethodOfSort(false);
        sortBySwitch.setChecked(false);
    }

    public void onClickTopRate(View view) {
        setMethodOfSort(true);
        sortBySwitch.setChecked(true);
    }

    private void setMethodOfSort(boolean isTopRated) {
        if (isTopRated) {
            sortBy = NetworkUtils.RATING;
            topRatedTv.setTextColor(getResources().getColor(R.color.colorAccent));
            mostPopularTv.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            sortBy = NetworkUtils.POPULARITY;
            topRatedTv.setTextColor(getResources().getColor(R.color.colorWhite));
            mostPopularTv.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        getData(sortBy, page, genreId);
    }

    private void getData(int sortBy, int page, int genreId) {
        URL url = NetworkUtils.buildURL(sortBy, page, genreId);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID_MOVIES, bundle, MainActivity.this);
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, args);
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.OnStartLoadingListener() {
            @Override
            public void onStartLoading() {
                isLoading = true;
            }
        });
        loadingProgressBar.setVisibility(View.VISIBLE);
        return jsonLoader;

    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(data);
        if (movies != null && movies.size() > 0) {
            if (page == 1) {
                viewModel.deleteAllMovies();
                adapter.clear();
            }
            for (Movie movie : movies) {
                viewModel.insertMovie(movie);
            }
            adapter.addMovies(movies);
            page++;
        }
        isLoading = false;
        loaderManager.destroyLoader(LOADER_ID_MOVIES);
        loadingProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}