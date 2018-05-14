package com.evidence.newsvom.ui.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.evidence.newsvom.R;
import com.evidence.newsvom.interfaces.DoneCallBack;
import com.evidence.newsvom.interfaces.EndlessRecyclerViewScrollListner;
import com.evidence.newsvom.models.Headline;
import com.evidence.newsvom.networking.NewsApiClient;
import com.evidence.newsvom.ui.adapters.HeadLinesAdapter;
import com.evidence.newsvom.ui.widgets.stickyrecyclerheaders.rendering.StickyRecyclerHeadersDecoration;
import com.evidence.newsvom.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.loading_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.swipe_to_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.news_feed_recylcer_view)
    RecyclerView newsFeedRecyclerView;

    private HeadLinesAdapter headLinesAdapter;
    private List<Headline> headlines = new ArrayList<>();

    private int PAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpHeadLinesAdapter();
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                headlines.clear();
                loadNewsHeadlines();
            }
        });
        loadNewsHeadlines();
    }

    private void loadNewsHeadlines() {
        NewsApiClient.fetchNewsHeadlines(PAGE, new DoneCallBack<List<Headline>>() {
            @Override
            public void done(List<Headline> result, Exception e) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (e == null && result != null && !result.isEmpty()) {
                    headlines.addAll(result);
                    headLinesAdapter.notifyDataSetChanged();
                    PAGE++;
                }
            }
        });
    }

    private void setUpHeadLinesAdapter() {
        headLinesAdapter = new HeadLinesAdapter(this, headlines);
        LinearLayoutManager verticalLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        newsFeedRecyclerView.setLayoutManager(verticalLinearLayoutManager);
        newsFeedRecyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(headLinesAdapter));
        newsFeedRecyclerView.setAdapter(headLinesAdapter);
        newsFeedRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListner(verticalLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!headlines.isEmpty() && headlines.size() >= 20) {
                    loadNewsHeadlines();
                }
            }
        });
    }

}
