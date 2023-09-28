package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Value("${thread-pools:notice:core}")
    private int noticeCoreSize;
    @Value("${thread-pools:notice:max}")
    private int maxNoticeCoreSize;
    @Value("${thread-pools:notice:alive-time}")
    private int aliveTimeNotice;
    @Value("${thread-pools:notice:queue}")
    private int noticeQueue;
    @Bean
    public ExecutorService noticePool(){
        return new ThreadPoolExecutor(noticeCoreSize, maxNoticeCoreSize, aliveTimeNotice, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(noticeQueue));
    }
}
