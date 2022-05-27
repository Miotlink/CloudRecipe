package com.miot.android;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.miot.android.listener.JSInterface;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements JSInterface{

    private static final String JS_INTERFACE_NAME="miotlink_cloud_menu_js";
    private FrameLayout mViewParent=null;

    private WebView mWebView=null;

    private ProgressBar mProgressBar;
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressBar=new ProgressBar(this);
        mViewParent=new FrameLayout(this);
        LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(params);
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        mViewParent.setLayoutParams(layoutParams);
        linearLayout.addView(mViewParent);
        setContentView(linearLayout);
        initView();
        initViewWebSettings();
//        String path = "file://" + Environment.getExternalStorageDirectory()  + "/miotlink/854/" + "854_index.html";
        String path="https://dev.51miaomiao.cn/open-cloud-iot-statics/iotShare/devices/iotControl.html?accessKey=ml001&Authorization=bearer%2093ceef328c67e7ed87da839aa184f67c";
        mWebView.loadUrl(path);
        scheduledExecutorService =  Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                String result = "{\"code\":\"additionalInfoRes\",\"data\":{\"function\":\"scanQrcodeRes\",\"requestResult\":\"" + 1 + "\"}}";
                Log.e("run", result);
                setCloudMenuCommands(result);
            }
        },5,5, TimeUnit.SECONDS);

    }

    private void initView(){
        mWebView = new WebView(this);
        mViewParent.addView(mWebView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scheduledExecutorService!=null){
            scheduledExecutorService.shutdown();
        }
    }

    @SuppressLint("JavascriptInterface")
    private void initViewWebSettings() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("UTF-8");//设置编码格式
        mWebView.addJavascriptInterface(this, JS_INTERFACE_NAME);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mProgressBar.setProgress(newProgress);
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            // 链接跳转都会走这个方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);// 强制在当前 WebView 中加载 url
                return true;
            }
        });
    }

    @Override
    @JavascriptInterface
    public String getCloudMenuInfos(String value) {
        Log.e("js", value);
        return null;
    }

    @Override
    public String setCloudMenuCommands(final String params) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:setCloudMenuCommands('" + params + "')");
                }
            });
        } catch (Exception e) {
        }

        return null;
    }


}