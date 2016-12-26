package com.dashboard.ble.activity;

import com.dashboard.ble.R;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class DemoActivity extends Activity implements OnClickListener{

	private VideoView videoView;
    private Button btn_start;
    private Button btn_pause;
    private Button btn_stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_layout);
        bindViews();
    }

    private void bindViews() {
        videoView = (VideoView) findViewById(R.id.videoView);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_stop = (Button) findViewById(R.id.btn_stop);

        btn_start.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_stop.setOnClickListener(this);

        //根据文件路径播放
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            videoView.setVideoPath(Environment.getExternalStorageDirectory() + "/lesson.mp4");
//        }

        //读取放在raw目录下的文件
        videoView.setVideoURI(Uri.parse("android.resource://com.dashboard.ble/" + R.raw.demo));
        videoView.setMediaController(new MediaController(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                videoView.start();
                break;
            case R.id.btn_pause:
                videoView.pause();
                break;
            case R.id.btn_stop:
                videoView.stopPlayback();
                break;
        }
    }

}
