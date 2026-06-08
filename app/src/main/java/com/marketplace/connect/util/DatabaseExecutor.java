package com.marketplace.connect.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DatabaseExecutor {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private DatabaseExecutor() {
    }

    public static void run(Runnable runnable) {
        EXECUTOR.execute(runnable);
    }
}
