package com.lala.hani.pocket.music;

import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.lala.hani.pocket.R;

import java.io.File;
import java.util.LinkedList;

/**
 * Created by Administrator on 2015/09/26.
 */
public class MusicFmt extends Fragment {


    private ProgressDialog progressDialog;

    private static String[] imageFormatSet = new String[]{".mp3"};

    public static final String fileName = "musicList.txt";


    private PlayerReceiver playerReceiver;


    private int currentItem;

/*
    private List<String> audioList = new ArrayList<String>();
    private LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();*/


    private ListView listView;

    private SeekBar voiceSeekBar, musicSeekBar;
    private TextView currentProgress, finalProgress;
    private Button playMusic, preMusic, nextMusic, playQueue;
    private ImageButton ibtn_player_voice;

    private MusicAdapter adapter = null;


    private int currentDuration;

    private boolean isPlaying, isPause;

    private int playType=MusicApp.PLAY_ORDER_LINE;

    RelativeLayout ll_player_voice;    //音量控制面板布局

    // 音量面板显示和隐藏动画
    private Animation showVoicePanelAnimation;
    private Animation hiddenVoicePanelAnimation;

    private AudioManager am;        //音频管理引用，提供对音频的控制

    int currentVolume, maxVolume;

    public static LinkedList<Music> mLinkedList=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        mLinkedList=MusicApp.getMusicData(getActivity());

        adapter = new MusicAdapter(getActivity(),mLinkedList);


        //音量调节面板显示和隐藏的动画
        showVoicePanelAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_in);
        hiddenVoicePanelAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_out);


        playerReceiver = new PlayerReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicApp.MUSIC_CURRENT);
        intentFilter.addAction(MusicApp.MUSIC_DURATION);
        intentFilter.addAction(MusicApp.MUSIC_NEXT);
        getActivity().registerReceiver(playerReceiver, intentFilter);

        am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);


        Log.w("MusicFmt", "OnCreate()");

    }

    @Override
    public void onStop() {
        super.onStop();

        Log.w("MusicFmt", "onStop()");


        MusicPref.setMusicStatus(getActivity(), MusicApp.PLAY_STATUS, isPlaying);
        MusicPref.setMusicStatus(getActivity(), MusicApp.PAUSE_STATUS, isPause);

        MusicPref.setMusicItem(getActivity(), currentItem);
        MusicPref.save(getActivity(),MusicApp.MUSIC_PLAY_TYPE,playType);



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(playerReceiver);
        Log.w("MusicFmt", "onDestroy()");
    }

    public void findViewById(View view) {

        voiceSeekBar = (SeekBar) view.findViewById(R.id.sb_player_voice);
        voiceSeekBar.setProgress(currentVolume);
        voiceSeekBar.setMax(maxVolume);

        SeekBarChangeListener seekBarChangeListener = new SeekBarChangeListener();

        voiceSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        listView = (ListView) view.findViewById(R.id.listView);
        musicSeekBar = (SeekBar) view.findViewById(R.id.audioTrack);

        musicSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        currentProgress = (TextView) view.findViewById(R.id.current_progress);
        finalProgress = (TextView) view.findViewById(R.id.final_progress);
        playMusic = (Button) view.findViewById(R.id.play_music);
        nextMusic = (Button) view.findViewById(R.id.next_music);
        preMusic = (Button) view.findViewById(R.id.previous_music);
        playQueue = (Button) view.findViewById(R.id.play_type);
        ibtn_player_voice = (ImageButton) view.findViewById(R.id.ibtn_player_voice);
        ll_player_voice = (RelativeLayout) view.findViewById(R.id.ll_player_voice);


    }

    public void setViewOnclickListener() {
        viewOnclickListener viewOnclickListener = new viewOnclickListener();
        playMusic.setOnClickListener(viewOnclickListener);
        nextMusic.setOnClickListener(viewOnclickListener);
        preMusic.setOnClickListener(viewOnclickListener);
        ibtn_player_voice.setOnClickListener(viewOnclickListener);
        playQueue.setOnClickListener(viewOnclickListener);

    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_music, container, false);



        findViewById(rootView);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new MusicItemClickListener());

        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(50); // 长按振动
                final AdapterView.AdapterContextMenuInfo menuInfo2 = (AdapterView.AdapterContextMenuInfo) menuInfo;

                musicListItemDialog(menuInfo2.position); // 长按后弹出的对话框


            }
        });



        setViewOnclickListener();


        Log.w("MusicFmt", "onCreateView()");

        if(PlayService.totalDuration!=0)
        {
            musicSeekBar.setMax((int)PlayService.totalDuration);
            finalProgress.setText(MusicApp.formatTime(PlayService.totalDuration));


            isPlaying=MusicPref.isPlaying(getActivity());
            isPause=MusicPref.isPause(getActivity());

            currentItem=MusicPref.musicCurItem(getActivity());

            playType=MusicPref.getInt(getActivity(),MusicApp.MUSIC_PLAY_TYPE);

        }

        return rootView;


    }

    public void musicListItemDialog(final int whereIs)
    {
        String[] menuItems = new String[]{"删除音乐", "设为铃声", "查看详情"};
        ListView menuList = new ListView(getActivity());
        menuList.setCacheColorHint(Color.TRANSPARENT);
        menuList.setDividerHeight(1);
        menuList.setAdapter(new ArrayAdapter<String>(getActivity(),
                R.layout.context_dialog_layout, R.id.dialogText, menuItems));
        menuList.setLayoutParams(new ViewGroup.LayoutParams(ConstantUtil
                .getScreen(getActivity())[0] / 2, ViewGroup.LayoutParams.WRAP_CONTENT));

        final CustomDialog customDialog = new CustomDialog.Builder(
                getActivity()).setTitle(R.string.operation)
                .setView(menuList).create();
        customDialog.show();

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    final String musicName = mLinkedList.get(whereIs).getSongTitle();
                    final int songId = mLinkedList.get(whereIs).getId();
                    final String  path=mLinkedList.get(whereIs).getSongUrl();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("操作");
                    dialog.setMessage("确认删除" + musicName + "?");
                    dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            File file = new File(path);
                            if (file.exists()) {
                                file.delete();
                                MusicApp.deleteSong(getActivity(), songId);
                                mLinkedList.remove(whereIs);
                                Intent i=new Intent(Intent.ACTION_MEDIA_SCANNER_STARTED);
                                getActivity().sendBroadcast(i);
                                adapter.notifyDataSetChanged();

                            }


                            customDialog.dismiss();

                        }
                    });

                    dialog.setNegativeButton("取消", null);
                    dialog.setCancelable(true);
                    dialog.show();
                }
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        String si = finalProgress.getText().toString();
        outState.putString("finalTime", si);
        Log.w("MusicFmt", "onSaveInstanceState()");
    }




    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
//        String si=savedInstanceState.getString("finalTime");
//        finalProgress.setText(si);
        Log.w("MusicFmt", "onViewStateRestored is " );


    }

    public void playMusic(int position) {

        Intent playIntent = new Intent(getActivity(),PlayService.class);
        playIntent.putExtra("currentItem", position);
        playIntent.putExtra("MSG", MusicApp.PLAY_MUSIC);
        getActivity().startService(playIntent);
        isPlaying = true;
        isPause = false;

    }


    class MusicItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Log.w("MusicFmt","position is :"+position);

            playMusic.setBackgroundResource(R.mipmap.play);
            currentItem = position;
            playMusic(currentItem);


        }
    }


    class viewOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {


            switch (v.getId()) {
                case R.id.play_music: {

                    if (isPlaying) {
                        Intent intent = new Intent(getActivity(),PlayService.class);

                        intent.putExtra("MSG", MusicApp.PAUSE_MUSIC);
                        getActivity().startService(intent);

                        isPlaying = false;
                        isPause = true;

                        playMusic.setBackgroundResource(R.mipmap.pause);


                        Log.w("MusicFmt", "after judge  isPlaying is:" + isPlaying);
                        Log.w("MusicFmt", "after judge  isPause is:" + isPause);


                    } else if (isPause) {
                        Intent intentP = new Intent(getActivity(),PlayService.class);

                        intentP.putExtra("MSG", MusicApp.COTINUNE_MUSIC);
                        getActivity().startService(intentP);
//                        getActivity().sendBroadcast(intentP);

                        isPlaying = true;
                        isPause = false;

                        playMusic.setBackgroundResource(R.mipmap.play);


                        Log.w("MusicFmt", "after judge  isPlaying is:" + isPlaying);
                        Log.w("MusicFmt", "after judge  isPause is:" + isPause);
                    }
                    break;

                }
                case R.id.previous_music: {
                    currentItem--;
                    if (currentItem < 0) {
                        currentItem =mLinkedList.size() - 1;
                    }

                    Toast.makeText(getActivity(),
                            mLinkedList.get(currentItem).getSongTitle(), Toast.LENGTH_SHORT).show();

                    Intent preInt = new Intent(getActivity(),PlayService.class);
                    preInt.putExtra("currentItem", currentItem);
                    preInt.putExtra("MSG", MusicApp.PLAY_MUSIC);
                    getActivity().startService(preInt);
                    break;
                }
                case R.id.next_music: {
                    currentItem++;
                    if (currentItem >mLinkedList.size() - 1) {
                        currentItem = 0;
                        Toast.makeText(getActivity(),
                                mLinkedList.get(currentItem).getSongTitle(), Toast.LENGTH_SHORT).show();

                    }
                    Toast.makeText(getActivity(),
                            mLinkedList.get(currentItem).getSongTitle(), Toast.LENGTH_SHORT).show();

                    Intent nexInt = new Intent(MusicApp.launchService);
                    nexInt.putExtra("currentItem", currentItem);
                    nexInt.putExtra("MSG", MusicApp.PLAY_MUSIC);
                    getActivity().startService(nexInt);
                    break;
                }

                case R.id.ibtn_player_voice: {
                    Log.w("MusicFmt", "next voicePanelAnimation()");
                    voicePanelAnimation();
                    break;
                }
                case R.id.play_type:
                {
                    if(playType==MusicApp.PLAY_ORDER_LINE)
                    {
                        playQueue.setBackgroundResource(R.mipmap.repeat_current);
                        playType=MusicApp.PLAY_ORDER_CIRCLE;
                        Toast.makeText(getActivity(),"单曲循环",500).show();


                    }
                    else {
                        playQueue.setBackgroundResource(R.mipmap.repeat_all);

                        playType=MusicApp.PLAY_ORDER_LINE;
                        Toast.makeText(getActivity(),"顺序播放",500).show();

                    }
                    Intent i=new Intent(MusicApp.MUSIC_TYPE_UPDATE);
                    i.putExtra("playType",playType);
                    getActivity().sendBroadcast(i);
                    break;
                }


            }


        }
    }


    //控制显示音量控制面板的动画
    public void voicePanelAnimation() {
        if (ll_player_voice.getVisibility() == View.GONE) {
            ll_player_voice.startAnimation(showVoicePanelAnimation);
            ll_player_voice.setVisibility(View.VISIBLE);
        } else {
            ll_player_voice.startAnimation(hiddenVoicePanelAnimation);
            ll_player_voice.setVisibility(View.GONE);
        }
    }

    public void audioTrackChange(int progress) {
        Intent i = new Intent(getActivity(),PlayService.class);
        i.putExtra("MSG", MusicApp.PROGRESS_CHAGE);
        i.putExtra("currentItem",currentItem);
        i.putExtra("currentDuration", progress);
        getActivity().startService(i);
    }


    class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            switch (seekBar.getId()) {
                case R.id.audioTrack:
                    if (fromUser) {
                        currentDuration = progress;
                        audioTrackChange(progress); // 用户控制进度的改变
                    }
                    break;
                case R.id.sb_player_voice:
                    // 设置音量
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);


                    break;
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }



    class PlayerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(MusicApp.MUSIC_DURATION)) {
                int duration = intent.getIntExtra("totalDuration", -1);
                musicSeekBar.setMax(duration);
                finalProgress.setText(MusicApp.formatTime(duration));
            } else if (action.equals(MusicApp.MUSIC_CURRENT)) {
                currentDuration = intent.getIntExtra("currentDuration", -1);
                currentProgress.setText(MusicApp.formatTime(currentDuration));
                musicSeekBar.setProgress(currentDuration);
            }
            else if(action.equals(MusicApp.MUSIC_NEXT))
            {
                currentItem=intent.getIntExtra("currentItem",-1);

            }


        }
    }


}
