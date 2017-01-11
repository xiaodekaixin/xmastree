package com.dashboard.ble.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dashboard.ble.R;

public class WebviewActivity extends Activity {
	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_layout);
		
		initView();
		
		initWebview();
	}
	
	private void initView() {  
        webView = (WebView)findViewById(R.id.webview);  
    }
	
	private void initWebview() {
		if (Build.VERSION.SDK_INT >= 19) {  
            webView.getSettings().setCacheMode(  
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);  
        }  
        webView.setWebViewClient(new GameWebViewClient());  
        webView.setWebChromeClient(new WebChromeClient() {
        	 @Override  
             public void onShowCustomView(View view, CustomViewCallback callback) {  
                 super.onShowCustomView(view, callback);  
             }
        });
        webView.setBackgroundColor(Color.TRANSPARENT);
        
        WebSettings settings = webView.getSettings();  
        settings.setJavaScriptEnabled(true); 
        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        
        webView.loadUrl("file:///android_asset/video.html");  
	}
	
	class GameWebViewClient extends WebViewClient {  
        @Override  
        public boolean shouldOverrideUrlLoading(WebView view,  
                String url_Turntable) {  
            view.loadUrl(url_Turntable);  
            return true;  
        }  
  
        @Override  
        public void onReceivedSslError(WebView view, SslErrorHandler handler,  
                SslError error) {  
            handler.proceed();  
        }  
  
        @Override  
        public void onPageStarted(WebView view, String url, Bitmap favicon) { 
        	super.onPageStarted(view, url, favicon);
        }  
  
        @Override  
        public void onPageFinished(WebView view, String url) {  
        	super.onPageFinished(view, url);
        	view.loadUrl("javascript:try{autoplay();}catch(e){}");//播放视频
        }  
  
    }  
}
