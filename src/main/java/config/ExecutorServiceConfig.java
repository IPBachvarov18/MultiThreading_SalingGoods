package config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceConfig {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);
    private ExecutorServiceConfig() {}
    public static ExecutorService getExecutorService() { return EXECUTOR; }
}
