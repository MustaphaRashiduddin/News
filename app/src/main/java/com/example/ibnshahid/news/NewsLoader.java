package com.example.ibnshahid.news;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by ibnShahid on 12/04/2017.
 */

public class NewsLoader extends AsyncTaskLoader<List<NewsModel>> {

    private String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public List<NewsModel> loadInBackground() {
        if (mUrl == null) return null;
        URL url = QueryUtils.createUrl(mUrl);
        String jsonResponse = "";
        try {
            jsonResponse = QueryUtils.makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return QueryUtils.extractNews(jsonResponse);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
