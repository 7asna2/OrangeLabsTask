package com.example.hasnaa.orangelabstask;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
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
    ImageButton searchButton ;
    EditText editText;
    TextView emptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        editText=(EditText)findViewById(R.id.search_word);
        emptyView = (TextView)findViewById(R.id.empty_view);
        searchButton=(ImageButton)findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    makeSearchQuery(editText.getText().toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                getSupportLoaderManager().initLoader(IMAGE_SEARCH_LOADER, null,MainActivity.this);


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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeSearchQuery(String tag) throws MalformedURLException {
        final String url = " https://api.flickr.com/services/rest/?method=flickr.photos.search&format=json&nojsoncallback=1&api_sig=56fd8d8bcee344e28c01b4279df0cf23&api_key=b16406e6933dcb70b73a9e933a094c01&tags=";
        tag ="cat" ;
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

//                mLoadingIndicator.setVisibility(View.VISIBLE);
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
