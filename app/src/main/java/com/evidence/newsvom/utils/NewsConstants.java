package com.evidence.newsvom.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NewsConstants {

    public static final String NEWS_API_KEY = "d7e978fb0f4045b0bcfe2dbc46f70420";

    public static final SimpleDateFormat DATE_FORMATTER_IN_12HRS = new SimpleDateFormat("h:mm a", Locale.getDefault());
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMATTER_IN_BIRTHDAY_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
}
