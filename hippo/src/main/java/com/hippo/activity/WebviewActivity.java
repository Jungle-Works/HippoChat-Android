package com.hippo.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hippo.HippoColorConfig;
import com.hippo.R;
import com.hippo.database.CommonData;

/**
 * Created by gurmail on 2019-10-18.
 *
 * @author gurmail
 */
public class WebviewActivity extends FuguBaseActivity {

    private RelativeLayout rootToolbar;
    private ImageView ivBackBtn;
    private TextView tvToolbarName;

    private ProgressBar pbWebPageLoader;
    private WebView webView;

    private HippoColorConfig hippoColorConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");

        rootToolbar = findViewById(R.id.my_toolbar);
        tvToolbarName = findViewById(R.id.tv_toolbar_name);
        ivBackBtn = findViewById(R.id.ivBackBtn);

        hippoColorConfig = CommonData.getColorConfig();
        rootToolbar.setBackgroundColor(hippoColorConfig.getHippoActionBarBg());
        tvToolbarName.setTextColor(hippoColorConfig.getHippoActionBarText());
        tvToolbarName.setText(title);

        pbWebPageLoader = findViewById(R.id.pbWebPageLoader);
        webView = findViewById(R.id.webView);


        WebSettings settings = webView.getSettings();
//        settings.domStorageEnabled = true
//        settings.javaScriptEnabled = true

        settings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //webView.addJavascriptInterface(JavaScriptInterface(this), "Android");
        settings.setPluginState(WebSettings.PluginState.ON);

        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(url);

    }

    public void onClick(View v) {
        if(v.getId() == R.id.ivBackBtn) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            try {
                if (pbWebPageLoader != null)
                    pbWebPageLoader.setVisibility(View.INVISIBLE);
            } catch (Exception e) {

            }
        }
    }
}
