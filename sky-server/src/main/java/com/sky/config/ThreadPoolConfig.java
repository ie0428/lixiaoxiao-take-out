package com.sky.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Configuration
//@RefreshScope
public class ThreadPoolConfig {
    

    @Bean(name = "boundedThreadPool")
    public ThreadPoolExecutor boundedThreadPool() {
        return new ThreadPoolExecutor(
            10, 100, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}