package com.example.hasnaa.orangelabstask;


import android.app.IntentService;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
//import android.widget.SearchView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>>{

    private final static int IMAGE_SEARCH_LOADER = 10;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    RecyclerView recyclerView;

    TextView emptyView;

    SearchView searchView;
    ProgressBar loadingIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView = (SearchView)findViewById(R.id.search1);
        emptyView=(TextView)findViewById(R.id.empty_view);
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    makeSearchQuery(query.trim());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return false;
                }
                getSupportLoaderManager().initLoader(IMAGE_SEARCH_LOADER, null,MainActivity.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        recyclerView= (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeSearchQuery(String tag) throws MalformedURLException {
        final String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=94b5b8ed24d00fd1af9f886b0a1f44cc&format=json&nojsoncallback=1&tags=";
        //tag =searchWord ;
        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, url+tag);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> searchLoader = loaderManager.getLoader(IMAGE_SEARCH_LOADER);
        if (searchLoader == null) {
            loaderManager.initLoader(IMAGE_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(IMAGE_SEARCH_LOADER, queryBundle, this);
        }
    }
    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<ArrayList<String>>(this) {
            @Override
            protected void onStartLoading() {

                if (args == null) {
                    return;
                }

                loadingIndicator.setVisibility(View.VISIBLE);
//
                forceLoad();
            }

            @Override
            public ArrayList<String> loadInBackground() {
                ArrayList<String>imgUrls=new ArrayList<>();
                String searchQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);
                if (searchQueryUrlString == null) {
                    return null;
                }

                try {
                    URL url = new URL(searchQueryUrlString);
                    String searchResults = getResponseFromHttpUrl(url);
                    imgUrls=Utiles.parseJsonString(searchResults);

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }

                return imgUrls;
            }
        };
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        loadingIndicator.setVisibility(View.INVISIBLE);
        if (data == null) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            RecycleViewAdapter adapter = new RecycleViewAdapter(this, data);
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {

    }
}
