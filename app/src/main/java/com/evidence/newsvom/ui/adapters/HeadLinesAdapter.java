package com.evidence.newsvom.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.evidence.newsvom.R;
import com.evidence.newsvom.models.Headline;
import com.evidence.newsvom.ui.activities.NewsDetailsActivity;
import com.evidence.newsvom.ui.widgets.LoadingImageView;
import com.evidence.newsvom.ui.widgets.stickyrecyclerheaders.StickyRecyclerHeadersAdapter;
import com.evidence.newsvom.utils.NewsConstants;
import com.evidence.newsvom.utils.UiUtils;

import org.joda.time.Instant;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HeadLinesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        StickyRecyclerHeadersAdapter<HeadLinesAdapter.NewsDateHeadersViewHolder> {

    private Context context;
    private List<Headline> headlines;
    private LayoutInflater layoutInflater;

    public HeadLinesAdapter(Context context, List<Headline> headlines) {
        this.context = context;
        this.headlines = headlines;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.news_cell, parent, false);
        return new HeadLineItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HeadLineItemViewHolder headLineItemViewHolder = (HeadLineItemViewHolder) holder;
        Headline headline = headlines.get(position);
        headLineItemViewHolder.bindData(context, headline);
    }

    @Override
    public String getHeaderId(int position) {
        return UiUtils.getDate(headlines.get(position));
    }

    @Override
    public NewsDateHeadersViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View headerView = layoutInflater.inflate(R.layout.news_date_headers, parent, false);
        return new NewsDateHeadersViewHolder(headerView);
    }

    @Override
    public void onBindHeaderViewHolder(NewsDateHeadersViewHolder holder, int position) {
        holder.bindHeaders(UiUtils.getDate(headlines.get(position)));
    }

    @Override
    public int getItemCount() {
        return headlines.size();
    }

    static class HeadLineItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.news_image)
        LoadingImageView headLineImageView;

        @BindView(R.id.news_title)
        TextView newsTitleView;

        @BindView(R.id.news_description)
        TextView headLineDescriptionView;

        @BindView(R.id.news_time_stamp)
        TextView headLineTimeStampView;

        @BindView(R.id.parent_view)
        View parentView;

        HeadLineItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(final Context context, final Headline headLine) {

            String headLineTitle = headLine.getTitle();
            String headLineDescription = headLine.getDescription();
            String headLineImageUrl = headLine.getUrlToImage();
            String headLineDate = headLine.getPublishedDate();

            newsTitleView.setText(headLineTitle);
            headLineDescriptionView.setText(headLineDescription);
            UiUtils.loadImage(headLineImageUrl, headLineImageView);
            Instant instant = Instant.parse(headLineDate);
            headLineTimeStampView.setText(NewsConstants.DATE_FORMATTER_IN_12HRS.format(new Date(instant.getMillis())));

            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UiUtils.blinkView(v);
                    Intent intent = new Intent(context, NewsDetailsActivity.class);
                    intent.putExtra("news", headLine);
                    context.startActivity(intent);
                }
            });
        }

    }

    static class NewsDateHeadersViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.news_date)
        TextView newsDateView;

        NewsDateHeadersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindHeaders(String date) {
            newsDateView.setText(date);
        }

    }

}
