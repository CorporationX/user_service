package school.faang.user_service.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class NotificationPublisherExecutor {

    @Value("${premium.updater.thread-pool}")
    private int fixedThreadPool;

    @Bean
    public ExecutorService notificationEventExecutor() {
        return Executors.newFixedThreadPool(fixedThreadPool);
    }
}
