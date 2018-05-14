package com.evidence.newsvom.ui.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.evidence.newsvom.R;
import com.evidence.newsvom.models.Headline;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsDetailsActivity extends AppCompatActivity {

    @BindView(R.id.news_feed_webview)
    WebView webView;

    @BindView(R.id.adView)
    AdView adView;

    @BindView(R.id.loading_progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);
        MobileAds.initialize(this, "ca-app-pub-2152296235772748~1192049631");

        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            Headline headline = intentExtras.getParcelable("news");
            if (headline != null) {
                String headLineUrl = headline.getUrl();
                String newsTitle = headline.getTitle();

                if (StringUtils.isNotEmpty(headLineUrl)) {
                    loadNews(headLineUrl);
                }

                if (StringUtils.isNotEmpty(newsTitle) && getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(newsTitle);
                }
            }
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            webView.stopLoading();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadNews(String headLineUrl) {
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        };
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(headLineUrl);
    }

}
