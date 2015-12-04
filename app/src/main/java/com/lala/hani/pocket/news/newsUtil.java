package com.lala.hani.pocket.news;

import android.content.Intent;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.lala.hani.pocket.MyUtils.MyApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import cz.msebera.android.httpclient.Header;

/**
 * Created by hani on 15-11-28.
 */
public class newsUtil {

    public static String url="http://v.juhe.cn/weixin/query?" +
            "key=208732f25c29202763f22769935da354";

    public static  RequestQueue mRequestQueue;
    public static  NewsSummary mNewsSummary=null;


    static {
        mRequestQueue= Volley.newRequestQueue(MyApplication.getContext());
    }


    public static void getData(final int curPage, final int type)
    {
        RequestParams requestParams=new RequestParams();
        requestParams.put("pno",curPage);
        requestParams.put("ps",20);


        AsyncHttpClient client = new AsyncHttpClient();

        client.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response=new String(responseBody);

                Gson gson=new Gson();

                 mNewsSummary=gson.fromJson(response,NewsSummary.class);

                if(mNewsSummary.getReason().equals("success"))
                {
                    if(  curPage==1 && type==1)
                    {
                        Intent i =new Intent();
                        i.setAction(newsApp.Action_getNews);
                        i.putExtra("NewsSummary",mNewsSummary);
                        MyApplication.getContext().sendBroadcast(i);
                    }
                    else if(curPage==1 && type==2)
                    {
                        Intent i =new Intent();
                        i.setAction(newsApp.Action_RefreshNews);
                        i.putExtra("NewsSummary",mNewsSummary);
                        MyApplication.getContext().sendBroadcast(i);
                    }
                    else if (type==3) {
                        Intent i =new Intent();
                        i.setAction(newsApp.Action_getMoreNews);
                        i.putExtra("NewsSummary",mNewsSummary);
                        MyApplication.getContext().sendBroadcast(i);
                    }
                }
                else
                {
                    Log.w("haha","the reason is "+mNewsSummary.getReason());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


   /* public static void getData(final int curPage)
    {

        Map<String ,Object>params=new HashMap<>();
        params.put("pno",curPage);
        params.put("ps",15);

        JSONObject jsonObject=new JSONObject(params);

        Response.Listener<JSONObject> mListener=new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson=new Gson();

                mNewsSummary=gson.fromJson(response.toString(),NewsSummary.class);
           *//*     Log.w("haha","in newsUtil mNewsSummary "+mNewsSummary.toString());
                Log.w("haha","in newsUtil mNewsSummary reason "+mNewsSummary.getReason());*//*

                if(mNewsSummary.getReason().equals("success"))
                {
                    if(mNewsSummary!=null && curPage==1)
                    {
                        Intent i =new Intent();
                        i.setAction(newsApp.Action_getNews);
                        i.putExtra("NewsSummary",mNewsSummary);
                        MyApplication.getContext().sendBroadcast(i);
                    }
                    else if(mNewsSummary!=null) {
                        Intent i =new Intent();
                        i.setAction(newsApp.Action_getMoreNews);
                        i.putExtra("NewsSummary",mNewsSummary);
                        MyApplication.getContext().sendBroadcast(i);
                    }
                }
                else
                {
                    Log.w("haha","the reason is "+mNewsSummary.getReason());
                }

            }
        };

        Response.ErrorListener mErrorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };

        JsonObjectRequest mJsonObjectRequest=new JsonObjectRequest(Request.Method.POST,
                url,jsonObject,mListener,mErrorListener)
        {
           *//* @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
//                map.put("pno",Integer.toString(curPage));
                map.put("pno",curPage+"");
                map.put("dtype","json");

                return map;
            }*//*
        };

        mRequestQueue.add(mJsonObjectRequest);

    }*/

}