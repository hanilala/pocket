package com.lala.hani.pocket.music;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hani on 15-12-6.
 */
public class MusicPref  {


    public static Boolean isPlaying( Context context)
    {
         SharedPreferences sharedPreferences=
                 context.getSharedPreferences("music.pref", Context.MODE_PRIVATE);
         Boolean status=sharedPreferences.getBoolean("PLAYING",false);
        return  status;
    }

    public static Boolean isPause(Context context)
    {
        SharedPreferences sharedPreferences=
                context.getSharedPreferences("music.pref", Context.MODE_PRIVATE);
        Boolean status=sharedPreferences.getBoolean("PAUSE",false);
        return  status;
    }



    public static int musicCurItem(Context context)
    {
        SharedPreferences sharedPreferences=
                context.getSharedPreferences("music.pref", Context.MODE_PRIVATE);
        int item=sharedPreferences.getInt("MUSIC_ITEM", -1);


        return  item;
    }

    public static void setMusicStatus(Context context,String key,Boolean value)
    {
        SharedPreferences sharedPreferences=
                context.getSharedPreferences("music.pref", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putBoolean(key,value);

        editor.commit();

    }
    public static void setMusicItem(Context context,int currentItem)
    {
        SharedPreferences sharedPreferences=
                context.getSharedPreferences("music.pref", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putInt("MUSIC_ITEM", currentItem);

        editor.commit();
    }

    public static void save(Context context,String key,int value)
    {
        SharedPreferences sharedPreferences=
                context.getSharedPreferences("music.pref", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putInt(key,value);
        editor.commit();
    }

    public static int getInt(Context context,String key)
    {
        SharedPreferences sharedPreferences=
                context.getSharedPreferences("music.pref", Context.MODE_PRIVATE);

        return sharedPreferences.getInt(MusicApp.MUSIC_PLAY_TYPE,1);
    }

    public static void clearData(Context context)
    {
        SharedPreferences sharedPreferences=
                context.getSharedPreferences("music.pref", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.clear();

        editor.commit();
    }

}
