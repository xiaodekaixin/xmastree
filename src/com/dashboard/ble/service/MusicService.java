package com.dashboard.ble.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by West on 2015/11/10.
 */
public class MusicService extends Service {

    private String[] musicDir = new String[]{
    		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/demo.mp3",
    		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/demo.mp3",
    		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/demo.mp3",
    		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/demo.mp3",
    };
    private int musicIndex = 1;
    
    private String mMusicDirPath = "";
    
    public MediaPlayer mp = new MediaPlayer();
//    public MusicService() {
//        try {
//        	String state = Environment.getExternalStorageState();
//        	if(state.equals(Environment.MEDIA_MOUNTED)) {
//        		mMusicDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
//        	}
//            mp.setDataSource(mMusicDirPath+"/demo.mp3");
//            mp.prepare();
//            musicIndex = 1;
//        } catch (Exception e) {
//            Log.d("hint","can't get to the song");
//            e.printStackTrace();
//        }
//    }
    
    @Override
    public void onCreate() {
    	super.onCreate();
    	try {
        	String state = Environment.getExternalStorageState();
        	if(state.equals(Environment.MEDIA_MOUNTED)) {
        		mMusicDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
        	}
            mp.setDataSource(mMusicDirPath+"/demo.mp3");
            mp.prepare();
            musicIndex = 1;
        } catch (Exception e) {
            Log.d("hint","can't get to the song");
            e.printStackTrace();
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	return super.onStartCommand(intent, flags, startId);
    }
    
    public void playOrPause() {
        if(mp.isPlaying()){
            mp.pause();
        } else {
            mp.start();
        }
    }
    public void stop() {
        if(mp != null) {
            mp.stop();
            try {
                mp.prepare();
                mp.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void nextMusic() {
        if(mp != null && musicIndex < 3) {
            mp.stop();
            try {
                mp.reset();
                mp.setDataSource(musicDir[musicIndex+1]);
                musicIndex++;
                mp.prepare();
                mp.seekTo(0);
                mp.start();
            } catch (Exception e) {
                Log.d("hint", "can't jump next music");
                e.printStackTrace();
            }
        }
    }
    public void preMusic() {
        if(mp != null && musicIndex > 0) {
            mp.stop();
            try {
                mp.reset();
                mp.setDataSource(musicDir[musicIndex-1]);
                musicIndex--;
                mp.prepare();
                mp.seekTo(0);
                mp.start();
            } catch (Exception e) {
                Log.d("hint", "can't jump pre music");
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        super.onDestroy();
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
}
