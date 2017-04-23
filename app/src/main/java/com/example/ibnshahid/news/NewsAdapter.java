package com.example.ibnshahid.news;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ibnShahid on 12/04/2017.
 */

public class NewsAdapter extends ArrayAdapter<NewsModel> {

    List<NewsModel> data;
    public NewsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<NewsModel> data) {
        super(context, resource, data);
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }

        TextView tv_section = (TextView) listItemView.findViewById(R.id.tv_section);
        tv_section.setText(data.get(position).getSection());

        TextView tv_title = (TextView) listItemView.findViewById(R.id.tv_title);
        tv_title.setText(data.get(position).getTitle());

        String dateTime = data.get(position).getDate();
        String date = dateTime.substring(0, 10);
        String time = dateTime.substring(11, 19);

        TextView tv_date = (TextView) listItemView.findViewById(R.id.tv_date);
        tv_date.setText(date + ", " + time);
        return listItemView;
    }
}
