package com.example.ibnshahid.news;

/**
 * Created by ibnShahid on 12/04/2017.
 */

public class NewsModel {
    private String mTitle;
    private String mSection;
    private String mDate;
    private String mUrl;
    NewsModel(String title, String section, String date, String url) {
        mTitle = title;
        mSection = section;
        mDate = date;
        mUrl = url;
    }
    public String getTitle() { return mTitle; }
    public String getSection() { return mSection; }
    public String getDate() { return mDate; }
    public String getUrl() { return mUrl; }
}
