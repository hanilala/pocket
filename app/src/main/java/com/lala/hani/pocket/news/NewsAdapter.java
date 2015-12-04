package com.lala.hani.pocket.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lala.hani.pocket.R;

import java.util.LinkedList;

/**
 * Created by hani on 15-11-28.
 */
public class NewsAdapter extends BaseAdapter {

    private LinkedList<NewsSummary.News> mLinkedList;

    private Context mContext;
    public NewsAdapter(LinkedList<NewsSummary.News> linkedList, Context context)
    {
        mLinkedList=linkedList;
        mContext=context;
    }

    public void updateDataList(LinkedList<NewsSummary.News> list)
    {
        mLinkedList=list;
        notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        return mLinkedList.size();
    }

    @Override
    public Object getItem(int position) {
        return mLinkedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=null;
        NewsViewHolder newsViewHolder=null;

        NewsSummary.News news=mLinkedList.get(position);

        if(convertView==null)
        {
            view= LayoutInflater.from(mContext)
                    .inflate(R.layout.fragment_news_item, parent, false);

            newsViewHolder=new NewsViewHolder();

            newsViewHolder.newsTitle= (TextView) view.findViewById(R.id.newsTitle);
            newsViewHolder.newsSource= (TextView) view.findViewById(R.id.newsSource);
            newsViewHolder.newsImg= (ImageView) view.findViewById(R.id.newsImg);

            view.setTag(newsViewHolder);
        }
        else {
            view=convertView;
            newsViewHolder= (NewsViewHolder) view.getTag();

        }

        newsViewHolder.newsTitle.setText(news.getTitle());
        newsViewHolder.newsSource.setText(news.getSource());

        Glide.with(mContext).load(news.getFirstImg()).centerCrop().
                into(newsViewHolder.newsImg);

        return view;
    }

    class NewsViewHolder
    {
        private TextView newsTitle;
        private TextView newsSource;
        private ImageView newsImg;
    }

}