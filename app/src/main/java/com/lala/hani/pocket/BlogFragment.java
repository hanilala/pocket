package com.lala.hani.pocket;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lala.hani.pocket.widget.BackHandledFragment;
import com.lala.hani.pocket.widget.ProgressWebView;


/**
 * Created by Chenyc on 15/6/27.
 */
public class BlogFragment extends BackHandledFragment {

    private ProgressWebView mWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, null);
        mWebView = (ProgressWebView) view.findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://www.aswifter.com/");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public String getTagText() {
        return null;
    }

    @Override
    public boolean onBackmPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }

        return false;
    }
}