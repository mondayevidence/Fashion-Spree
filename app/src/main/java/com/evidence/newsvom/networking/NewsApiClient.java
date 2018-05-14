package com.evidence.newsvom.networking;

import android.support.annotation.NonNull;

import com.evidence.newsvom.interfaces.DoneCallBack;
import com.evidence.newsvom.models.Headline;
import com.evidence.newsvom.utils.NewsConstants;
import com.evidence.newsvom.utils.NewsLogger;
import com.evidence.newsvom.utils.ThreadUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

@SuppressWarnings({"ConstantConditions", "unchecked"})
public class NewsApiClient {

    private static String BASE_URL = "https://newsapi.org/v2";
    private static String EVERYTHING_PATH = "everything";

    private static OkHttpClient.Builder getOkHttpClientBuilder() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                NewsLogger.d("NewsLoggerNetworkCallLogger", message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000, TimeUnit.MILLISECONDS)
                .addInterceptor(logging);
    }

    private static void callBackOnMainThread(final DoneCallBack doneCallback, final Object result, final Exception e) {
        ThreadUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                if (e != null) {
                    if (e.getMessage() != null) {
                        if (StringUtils.containsIgnoreCase(e.getMessage(), "host") || StringUtils.containsIgnoreCase(e.getMessage(), "ssl")) {
                            doneCallback.done(result, new Exception("Network Error! Please review your data connection and try again"));
                        } else {
                            NewsLogger.d("MajorException", e.getMessage());
                            doneCallback.done(result, e);
                        }
                    } else {
                        doneCallback.done(result, new Exception("Error in operation!"));
                    }
                } else {
                    doneCallback.done(result, null);
                }
            }
        });
    }

    private static OkHttpClient getOkHttpClient() {
        return getOkHttpClientBuilder().build();
    }

    private static Request.Builder getRequestBuilder(HashMap<String, String> headers) {
        final Request.Builder requestBuilder = new Request.Builder();
        if (headers != null) {
            for (String key : headers.keySet()) {
                requestBuilder.addHeader(key, headers.get(key));
            }
        }
        return requestBuilder;
    }

    public static void fetchNewsHeadlines(int page, final DoneCallBack<List<Headline>> headlinesFetchDoneCallBack) {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(BASE_URL + "/" + EVERYTHING_PATH).newBuilder();
        httpUrlBuilder.addQueryParameter("q", "fashion");
        httpUrlBuilder.addQueryParameter("page", String.valueOf(page));
        attachApiKey(httpUrlBuilder);
        String topHeadlinesUrl = httpUrlBuilder.build().toString();
        Request topHeadlinesRequest = getRequestBuilder(null).url(topHeadlinesUrl).get().build();
        getOkHttpClient().newCall(topHeadlinesRequest).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callBackOnMainThread(headlinesFetchDoneCallBack, null, spitException(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseString = response.body().string();
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseProps = new JSONObject(responseString);
                        String status = responseProps.optString("status");
                        if (status.equalsIgnoreCase("ok")) {
                            JSONArray articles = responseProps.optJSONArray("articles");
                            if (articles != null && articles.length() > 0) {
                                List<Headline> results = new ArrayList<>();
                                for (int i = 0; i < articles.length(); i++) {
                                    JSONObject articleAtPoint = articles.optJSONObject(i);
                                    Headline headline = constructHeadLineFromJsonObject(articleAtPoint);
                                    results.add(headline);
                                }
                                callBackOnMainThread(headlinesFetchDoneCallBack, results, null);
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callBackOnMainThread(headlinesFetchDoneCallBack, null, spitException("Error loading news headlines.Please try later"));
            }

        });

    }

    private static Headline constructHeadLineFromJsonObject(JSONObject articleAtPoint) {
        Headline headline = new Headline();
        headline.setAuthor(articleAtPoint.optString("author"));
        headline.setTitle(articleAtPoint.optString("title"));
        headline.setDescription(articleAtPoint.optString("description"));
        headline.setUrl(articleAtPoint.optString("url"));
        headline.setUrlToImage(articleAtPoint.optString("urlToImage"));
        headline.setPublishedDate(articleAtPoint.optString("publishedAt"));
        return headline;
    }

    private static Exception spitException(IOException e) {
        return e;
    }

    private static Exception spitException(String errorMessage) {
        return new Exception(errorMessage);
    }

    private static void attachApiKey(HttpUrl.Builder httpUrlBuilder) {
        httpUrlBuilder.addQueryParameter("apiKey", NewsConstants.NEWS_API_KEY);
    }

}