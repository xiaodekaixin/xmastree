package com.dashboard.ble.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import com.dashboard.ble.R;

import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dashboard.ble.service.MusicService;

import java.text.SimpleDateFormat;

public class MusicActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imgBackIcon;
    private TextView chrismasTreeMusic;
    private TextView myMusic;

    private ImageView rotatePicture;
    private ImageView playPicture;
    private ImageView pausePicture;

    private MusicService musicService;
    private SeekBar seekBar;
    private TextView musicStatus, musicTime;
    private Button btnPlayOrPause;
    private SimpleDateFormat time = new SimpleDateFormat("m:ss");

    public android.os.Handler handler = new android.os.Handler();

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicService = ((MusicService.MyBinder)iBinder).getService();
            Log.d("hint", "service is connected"+musicService);
            // 请求权限
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
            Log.d("hint", "service is disconnected");
        }
    };


    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (musicService != null) {
                MediaPlayer player = musicService.mPlayer;
                if (player.isPlaying()) {
                    musicStatus.setText(getResources().getString(R.string.playing));
                    btnPlayOrPause.setText(getResources().getString(R.string.pause).toUpperCase());
                } else {
                    musicStatus.setText(getResources().getString(R.string.pause));
                    btnPlayOrPause.setText(getResources().getString(R.string.play).toUpperCase());
                }
                int currentPosition = player.getCurrentPosition();
                int duration = player.getDuration();
                musicTime.setText(time.format(currentPosition) + "/" + time.format(duration));
                seekBar.setProgress(currentPosition);
                seekBar.setMax(duration);
            }
            handler.postDelayed(runnable, 200);
        }
    };

    @Override
    protected void onCreate() {
        setContentView(R.layout.music_layout);
    }

    @Override
    protected void setupView() {
        chrismasTreeMusic = (TextView) findViewById(R.id.christmasTreeMusic);
        myMusic = (TextView) findViewById(R.id.myMusic);

        findViewById(R.id.tvTitle).setVisibility(View.GONE);
        imgBackIcon = (ImageView) findViewById(R.id.imgBackIcon);
        imgBackIcon.setVisibility(View.VISIBLE);

        seekBar = (SeekBar)this.findViewById(R.id.MusicSeekBar);
        musicStatus = (TextView)this.findViewById(R.id.MusicStatus);
        musicTime = (TextView)this.findViewById(R.id.MusicTime);
        btnPlayOrPause = (Button)this.findViewById(R.id.BtnPlayorPause);

        Log.d("hint", Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.d("hint", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath());
        Log.d("hint", Environment.getDataDirectory().getAbsolutePath());

        bindServiceConnection();
    }

    @Override
    protected void setViewListner() {
        imgBackIcon.setOnClickListener(this);
        chrismasTreeMusic.setOnClickListener(this);
        myMusic.setOnClickListener(this);

        playPicture.setOnClickListener(this);
        pausePicture.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && musicService != null) {
                    musicService.mPlayer.seekTo(seekBar.getProgress());
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
    protected void initViewData() {


    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.imgBackIcon:
                finish();
                break;
            case R.id.christmasTreeMusic:
                break;
            case R.id.myMusic:
                break;
            case R.id.playPicture:
                startRotateAnim();
                break;
            case R.id.pausePicture:
                break;
            case R.id.BtnPlayorPause:
                if (musicService != null) {
                    musicService.playOrPauseMusic();
                }
                initMusicState();
                break;
            case R.id.btnPre:
                if (musicService != null) {
                    musicService.playMusic();
                }
                break;
            case R.id.btnNext:
                if (musicService != null) {
                    musicService.playMusic();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMusicState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        unbindService(sc);
        super.onDestroy();
    }

    private void startRotateAnim() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(rotatePicture, "rotation", 0f, 360f);
        anim.setDuration(5000);
        // 正式开始启动执行动画  
        anim.start();
    }

    private void bindServiceConnection() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, sc, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private void initMusicState() {
        if (musicService != null) {
            seekBar.setProgress(musicService.mPlayer.getCurrentPosition());
            seekBar.setMax(musicService.mPlayer.getDuration());
            handler.post(runnable);
            Log.d("hint", "handler post runnable");
        }
    }
}
