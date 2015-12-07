package com.lala.hani.pocket.music;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/09/27.
 */
public class PlayService extends Service {


    private MediaPlayer mediaPlayer;

    private String path;

    private int currentItem;

    private int currentDuration;

    public static long totalDuration=0;

    private  PlayerServiceReceiver mPlayerServiceReceiver;
    private IntentFilter mIntentFilter;

    private boolean isPauseS;

    private static LinkedList<Music> mLinkedList=null;


    private int playType=MusicApp.PLAY_ORDER_LINE;
//    private PlayerReceiver playerReceiver;


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (mediaPlayer != null) {
                    currentDuration = mediaPlayer.getCurrentPosition(); // ��ȡ��ǰ���ֲ��ŵ�λ��
                    Intent intent = new Intent();
                    intent.setAction(MusicApp.MUSIC_CURRENT);
                    intent.putExtra("currentDuration", currentDuration);
                    sendBroadcast(intent); // ��PlayerActivity���͹㲥
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = new MediaPlayer();


        mLinkedList=MusicFmt.mLinkedList;


        mPlayerServiceReceiver=new PlayerServiceReceiver();
        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(MusicApp.MUSIC_TYPE_UPDATE);

        registerReceiver(mPlayerServiceReceiver,mIntentFilter);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if(playType==MusicApp.PLAY_ORDER_LINE)
                nextMusic();
                else if(playType==MusicApp.PLAY_ORDER_CIRCLE)
                {
                circleMusic();
                }
            }
        });



        // �����������¼�
        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); // ��ȡϵͳ����
        telManager.listen(new MobliePhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE);

    }


    public void circleMusic()
    {
        play(0);
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int msg = intent.getIntExtra("MSG", 0);

        currentItem = intent.getIntExtra("currentItem", -1);

        switch (msg) {
            case MusicApp.PLAY_MUSIC: {
                path =mLinkedList.get(currentItem).getSongUrl();
                if(path!=null)
                {
                    play(0);

                }
                else
                {
                    nextMusic();
                }
                break;

            }
            case MusicApp.PAUSE_MUSIC:
            {
                Log.w("PlayService", "next pause()");
                pause();
                break;
            }

            case MusicApp.COTINUNE_MUSIC:
            {
//               currentDuration=intent.getIntExtra("currentDuration",0);
                //play(currentDuration);
                Log.w("PlayService", "next continueMusic()");

                continueMusic();
                break;
            }
            case MusicApp.PROGRESS_CHAGE:
            {
                currentDuration = intent.getIntExtra("currentDuration",0);
                play(currentDuration);
                break;
            }
        }





        return super.onStartCommand(intent, flags, startId);

    }


    public void pause()
    {
        if(mediaPlayer!=null && mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            isPauseS=true;
        }
    }



    public void continueMusic()
    {
        if(isPauseS)
        {
            mediaPlayer.start();
            isPauseS=false;
        }
    }

    public void nextMusic()
    {
        currentItem++;
        if(currentItem>mLinkedList.size()-1)
        {
            currentItem=0;
        }

        path=mLinkedList.get(currentItem).getSongUrl();
        if(path!=null)
        {
            play(0);
            Intent i=new Intent(MusicApp.MUSIC_NEXT);
            i.putExtra("currentItem",currentItem);
            sendBroadcast(i);
        }
        else
        {
            nextMusic();

        }



    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        unregisterReceiver(mPlayerServiceReceiver);
        MusicPref.clearData(getApplicationContext());

    }


    public void play(int currentDuration) {

        try {

            mediaPlayer.reset();// �Ѹ�������ָ�����ʼ״̬
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare(); // ���л���
            mediaPlayer.setOnPreparedListener(new PreparedListener(currentDuration));
            handler.sendEmptyMessage(1);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }



   class PlayerServiceReceiver extends BroadcastReceiver
   {

       @Override
       public void onReceive(Context context, Intent intent) {

           playType=intent.getIntExtra("playType",1);
       }
   }



    /**
     *
     * @author wwj
     * �绰��������
     */
    private class MobliePhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: // �һ�״̬
                    continueMusic();
                    isPauseS = false;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:	//ͨ��״̬
                case TelephonyManager.CALL_STATE_RINGING:	//����״̬
                    pause();
                    isPauseS =true;
                    break;
                default:
                    break;
            }
        }
    }

    private final class PreparedListener implements MediaPlayer.OnPreparedListener {
        private int currentTime;

        public PreparedListener(int currentTime) {
            this.currentTime = currentTime;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start(); // ��ʼ����
            if (currentTime > 0) { // ������ֲ��Ǵ�ͷ����
                mediaPlayer.seekTo(currentTime);
            }
            Intent intent = new Intent();
            intent.setAction(MusicApp.MUSIC_DURATION);
            totalDuration = mediaPlayer.getDuration();
            int duration=(int )totalDuration;
            intent.putExtra("totalDuration", duration);    //ͨ��Intent�����ݸ������ܳ���
            sendBroadcast(intent);
        }
    }
}
