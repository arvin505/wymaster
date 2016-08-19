package com.miqtech.master.client.ui;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.WebInterface;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/21.
 */
public class DownloadWebView extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.subWebview)
    WebView webView;
    @Bind(R.id.viewSpecial)
    View specialView;

    private String url;
    private String title;
    private String userId;
    private String token;
    User user = WangYuApplication.getUser(this);

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.warinfo_list);
        ButterKnife.bind(this);
        url = getIntent().getStringExtra("download_url");
        title = getIntent().getStringExtra("title");

        if (user != null) {
            userId = user.getId();
            token = user.getToken();
        }

        initView();
        setLeftIncludeTitle(title);
        loadHtml();
    }

    @Override
    protected void initView() {
        super.initView();
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);

    }

    private void loadHtml() {
        loadWebView(url);
    }

    private void loadWebView(String mUrl) {
        WebSettings setting = webView.getSettings();
        WebInterface jsInterface = new WebInterface(this, webView);
        webView.addJavascriptInterface(jsInterface, "browser");
        setting.setJavaScriptEnabled(true);
       /* if (type == DOWNLOADGAME) {*/
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        /*} else {
            webView.setWebViewClient(new MyViewClient());
        }*/
        webView.setDownloadListener(new MyWebViewDownLoadListener());

        webView.setWebChromeClient(wvcc);
        webView.loadUrl(mUrl); // 加载指定网址
        specialView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    WebChromeClient wvcc = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (title.equals("-1")) {
                Intent intent = new Intent();
                intent.setClass(DownloadWebView.this, LoginActivity.class);
                startActivity(intent);
                finish();
                showToast("用户信息失效，请重新登陆");
            }
        }
    };

    private class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
        }
    }
}
