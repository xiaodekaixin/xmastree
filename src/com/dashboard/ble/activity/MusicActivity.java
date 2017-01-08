package com.dashboard.ble.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.SeekBar;
import com.dashboard.ble.R;

import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dashboard.ble.service.MusicService;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

public class MusicActivity extends BaseActivity implements View.OnClickListener, MusicService.OnPlayMusicListener {
    private final int REQUEST_CODE_PICK_MUSIC = 0x02;
    private TextView myMusic;
    private TextView musicFolder;
//    private ImageView rotatePicture;
//    private ImageView playPicture;
//    private ImageView pausePicture;

    private MusicService musicService;
    private SeekBar seekBar;
    private SimpleDateFormat time = new SimpleDateFormat("m:ss");

    private ImageView imgPlay;
    private ImageView imgPause;

    private TextView musicPosition;
    private TextView musicDuration;

    private ImageView christmastPicture;
    private TextView musicName;

    private ObjectAnimator rotateAnimator;

    private String musicFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/demo.mp3";

    public android.os.Handler handler = new android.os.Handler();

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicService = ((MusicService.MyBinder) iBinder).getService();
            Log.d("hint", "service is connected" + musicService);
            // 请求权限
            musicService.setOnPlayMusicListener(MusicActivity.this);
            if (musicService.mPlayer.isPlaying()) {
                showPlayOrPauseBtn(false);
                startRotateAnim();
            }
            handler.post(runnable);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
        }
    };


    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (musicService != null) {
                MediaPlayer player = musicService.mPlayer;
                if (player.isPlaying()) {
                    int currentPosition = player.getCurrentPosition();
                    int duration = player.getDuration();
                    musicPosition.setText(time.format(currentPosition));
                    musicDuration.setText(time.format(duration));
                    seekBar.setProgress(currentPosition);
                    seekBar.setMax(duration);
                    Log.d("hint", "refresh process,position=" + currentPosition + ",duration=" + duration);
                }
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
        findViewById(R.id.tvTitle).setVisibility(View.GONE);

        myMusic = (TextView) findViewById(R.id.myMusic);
        musicFolder = (TextView) findViewById(R.id.musicFolder);

        seekBar = (SeekBar) this.findViewById(R.id.musicSeekBar);
        musicPosition = (TextView) this.findViewById(R.id.musicPosition);
        musicDuration = (TextView) this.findViewById(R.id.musicDuration);

        imgPlay = (ImageView) this.findViewById(R.id.imgPlay);
        imgPause = (ImageView) this.findViewById(R.id.imgPause);

        christmastPicture = (ImageView) findViewById(R.id.christmastPicture);
        musicName = (TextView) findViewById(R.id.musicName);

        bindServiceConnection();
    }

    @Override
    protected void setViewListner() {
        myMusic.setOnClickListener(this);
        musicFolder.setOnClickListener(this);

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
            case R.id.myMusic:
                playMusicWithSystemPlayer();
                break;
            case R.id.musicFolder:
                selectMusicFromPhone();
                break;
            case R.id.imgPause:
                if (musicService != null) {
                    musicService.pauseMusic();
                    showPlayOrPauseBtn(true);
                    stopRotateAnim();
                }
                break;
            case R.id.imgPlay:
                if (musicService != null) {
                    musicService.playMusic();
                    showPlayOrPauseBtn(false);
                    startRotateAnim();
                }
                break;
            case R.id.imgPrev:
                if (musicService != null) {
                    musicService.startPlayMusic();
                }
                break;
            case R.id.imgNext:
                if (musicService != null) {
                    musicService.startPlayMusic();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable);
        Log.d("hint", "onresume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("hint", "onstart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    private void startRotateAnim() {
        rotateAnimator = ObjectAnimator.ofFloat(christmastPicture, "rotation", 0f, 360f);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.setDuration(5000);
        rotateAnimator.setRepeatCount(-1);
        rotateAnimator.start();
    }

	private void stopRotateAnim() {
		if (rotateAnimator != null) {
			rotateAnimator.end();
		}
	}

    private void bindServiceConnection() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private void initMusicState() {
        if (musicService != null) {
            int currentPosition = musicService.mPlayer.getCurrentPosition();
            int duration = musicService.mPlayer.getDuration();
            Log.d("hint", "handler post runnable,position=" + currentPosition + ",duration=" + duration);
        }
        handler.post(runnable);
    }

    @Override
    public void onCompletion() {
        // 音乐播放完毕
        showPlayOrPauseBtn(true);
        stopRotateAnim();
    }

    private void showPlayOrPauseBtn(boolean showPlayBtn) {
        if (showPlayBtn) {
            imgPlay.setVisibility(View.VISIBLE);
            imgPause.setVisibility(View.GONE);
        } else {
            imgPlay.setVisibility(View.GONE);
            imgPause.setVisibility(View.VISIBLE);
        }
    }

    private void playMusicWithSystemPlayer() {
        try {
            Uri uri = Uri.parse("file://" + musicFilePath);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "audio/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("hint", "play music,path:" + e.getMessage());
        }
    }

    private void selectMusicFromPhone() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        Intent wrapperIntent = Intent.createChooser(intent, null);
        startActivityForResult(wrapperIntent, REQUEST_CODE_PICK_MUSIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_MUSIC) {
            Log.d("hint", "return data = " + data);
            if (data != null) {
                Uri uri = data.getData();
                String scheme = uri.getScheme();
                Log.d("hint", "default,scheme=" + scheme);
                String musicPath = uri.getPath();
                if (scheme.equals("file")) {
                    musicPath = uri.toString().substring("file://".length());
                } else if (scheme.equals("content")) {
                	// 获取本地音乐文件列表
					String[] projection = { MediaStore.Audio.Media.DATA };
                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    cursor.moveToFirst();
                    musicPath = cursor.getString(columnIndex);
                }
                Log.d("hint", "musicPath=" + musicPath);

                musicFilePath = musicPath;
                if (musicService != null) {
                    int pos = musicPath.lastIndexOf("/");
                    String fileName = musicPath.substring(pos + 1);
                    if (musicName != null) {
                        musicName.setText(fileName);
                    }

                    musicService.setMusicFilePath(musicFilePath);
                    imgPlay.performClick();
                }
            }
        }
    }
}
