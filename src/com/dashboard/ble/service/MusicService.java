package com.dashboard.ble.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by West on 2015/11/10.
 */
public class MusicService extends Service {

//    private String[] musicDir = new String[]{
//    		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/demo.mp3",
//    		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/demo.mp3",
//    		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/demo.mp3",
//    		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/demo.mp3",
//    };

    private String mMusicFilePath = "";
    public MediaPlayer mPlayer = new MediaPlayer();
    private OnPlayMusicListener onPlayMusicListener;

    @Override
    public void onCreate() {
        super.onCreate();
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            mMusicFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/demo.mp3";
        }
        mPlayer.reset();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // 通知音乐播放器播放完毕
                if(onPlayMusicListener != null) {
                    onPlayMusicListener.onCompletion();
                }
            }
        });
    }

    public void setMusicFilePath(String musicFilePath) {
        this.mMusicFilePath = musicFilePath;
    }

    public void stopMusic() {
        try {
            mPlayer.stop();
            mPlayer.prepare();
            mPlayer.seekTo(0);
        } catch (Exception e) {
            Log.d("hint","stop music,fail:" + e.getMessage());
            e.printStackTrace();
        }
        Log.d("hint","stop music");
    }

    public void pauseMusic() {
        mPlayer.pause();
    }

    public void playMusic() {
        int duration = mPlayer.getDuration();
        int currentPosition = mPlayer.getCurrentPosition();
        Log.d("hint", "playing music,currentPos=" + currentPosition + ",duration=" + duration);
        if (currentPosition > 0 && currentPosition < duration) {
            if (!mPlayer.isPlaying()) {
                mPlayer.start();
            }
        } else {
            startPlayMusic();
        }
    }

    public void startPlayMusic() {
        try {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.reset();
            mPlayer.setDataSource(mMusicFilePath);
            mPlayer.prepare();
            mPlayer.seekTo(0);
            mPlayer.start();
        } catch (IOException e) {
            Log.d("hint", "play music,fail:" + e.getMessage());
            e.printStackTrace();
        }
        Log.d("hint", "play music,path:" + mMusicFilePath);
    }

    /**
     * onBind 是 Service 的虚方法，因此我们不得不实现它。
     * 返回 null，表示客服端不能建立到此服务的连接。
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    public final IBinder binder = new MyBinder();
    public class MyBinder extends Binder{
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onDestroy() {
        mPlayer.stop();
        mPlayer.release();
        super.onDestroy();
    }

    public static interface OnPlayMusicListener {
        public void onCompletion();
    }

    public void setOnPlayMusicListener(OnPlayMusicListener listener) {
        this.onPlayMusicListener = listener;
    }
}
