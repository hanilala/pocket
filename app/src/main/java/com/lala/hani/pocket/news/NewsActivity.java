package com.lala.hani.pocket.news;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.lala.hani.pocket.R;
import com.lala.hani.pocket.widget.ProgressWebView;


/**
 * Created by hani on 15-11-28.
 */
public class NewsActivity extends AppCompatActivity {

    private ProgressWebView mProgressWebView;
    private Toolbar mToolbar;

    private NewsSummary.News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        news= (NewsSummary.News) getIntent().getSerializableExtra("news");
        initView();

        mProgressWebView.getSettings().setJavaScriptEnabled(true);
        mProgressWebView.loadUrl(news.getUrl());
    }

    /**
     *
     */

    private void initView() {
        mProgressWebView= (ProgressWebView) findViewById(R.id.progressWebView);
        mToolbar= (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(news.getTitle());

//        mToolbar.setTitle(news.getTitle());
        mToolbar.setBackgroundColor(getResources().getColor(R.color.primary));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onBackPressed();
                Log.w("haha", "setNavigationOnClickListener ");
                finish();
            }
        });

    }
}