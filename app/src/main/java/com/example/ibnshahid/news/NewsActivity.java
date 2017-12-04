package com.example.ibnshahid.news;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsModel>> {

    NewsAdapter adapter = null;
    ListView newsListView = null;
    ProgressBar pb = null;
    TextView empty = null;
    private List<NewsModel> data;

    int page;
    boolean isLoading = false;
    private String request = null;
    private int pageSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        page = 1;
        newsListView = (ListView) findViewById(R.id.ll_list);
        pb = (ProgressBar) findViewById(R.id.pb_wait);
        Bundle bundle = new Bundle();
        empty = (TextView) findViewById(R.id.tv_empty);
        empty.setText("Search for news");
        if (getLoaderManager().getLoader(1) != null)
            initLoaderWrapper(1, bundle);
        newsListView.setEmptyView(empty);
        newsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) { }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0 && !isLoading && firstVisibleItem + visibleItemCount == pageSize * page) {
                    isLoading = true;
                    String sUrl = request + "&page=" + page+1;
                    Bundle bundle = new Bundle();
                    bundle.putString("request", sUrl);
                    if (restartLoaderWrapper(2, bundle) == null && scrollMsg) {
                        Toast.makeText(getApplicationContext(), "Can't load more data; check your internet connection", Toast.LENGTH_SHORT).show();
                        scrollMsg = false;
                    }
                } else {
                    scrollMsg = true;
                }
            }
        });
    } boolean scrollMsg = true;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
        outState.putInt("page", page);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        page = savedInstanceState.getInt("page");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                request = query + "&page-size="+pageSize;
                Bundle bundle = new Bundle();
                bundle.putString("request", request);
                if (getLoaderManager().getLoader(1) == null)
                    initLoaderWrapper(1, bundle);
                else
                    restartLoaderWrapper(1, bundle);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { return false; }
        });
        return true;
    }

    String getUrl (String request) {
        String url = Constants.beg + request + Constants.cap + Constants.key;
        return url;
    }

    Loader<List<NewsModel>> initLoaderWrapper(int id, Bundle bundle) {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            pb.setVisibility(View.GONE);
            empty.setText("No internet connection");
            if (adapter != null && adapter.getCount() != 0)
                Toast.makeText(getApplicationContext(), "Check your connection", Toast.LENGTH_SHORT).show();
            return null;
        }
        return getLoaderManager().initLoader(id, bundle, NewsActivity.this);
    }

    Loader<List<NewsModel>> restartLoaderWrapper(int id, Bundle bundle) {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            isLoading = false;
            pb.setVisibility(View.GONE);
            empty.setText("No internet connection");
            if (adapter != null && adapter.getCount() != 0 && id != 2)
                Toast.makeText(getApplicationContext(), "Check your connection", Toast.LENGTH_SHORT).show();
            return null;
        }
        return getLoaderManager().restartLoader(id, bundle, NewsActivity.this);
    }

    @Override
    public Loader<List<NewsModel>> onCreateLoader(int id, Bundle args) {
        if (id == 2) isLoading = true;
        pb.setVisibility(View.VISIBLE);
        String url = getUrl(args.getString("request"));
        Log.e("url", url);
        return new NewsLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsModel>> loader, final List<NewsModel> data) {
        //TODO
        switch (loader.getId()) {
            case 1:
                this.data = data;
                adapter = new NewsAdapter(this, 0, this.data);
                newsListView.setAdapter(adapter);
                newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String url = data.get(position).getUrl();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                });
                empty.setText("No news");
                page = 1;
                break;
            case 2:
                isLoading = false;
                this.data.addAll(data);
                adapter.notifyDataSetChanged();
                page++;
                break;
        }
        pb.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<NewsModel>> loader) {

    }
}