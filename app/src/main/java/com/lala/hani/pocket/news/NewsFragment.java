package com.lala.hani.pocket.news;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.lala.hani.pocket.R;
import com.lala.hani.pocket.xListView.XListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by hani on 15-11-28.
 */
public class NewsFragment extends Fragment implements XListView.IXListViewListener{

    public int mLastPage =1;
    public int mTotalPageNum;
    private XListView mXListView;
    private ProgressBar mProgressBar;
    private NewsAdapter mNewsAdapter;

    private NewsReceiver mNewsReceiver;

    private IntentFilter mIntentFilter;

    private LinkedList<NewsSummary.News> mLinkedList;

    private AdapterView.OnItemClickListener mOnItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.w("haha","position is: "+position);
            NewsSummary.News news=mLinkedList.get(position-1);

            Intent i=new Intent(getActivity(),NewsActivity.class);
            i.putExtra("news",news);
            getActivity().startActivity(i);

        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNewsReceiver=new NewsReceiver();
        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(newsApp.Action_getNews);
        mIntentFilter.addAction(newsApp.Action_getMoreNews);
        mIntentFilter.addAction(newsApp.Action_RefreshNews);
        getActivity().registerReceiver(mNewsReceiver, mIntentFilter);

        newsUtil.getData(mLastPage,newsApp.Type_getData);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_news,container,false);
        initView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mNewsReceiver);
    }

    private void initView(View view) {
        mXListView= (XListView) view.findViewById(R.id.xListView);



        mProgressBar= (ProgressBar) view.findViewById(R.id.progressBar_xListV);


    }

    public void initAdapter()
    {
        mNewsAdapter=new NewsAdapter(mLinkedList,getActivity());
        mXListView.setAdapter(mNewsAdapter);

        mXListView.setOnItemClickListener(mOnItemClickListener);

        mXListView.setPullLoadEnable(true);

        //下拉刷新存在重复问题.
        mXListView.setPullRefreshEnable(false);
        mXListView.setXListViewListener(this);

        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {

        Log.w("haha", "下拉刷新");

//        newsUtil.getData(1,newsApp.Type_refreshData);

    }

    public void onStopLoadMore()
    {
        mXListView.stopLoadMore();
    }

    public void onStopRefresh()
    {


        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");

        String timesS=simpleDateFormat.format(new Date());

        mXListView.setRefreshTime(timesS);
        mXListView.stopLoadMore();
        mXListView.stopRefresh();
    }

    @Override
    public void onLoadMore() {

        Log.w("haha", "上拉加载");
        mLastPage++;
        Log.w("haha", "上拉加载 mCurPage is: "+ mLastPage);
        if(mLastPage >mTotalPageNum)
            mLastPage =1;

        newsUtil.getData(mLastPage,newsApp.Type_getMoreData);

    }



    class NewsReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action=intent.getAction();
            NewsSummary newsSummary= (NewsSummary) intent.getSerializableExtra("NewsSummary");

//            int whichPage=newsSummary.getResult().getPno();

            mTotalPageNum=newsSummary.getResult().getTotalPage();

            if(action.equals(newsApp.Action_getNews))
            {
                mLinkedList=newsSummary.getResult().getList();
                if(mLinkedList!=null)
                    initAdapter();
            }
            else if(action.equals(newsApp.Action_getMoreNews))
            {
                LinkedList<NewsSummary.News>linkedListOne=newsSummary.getResult().getList();

                Log.w("haha", "the new Size is: " + linkedListOne.size());
                if(!mLinkedList.containsAll(linkedListOne))
                {
                    mLinkedList.addAll(mLinkedList.size(),linkedListOne);
                    mNewsAdapter.updateDataList(mLinkedList);
                }

                onStopLoadMore();
            }
            else
            {
                Log.w("haha","action in Refresh "+action);
                LinkedList<NewsSummary.News>linkedListTwo=newsSummary.getResult().getList();

                LinkedList<NewsSummary.News>testLink=new LinkedList<>();
                Log.w("haha", "the Refresh Size is: " + linkedListTwo.size());

                   for(NewsSummary.News news:linkedListTwo)
                   {
                       if(!mLinkedList.contains(news))
                       {
                           testLink.add(news);

                       }
                   }
                mLinkedList.addAll(0,testLink);
                mNewsAdapter.updateDataList(mLinkedList);


                onStopRefresh();
            }

            Log.w("haha", "mLastPage is: "+ mLastPage);
            Log.w("haha", "mTotalPageNum is: " + mTotalPageNum);
            Log.w("haha", "mLinkedList size is is: " + mLinkedList.size());


        }
    }


}