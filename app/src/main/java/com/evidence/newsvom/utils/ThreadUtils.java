package com.evidence.newsvom.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

public class ThreadUtils {

    // Suppresses default constructor, ensuring non-instantiability.
    private ThreadUtils() {
    }
    private static Handler handler = new Handler(Looper.getMainLooper());

    private static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void runOnMain(final @NonNull Runnable runnable) {
        if (isMainThread()) runnable.run();
        else handler.post(runnable);
    }

}
