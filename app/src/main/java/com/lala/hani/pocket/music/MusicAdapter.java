package com.lala.hani.pocket.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lala.hani.pocket.R;

import java.util.LinkedList;

/**
 * Created by hani on 15-12-5.
 */
public class MusicAdapter extends BaseAdapter {

    private Context mContext;
    private LinkedList<Music>mLinkedList;

    public MusicAdapter(Context context,LinkedList<Music> list)
    {
        mContext=context;
        mLinkedList=list;
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

        View view;
        MusicViewHolder musicViewHolder;

        Music music=mLinkedList.get(position);

        if(convertView==null)
        {
            view= LayoutInflater.from(mContext).inflate(R.layout.fragment_music_item
                    ,parent,false);
            musicViewHolder=new MusicViewHolder();

            musicViewHolder.songTitle= (TextView) view.findViewById(R.id.songTitle);
            musicViewHolder.songArtist= (TextView) view.findViewById(R.id.songArtist);
            musicViewHolder.songDuration= (TextView) view.findViewById(R.id.songDuration);

            view.setTag(musicViewHolder);

        }
        else
        {
            view=convertView;
            musicViewHolder= (MusicViewHolder) view.getTag();
        }

        musicViewHolder.songTitle.setText(music.getSongTitle());
        musicViewHolder.songArtist.setText(music.getSongArtist());
        musicViewHolder.songDuration.setText(MusicApp.formatTime(music.getSongDuration()));


        return view;
    }


    class MusicViewHolder
    {
        TextView songTitle,songArtist,songDuration;

    }
}
