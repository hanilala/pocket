package com.lala.hani.pocket.music;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.LinkedList;

/**
 * Created by Administrator on 2015/09/27.
 */
public class MusicApp {

    public static final int PLAY_MUSIC=1;
    public static final int PAUSE_MUSIC=2;
    public static final int COTINUNE_MUSIC=3;
    public static final int PROGRESS_CHAGE=4;

    public static final String launchService="com.lan.media.MUSIC_SERVICE";


    public static final String MUSIC_DURATION = "com.lan.action.MUSIC_DURATION";//新音乐长度更新动作

    public static final String MUSIC_CURRENT = "com.lan.action.MUSIC_CURRENT";	//当前音乐播放时间更新动作
    public static final String MUSIC_NEXT = "com.lan.action.MUSIC_NEXT";	//当前音乐播放时间更新动作


    public static String PLAY_STATUS="PLAYING";
    public static String PAUSE_STATUS="PAUSE";

    public static String MUSIC_CURRITEM="MUSIC_ITEM";

    public static String MUSIC_TYPE_UPDATE="com.lan.action.MusicTypeUpdate";

    public static String MUSIC_PLAY_TYPE="playType";

    public static int PLAY_ORDER_LINE=1;
    public static int PLAY_ORDER_CIRCLE=2;

    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    public  static LinkedList<Music> getMusicData(final Context context)
    {
       final LinkedList<Music> mLinkedListMusic=new LinkedList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Cursor cursor=context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        ,null,null,null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

                for(int i=0;i<cursor.getCount();i++)
                {
                    Music music=new Music();
                    cursor.moveToNext();

                    String title=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String albun=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String url=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String artist=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    int duration=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    int size=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                    int isMusic=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
                    int isRingTone=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_RINGTONE));
                    int isPodCast=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_PODCAST));
                    int isAlerm=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_ALARM));
                    int isNoti=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_NOTIFICATION));

                    if(isMusic!=0 && isAlerm==0 && isPodCast==0 &&
                            isRingTone==0 && isNoti==0)
                    {
                        music.setSongAlbum(albun);
                        music.setSongTitle(title);
                        music.setSongArtist(artist);
                        music.setSongSize(size);
                        music.setSongUrl(url);
                        music.setSongDuration(duration);
                    }
                    mLinkedListMusic.add(music);

                }
                cursor.close();


            }
        }).start();

        return mLinkedListMusic;


    }

    public static void deleteSong(Context context,int id)
    {
        String selection =MediaStore.Audio.Media._ID + "=?";
        context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                selection,new String[]{Integer.toString(id)});
        Log.w("MusicFmt","delete successful");
    }

}