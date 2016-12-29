package com.dashboard.ble.activity;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dashboard.ble.R;
import com.dashboard.ble.service.MusicService;

public class MainActivity extends Activity implements View.OnClickListener {

    private MusicService musicService;
    private SeekBar seekBar;
    private TextView musicStatus, musicTime;
    private Button btnPlayOrPause, btnStop, btnQuit;
    private SimpleDateFormat time = new SimpleDateFormat("m:ss");
    
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicService = ((MusicService.MyBinder)iBinder).getService();
            Log.d("hint", "service is connected"+musicService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
            Log.d("hint", "service is disconnected");
        }
    };
    private void bindServiceConnection() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, sc, Context.BIND_AUTO_CREATE);
        startService(intent);
    }
    
    public android.os.Handler handler = new android.os.Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(musicService.mp.isPlaying()) {
                musicStatus.setText(getResources().getString(R.string.playing));
                btnPlayOrPause.setText(getResources().getString(R.string.pause).toUpperCase());
            } else {
                musicStatus.setText(getResources().getString(R.string.pause));
                btnPlayOrPause.setText(getResources().getString(R.string.play).toUpperCase());
            }
            musicTime.setText(time.format(musicService.mp.getCurrentPosition()) + "/" + time.format(musicService.mp.getDuration()));
            seekBar.setProgress(musicService.mp.getCurrentPosition());
            handler.postDelayed(runnable, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Log.d("hint", "ready to new MusicService");
//        musicService = new MusicService();
//        Log.d("hint", "finish to new MusicService");
        

        seekBar = (SeekBar)this.findViewById(R.id.MusicSeekBar);
//        seekBar.setProgress(musicService.mp.getCurrentPosition());
//        seekBar.setMax(musicService.mp.getDuration());

        musicStatus = (TextView)this.findViewById(R.id.MusicStatus);
        musicTime = (TextView)this.findViewById(R.id.MusicTime);

        btnPlayOrPause = (Button)this.findViewById(R.id.BtnPlayorPause);
        
        Log.d("hint", Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.d("hint", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath());
        Log.d("hint", Environment.getDataDirectory().getAbsolutePath());
        
        bindServiceConnection();
        
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && musicService != null) {
                    musicService.mp.seekTo(seekBar.getProgress());
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	refreshMusicUi();
    }
    
    private void refreshMusicUi() {
    	if(musicService != null) {
    		if(musicService.mp.isPlaying()) {
                musicStatus.setText(getResources().getString(R.string.playing));
            } else {
                musicStatus.setText(getResources().getString(R.string.pause));
            }
            seekBar.setProgress(musicService.mp.getCurrentPosition());
            seekBar.setMax(musicService.mp.getDuration());
            handler.post(runnable);
            Log.d("hint", "handler post runnable");
    	}
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.BtnPlayorPause:
            	if(musicService != null) {
            		musicService.playOrPause();
            		refreshMusicUi();
            	}
                break;
            case R.id.BtnStop:
            	if(musicService != null) {
            		musicService.stop();
                    seekBar.setProgress(0);
                    handler.removeCallbacks(runnable);
            	}
                break;
            case R.id.BtnQuit:
                handler.removeCallbacks(runnable);
                unbindService(sc);
                try {
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnPre:
            	if(musicService != null) {
            		musicService.preMusic();
            	}
                break;
            case R.id.btnNext:
            	if(musicService != null) {
            		musicService.nextMusic();
            	}
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        unbindService(sc);
        super.onDestroy();
    }

}
